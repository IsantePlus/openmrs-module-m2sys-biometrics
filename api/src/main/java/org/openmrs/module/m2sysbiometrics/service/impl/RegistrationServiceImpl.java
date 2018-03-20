package org.openmrs.module.m2sysbiometrics.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.module.m2sysbiometrics.util.NationalUuidGenerator;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    private PatientHelper patientHelper;

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private RegistrationCoreService registrationCoreService;

    @Autowired
    private M2SysProperties properties;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private NationalUuidGenerator nationalUuidGenerator;

    @Autowired
    private NationalSynchronizationFailureService nationalSynchronizationFailureService;

    @Override
    public void registerLocally(BiometricSubject subject, M2SysCaptureResponse capture) {
        String response = localBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isRegisterSuccess()) {
            String responseValue = results.firstValue();
            LOGGER.info("Got error response from the local server: {}. Checking if tied to patient.", responseValue);
            Patient patient = patientHelper.findByLocalFpId(responseValue);
            handleLocalRegistrationError(subject, responseValue, patient);
        }
    }

    @Override
    public void registerNationally(String nationalId, M2SysCaptureResponse capture) {
        try {
            String response = nationalBioServerClient.enroll(nationalId, capture.getTemplateData());
            M2SysResults results = XmlResultUtil.parse(response);
            if (!results.isRegisterSuccess()) {
                throw new M2SysBiometricsException("National registration failed");
            }
        } catch (Exception e) {
            LOGGER.error("Registration with the national fingerprint server failed.", e);
            handleNationalRegistrationError(nationalId, capture);
        }
    }

    @Override
    public void fetchFromMpiByNationalFpId(BiometricSubject nationalBiometricSubject, M2SysCaptureResponse fingerScan) {
        registrationCoreService.importMpiPatient(nationalBiometricSubject.getSubjectId(),
                getNationalPatientIdentifierTypeUuid());
        registerLocally(nationalBiometricSubject, fingerScan);
    }

    @Override
    public void synchronizeFingerprints(M2SysCaptureResponse fingerScan, FingerScanStatus fingerScanStatus) {
        if (fingerScanStatus.isRegisteredNationally()) {
            String nationalId = fingerScanStatus.getNationalBiometricSubject().getSubjectId();
            if (patientHelper.findByNationalFpId(nationalId) == null) {
                String localId = fingerScanStatus.getLocalBiometricSubject().getSubjectId();
                Patient patient = patientHelper.findByLocalFpId(localId);
                attachNationalIdToThePatient(patient, nationalId);
            }
        } else {
            String nationalId = nationalUuidGenerator.generate();
            registerNationally(nationalId, fingerScan);
        }
    }

    private void handleLocalRegistrationError(BiometricSubject subject, String responseValue, Patient patient) {
        if (patient == null) {
            LOGGER.info("No patient matching fingerprint ID: {}", responseValue);

            String isRegisterResponse = localBioServerClient.isRegistered(responseValue);
            M2SysResults isRegisterResults = XmlResultUtil.parse(isRegisterResponse);

            if (isRegisterResults.isLookupNotFound()) {
                throw new M2SysBiometricsException("No success during fingerprint registration: "
                        + responseValue);
            } else {
                LOGGER.info("Fingerprints are registered with ID {} but do not match any patient, fixing.",
                        responseValue);
                if (!StringUtils.equals(subject.getSubjectId(), responseValue)) {
                    LOGGER.info("Changing existing fingerprint ID {} to {}",
                            responseValue, subject.getSubjectId());

                    localBioServerClient.changeId(responseValue, subject.getSubjectId());
                } else {
                    LOGGER.info("User already has the same fingerprints registered under his fingerprint ID");
                }
            }
        } else {
            throw new M2SysBiometricsException("Fingerprints already match patient: "
                    + patient.getPersonName().getFullName());
        }
    }

    private void handleNationalRegistrationError(String nationalId, M2SysCaptureResponse fingerScan) {
        NationalSynchronizationFailure nationalSynchronizationFailure =
                new NationalSynchronizationFailure(nationalId, fingerScan.getTemplateData(), false);

        nationalSynchronizationFailureService.save(nationalSynchronizationFailure);
    }

    private void attachNationalIdToThePatient(Patient patient, String nationalId) {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(
                properties.getGlobalProperty(RegistrationCoreConstants.GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID));
        Location location = locationService.getDefaultLocation();
        PatientIdentifier nationalIdentifier = new PatientIdentifier(nationalId, patientIdentifierType, location);
        patient.addIdentifier(nationalIdentifier);
        patientService.savePatient(patient);
    }

    private String getNationalPatientIdentifierTypeUuid() {
        return properties.getGlobalProperty(RegistrationCoreConstants.GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID);
    }
}
