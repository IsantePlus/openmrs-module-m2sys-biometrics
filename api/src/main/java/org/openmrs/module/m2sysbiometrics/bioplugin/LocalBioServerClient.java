package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalBioServerClient extends AbstractBioServerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalBioServerClient.class);

    @Override
    protected String getServerUrlPropertyName() {
        return M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL;
    }

    @Override
    protected Object getResponse(Object requestPayload) {
        return getWebServiceTemplate().marshalSendAndReceive(getServiceUrl(), requestPayload);
    }

    public void handleRegistrationError(BiometricSubject subject, String responseValue, Patient patient) {
        if (patient == null) {
            LOGGER.info("No patient matching fingerprint ID: {}", responseValue);

            String isRegisterResponse = isRegistered(responseValue);
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

                    changeId(responseValue, subject.getSubjectId());
                } else {
                    LOGGER.info("User already has the same fingerprints registered under his fingerprint ID");
                }
            }
        } else {
            throw new M2SysBiometricsException("Fingerprints already match patient: "
                    + patient.getPersonName().getFullName());
        }
    }
}

