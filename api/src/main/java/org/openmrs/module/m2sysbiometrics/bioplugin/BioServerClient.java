package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Component
public class BioServerClient extends WebServiceGatewaySupport {

    public void enroll(String serviceUrl, String subjectId, int locationId,
                       String leftTemplate, String rightTemplate) {
        Register register = new Register();
        register.setLocationID(locationId);
        register.setID(subjectId);

        register.setLeftEnrollTemplate(leftTemplate);
        register.setLeftFingerType(0);
        register.setRightEnrollTemplate(rightTemplate);
        register.setRightFingerType(0);

        RegisterResponse response = (RegisterResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, register);

        // TODO
        if (response == null) {
            throw new M2SysBiometricsException("Unable to register fingerprints with"
                    + "the BioPlugin server");
        }
    }

    public void update(String serviceUrl, String subjectId, int locationId,
                       String leftTemplate, String rightTemplate) {
        Update update = new Update();
        update.setLocationID(locationId);
        update.setID(subjectId);

        update.setLeftUpdateTemplate(leftTemplate);
        update.setLeftFingerType(0);
        update.setRightUpdateTemplate(rightTemplate);
        update.setRightFingerType(0);
        UpdateResponse response = (UpdateResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, update);

        // TODO
        if (response == null) {
            throw new M2SysBiometricsException("Unable to update fingerprints with"
                    + "the BioPlugin server");
        }
    }

    public void changeId(String serviceUrl, String oldId, String newId) {
        ChangeID changeID = new ChangeID();
        changeID.setNewID(newId);
        changeID.setOldID(oldId);

        ChangeIDResponse response = (ChangeIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, changeID);

        // TODO
        if (response == null) {
            throw new M2SysBiometricsException("Unable to change the ID with"
                    + "the BioPlugin server");
        }
    }

    public String identify(String serviceUrl, int locationId,
                         String leftTemplate, String rightTemplate) {
        Identify identify = new Identify();

        identify.setLeftCaptureTemplate(leftTemplate);
        identify.setRightCaptureTemplate(rightTemplate);
        identify.setLocationID(locationId);

        IdentifyResponse response = (IdentifyResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, identify);

        // TODO
        if (response == null) {
            throw new M2SysBiometricsException("Unable to perform a biometric search");
        }

        return response.identifyResult;
    }

    public void delete(String serviceUrl, String subjectId) {
        DeleteID deleteID = new DeleteID();
        deleteID.setID(subjectId);

        DeleteIDResponse response = (DeleteIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, deleteID);

        if (response == null) {
            throw new M2SysBiometricsException("Unable to delete subject: " + subjectId);
        }
    }

    public String isRegistered(String serviceUrl, String subjectId) {
        IsRegistered isRegistered = new IsRegistered();
        isRegistered.setID(subjectId);

        IsRegisteredResponse response = (IsRegisteredResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, isRegistered);

        if (response == null) {
            throw new M2SysBiometricsException("Unable to lookup subject: " + subjectId);
        }

        return response.getIsRegisteredResult();
    }
}
