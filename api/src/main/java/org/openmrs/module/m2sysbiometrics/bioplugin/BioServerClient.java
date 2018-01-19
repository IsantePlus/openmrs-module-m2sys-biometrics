package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.annotation.PostConstruct;

@Component
public class BioServerClient extends WebServiceGatewaySupport {

    @Autowired
    @Qualifier("m2sysbiometrics.jax2b")
    private Jaxb2Marshaller marshaller;

    @Autowired
    private WebServiceMessageFactory messageFactory;

    @PostConstruct
    public void init() {
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMessageFactory(messageFactory);
    }

    public String enroll(String serviceUrl, String subjectId, int locationId,
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

        return response.getRegisterResult();
    }

    public String update(String serviceUrl, String subjectId, int locationId,
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

        return response.getUpdateResult();
    }

    public String getInfo(String serviceUrl, String subjectId) {
        GetInfo getInfo = new GetInfo();

        GetInfoResponse response = (GetInfoResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, getInfo);

        return response.getGetInfoResult();
    }

    public String changeId(String serviceUrl, String oldId, String newId) {
        ChangeID changeID = new ChangeID();
        changeID.setNewID(newId);
        changeID.setOldID(oldId);

        ChangeIDResponse response = (ChangeIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, changeID);

        return response.getChangeIDResult();
    }

    public String identify(String serviceUrl, int locationId,
                         String leftTemplate, String rightTemplate) {
        Identify identify = new Identify();

        identify.setLeftCaptureTemplate(leftTemplate);
        identify.setRightCaptureTemplate(rightTemplate);
        identify.setLocationID(locationId);

        IdentifyResponse response = (IdentifyResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, identify);

        return response.identifyResult;
    }

    public String delete(String serviceUrl, String subjectId) {
        DeleteID deleteID = new DeleteID();
        deleteID.setID(subjectId);

        DeleteIDResponse response = (DeleteIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, deleteID);

        return response.getDeleteIDResult();
    }

    public String isRegistered(String serviceUrl, String subjectId) {
        IsRegistered isRegistered = new IsRegistered();
        isRegistered.setID(subjectId);

        IsRegisteredResponse response = (IsRegisteredResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, isRegistered);

        return response.getIsRegisteredResult();
    }
}
