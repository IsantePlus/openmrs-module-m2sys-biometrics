package org.openmrs.module.m2sysbiometrics.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.module.m2sysbiometrics.util.NationalUuidGenerator;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
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
    private NationalUuidGenerator nationalUuidGenerator;

    @Autowired
    private NationalSynchronizationFailureService nationalSynchronizationFailureService;

    @Autowired
    private SearchService searchService;

    @Override
    public BiometricSubject registerLocally(BiometricSubject subject, M2SysCaptureResponse fingerScan) {
        BiometricSubject setSubjectId = subject;
        String response = localBioServerClient.enroll(subject.getSubjectId(), fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isRegisterSuccess()) {
            String existingInLocalFpSubjectId = results.firstValue();
            LOGGER.info("Got error response from the local server: {}. Checking if tied to patient.",
                    existingInLocalFpSubjectId);
            Patient patient = patientHelper.findByLocalFpId(existingInLocalFpSubjectId);
            setSubjectId = handleLocalRegistrationError(subject, existingInLocalFpSubjectId, patient, fingerScan);
        }
        return setSubjectId;
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
        String nationalId = nationalBiometricSubject.getSubjectId();
        registrationCoreService.importMpiPatient(nationalId, properties.getNationalPatientIdentifierTypeUuid());

        Patient patient = patientHelper.findByNationalFpId(nationalId);
        if (patient == null) {
            throw new M2SysBiometricsException(String.format(
                    "Error during fetching patient from MPI with national fingerprint ID %s", nationalId));
        }

        BiometricSubject setLocalSubjectId = registerLocally(nationalBiometricSubject, fingerScan);
        if (StringUtils.equals(setLocalSubjectId.getSubjectId(), nationalBiometricSubject.getSubjectId())) {
            patientHelper.attachLocalIdToThePatient(patient, nationalId);
        }
    }

    @Override
    public void synchronizeFingerprints(M2SysCaptureResponse fingerScan, FingerScanStatus fingerScanStatus) {
        if (fingerScanStatus.isRegisteredNationally()) {
            String nationalId = fingerScanStatus.getNationalBiometricSubject().getSubjectId();
            if (patientHelper.findByNationalFpId(nationalId) == null) {
                String localId = fingerScanStatus.getLocalBiometricSubject().getSubjectId();
                Patient patient = patientHelper.findByLocalFpId(localId);
                patientHelper.attachNationalIdToThePatient(patient, nationalId);
            }
        } else {
            String nationalId = nationalUuidGenerator.generate();
            registerNationally(nationalId, fingerScan);
        }
    }

    private BiometricSubject handleLocalRegistrationError(BiometricSubject expectedSubjectId,
            String existingInLocalFpSubjectId, Patient patientWithLocalFpSubjectId, M2SysCaptureResponse fingerScan) {
        if (patientWithLocalFpSubjectId != null) {
            throw new M2SysBiometricsException("Fingerprints already match patient: "
                    + patientWithLocalFpSubjectId.getPersonName().getFullName());
        } else {
            LOGGER.info("No patient matching fingerprint ID: {}", existingInLocalFpSubjectId);

            String isRegisterResponse = localBioServerClient.isRegistered(existingInLocalFpSubjectId);
            M2SysResults isRegisterResults = XmlResultUtil.parse(isRegisterResponse);

            if (isRegisterResults.isLookupNotFound()) {
                throw new M2SysBiometricsException("No success during fingerprint registration: "
                        + existingInLocalFpSubjectId);
            } else {
                LOGGER.info("Fingerprints are registered with ID {} but do not match any patient, fixing.",
                        existingInLocalFpSubjectId);
                return fixLocalFpIdDoesNotMatchingPatient(expectedSubjectId, existingInLocalFpSubjectId, fingerScan);
            }
        }
    }

    private BiometricSubject fixLocalFpIdDoesNotMatchingPatient(BiometricSubject expectedSubjectId,
            String existingInLocalFpSubjectId, M2SysCaptureResponse fingerScan) {
        BiometricSubject setSubjectId;
        boolean success = handleLocalRegistrationErrorWithNationalFp(existingInLocalFpSubjectId, fingerScan);
        if (success) {
            setSubjectId = new BiometricSubject(existingInLocalFpSubjectId);
        } else {
            handleLocalRegistrationErrorWithReplacingIdInLocalFp(expectedSubjectId, existingInLocalFpSubjectId);
            setSubjectId = expectedSubjectId;
        }
        return setSubjectId;
    }

    private boolean handleLocalRegistrationErrorWithNationalFp(String existingInLocalFpSubjectId,
            M2SysCaptureResponse fingerScan) {
        boolean success = false;
        if (nationalBioServerClient.isServerUrlConfigured()) {
            LOGGER.info("Trying to find patient on national level");
            BiometricSubject nationalSubject = searchService.findMostAdequateSubjectNationally(fingerScan);
            if (nationalSubject != null) {
                LOGGER.info("Patient found on national level with national subject id {}", nationalSubject.getSubjectId());
                Patient localPatientWithNationalId = patientHelper.findByNationalFpId(nationalSubject.getSubjectId());
                if (localPatientWithNationalId != null) {
                    LOGGER.info("Patient presents on national level is also available in local instance, "
                            + "overwriting its local subject id type", nationalSubject.getSubjectId());
                    try {
                        patientHelper.changeLocalFpId(localPatientWithNationalId, existingInLocalFpSubjectId);
                        success = true;
                    } catch (Exception ex) {
                        success = false;
                    }
                }
            }
        }
        return success;
    }

    private void handleLocalRegistrationErrorWithReplacingIdInLocalFp(BiometricSubject expectedSubjectId,
            String existingInLocalFpSubjectId) {
        if (!StringUtils.equals(expectedSubjectId.getSubjectId(), existingInLocalFpSubjectId)) {
            LOGGER.info("Changing existing fingerprint ID {} to {}",
                    existingInLocalFpSubjectId, expectedSubjectId.getSubjectId());
            localBioServerClient.changeId(existingInLocalFpSubjectId, expectedSubjectId.getSubjectId());
        } else {
            LOGGER.info("User already has the same fingerprints registered under his fingerprint ID");
        }
    }

    private void handleNationalRegistrationError(String nationalId, M2SysCaptureResponse fingerScan) {
        NationalSynchronizationFailure nationalSynchronizationFailure =
                new NationalSynchronizationFailure(nationalId, fingerScan.getTemplateData(), false);

        nationalSynchronizationFailureService.save(nationalSynchronizationFailure);
    }
}
