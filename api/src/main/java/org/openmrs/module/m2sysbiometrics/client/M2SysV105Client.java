package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.bioplugin.BioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.Fingers;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class M2SysV105Client extends AbstractM2SysClient {

    @Autowired
    private BioServerClient bioServerClient;

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        Fingers fingers = scanDoubleFingers();

        bioServerClient.enroll(getLocalBioServerUrl(), subject.getSubjectId(),
                getLocationID(), fingers.getLeftFingerData(), fingers.getRightFingerData());

        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        Fingers fingers = scanDoubleFingers();

        bioServerClient.enroll(getLocalBioServerUrl(), subject.getSubjectId(),
                getLocationID(), fingers.getLeftFingerData(), fingers.getRightFingerData());

        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        bioServerClient.changeId(getLocalBioServerUrl(), oldId, newId);

        return new BiometricSubject(newId);
    }

    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        Fingers fingers = scanDoubleFingers();

        bioServerClient.identify(getLocalBioServerUrl(), getLocationID(),
                fingers.getLeftFingerData(), fingers.getRightFingerData());

        // TODO;
        return new ArrayList<>();
    }

    @Override
    public BiometricSubject lookup(String subjectId) {
        bioServerClient.isRegistered(getLocalBioServerUrl(), subjectId);
        // TODO:
        return null;
    }

    @Override
    public void delete(String subjectId) {
        bioServerClient.delete(getLocalBioServerUrl(), subjectId);
    }

    private Fingers scanDoubleFingers() {
        M2SysCaptureResponse capture = capture();
        Fingers fingers = capture.getFingerData();

        checkFingers(fingers);

        return fingers;
    }

    private M2SysCaptureResponse capture() {
        M2SysCaptureRequest request = new M2SysCaptureRequest();
        addRequiredValues(request);

        Token token = getToken();
        return getHttpClient().postRequest(getServerUrl() + M2SysBiometricsConstants.M2SYS_CAPTURE_ENDPOINT,
                request, token, M2SysCaptureResponse.class);
    }

    private String getLocalBioServerUrl() {
        return getProperty(M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL);
    }

    private void checkFingers(Fingers fingers) {
        if (!fingers.bothFingersCaptured()) {
            throw new M2SysBiometricsException("Capture didn't return biometric "
                    + "data for both fingers");
        }
    }
}
