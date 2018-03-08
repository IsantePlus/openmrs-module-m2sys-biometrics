package org.openmrs.module.m2sysbiometrics.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.m2sysbiometrics.bioplugin.AbstractBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
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

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    private PatientHelper patientHelper;

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private RegistrationCoreService registrationCoreService;

    @Override
    public void registerLocally(BiometricSubject subject, M2SysCaptureResponse capture) {
        String response = localBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isRegisterSuccess()) {
            String responseValue = results.firstValue();
            LOG.info("Got error response from the local server: {}. Checking if tied to patient.", responseValue);
            Patient patient = patientHelper.findByLocalFpId(responseValue);
            handleRegistrationError(subject, results, patient, localBioServerClient);
        }
    }

    @Override
    public void registerNationally(BiometricSubject subject, M2SysCaptureResponse capture) {
        try {
            String response = nationalBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData());
            M2SysResults results = XmlResultUtil.parse(response);

            if (!results.isRegisterSuccess()) {
                LOG.error("Registration with the national fingerprint server failed.");
            }
        } catch (RuntimeException exception) {
            LOG.error("Registration with the national fingerprint server failed.", exception);
        }
    }

    @Override
    public void fetchFromNational(BiometricSubject nationalBiometricSubject) {
        registrationCoreService.importMpiPatient(nationalBiometricSubject.getSubjectId(),
                RegistrationCoreConstants.GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID);
    }

    private void handleRegistrationError(BiometricSubject subject, M2SysResults results, Patient patient,
                                         AbstractBioServerClient client) {
        if (patient == null) {
            String responseValue = results.firstValue();
            LOG.info("No patient matching fingerprint ID: {}", responseValue);

            String isRegisterResponse = client.isRegistered(results.firstValue());
            M2SysResults isRegisterResults = XmlResultUtil.parse(isRegisterResponse);

            if (isRegisterResults.isLookupNotFound()) {
                throw new M2SysBiometricsException("No success during fingerprint registration: "
                        + responseValue);
            } else {
                LOG.info("Fingerprints are registered with ID {} but do not match any patient, fixing.",
                        responseValue);
                if (!StringUtils.equals(subject.getSubjectId(), responseValue)) {
                    LOG.info("Changing existing fingerprint ID {} to {}",
                            responseValue, subject.getSubjectId());

                    client.changeId(responseValue, subject.getSubjectId());
                } else {
                    LOG.info("User already has the same fingerprints registered under his fingerprint ID");
                }
            }
        } else {
            throw new M2SysBiometricsException("Fingerprints already match patient: "
                    + patient.getPersonName().getFullName());
        }
    }
}
