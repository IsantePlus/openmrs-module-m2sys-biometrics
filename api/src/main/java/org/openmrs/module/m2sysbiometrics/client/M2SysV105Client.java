package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.bioplugin.BioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.Finger;
import org.openmrs.module.m2sysbiometrics.model.Fingers;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.util.List;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV105Client extends AbstractM2SysClient {

    @Autowired
    private BioServerClient bioServerClient;

    private JAXBContext jaxbContext;

    @PostConstruct
    public void init() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Fingers.class, Finger.class, M2SysResults.class,
                M2SysResult.class);
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        Fingers fingers = scanDoubleFingers();

        String response = bioServerClient.enroll(getLocalBioServerUrl(), subject.getSubjectId(),
                getLocationID(), fingers.getLeftFingerData(), fingers.getRightFingerData());
        M2SysResults results = unmarshalResponse(response);

        if (!results.isRegisterSuccess()) {
            throw new M2SysBiometricsException("No success during fingerprint registration");
        }

        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        Fingers fingers = scanDoubleFingers();

        String response = bioServerClient.update(getLocalBioServerUrl(), subject.getSubjectId(),
                getLocationID(), fingers.getLeftFingerData(), fingers.getRightFingerData());
        M2SysResults results = unmarshalResponse(response);

        if (!results.isUpdateSuccess()) {
            throw new M2SysBiometricsException("Unable to update fingerprints for: "
                    + subject.getSubjectId());
        }

        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        String response = bioServerClient.changeId(getLocalBioServerUrl(), oldId, newId);
        M2SysResults results = unmarshalResponse(response);

        if (!results.isChangeIdSuccess()) {
            throw new M2SysBiometricsException("Unable to change ID from " + oldId
                + " to " + newId);
        }

        return new BiometricSubject(newId);
    }

    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        Fingers fingers = scanDoubleFingers();

        String response = bioServerClient.identify(getLocalBioServerUrl(), getLocationID(),
                fingers.getLeftFingerData(), fingers.getRightFingerData());
        M2SysResults results = unmarshalResponse(response);

        return results.toOpenMrsMatchList();
    }

    @Override
    public BiometricSubject lookup(String subjectId) {
        String response = bioServerClient.isRegistered(getLocalBioServerUrl(), subjectId);
        M2SysResults results = unmarshalResponse(response);

        return results.isLookupNotFound() ? null : new BiometricSubject(subjectId);
    }

    @Override
    public void delete(String subjectId) {
        String response = bioServerClient.delete(getLocalBioServerUrl(), subjectId);
        M2SysResults results = unmarshalResponse(response);

        if (!results.isDeleteSuccess()) {
            throw new M2SysBiometricsException("Unable to delete fingerprints for: " + subjectId);
        }
    }

    private Fingers scanDoubleFingers() {
        M2SysCaptureRequest request = new M2SysCaptureRequest();
        addRequiredValues(request);
        request.setCaptureType(1);

        Token token = getToken();
        M2SysCaptureResponse capture = getHttpClient().postRequest(
                getServerUrl() + M2SysBiometricsConstants.M2SYS_CAPTURE_ENDPOINT,
                request, token, M2SysCaptureResponse.class);

        Fingers fingers = capture.getFingerData(jaxbContext);
        checkFingers(fingers);

        return fingers;
    }

    private void checkFingers(Fingers fingers) {
        if (!fingers.bothFingersCaptured()) {
            throw new M2SysBiometricsException("Capture didn't return biometric "
                    + "data for both fingers");
        }
    }

    private M2SysResults unmarshalResponse(String response) {
        try {
            InputSource input = new InputSource(new StringReader(response));
            return (M2SysResults) jaxbContext.createUnmarshaller().unmarshal(input);
        } catch (JAXBException e) {
            throw new M2SysBiometricsException("Unable to unmarshal response: " + response, e);
        }
    }
}
