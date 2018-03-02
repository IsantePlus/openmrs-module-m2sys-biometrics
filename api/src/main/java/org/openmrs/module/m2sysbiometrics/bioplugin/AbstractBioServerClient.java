package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.client.M2SysClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.annotation.PostConstruct;

public abstract class AbstractBioServerClient extends WebServiceGatewaySupport implements BioServerClient {

    private static final String LOCATION_ID_PROPERTY = M2SysBiometricsConstants.M2SYS_LOCATION_ID;

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

    @Override
    public String enroll(M2SysClient client, String subjectId, String biometricXml) {
        Register register = new Register();
        register.setLocationID(getLocationId(client));
        register.setID(subjectId);

        register.setBiometricXml(biometricXml);

        RegisterResponse response = (RegisterResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(client), register);

        return response.getRegisterResult();
    }

    @Override
    public String isRegistered(M2SysClient client, String subjectId) {
        IsRegistered isRegistered = new IsRegistered();
        isRegistered.setID(subjectId);

        IsRegisteredResponse response = (IsRegisteredResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(client), isRegistered);

        return response.getIsRegisteredResult();
    }

    @Override
    public String changeId(M2SysClient client, String oldId, String newId) {
        ChangeID changeID = new ChangeID();
        changeID.setNewID(newId);
        changeID.setOldID(oldId);

        ChangeIDResponse response = (ChangeIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(client), changeID);

        return response.getChangeIDResult();
    }

    @Override
    public String update(M2SysClient client, String subjectId, String biometricXml) {
        Update update = new Update();
        update.setLocationID(getLocationId(client));
        update.setID(subjectId);

        update.setBiometricXml(biometricXml);

        UpdateResponse response = (UpdateResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(client), update);

        return response.getUpdateResult();
    }

    @Override
    public String identify(M2SysClient client, String biometricXml) {
        Identify identify = new Identify();

        identify.setBiometricXml(biometricXml);
        identify.setLocationID(getLocationId(client));

        IdentifyResponse response = (IdentifyResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(client), identify);

        return response.identifyResult;
    }

    @Override
    public String delete(M2SysClient client, String subjectId) {
        DeleteID deleteID = new DeleteID();
        deleteID.setID(subjectId);

        DeleteIDResponse response = (DeleteIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(client), deleteID);

        return response.getDeleteIDResult();
    }

    private int getLocationId(M2SysClient client) {
        return Integer.parseInt(client.getProperty(LOCATION_ID_PROPERTY));
    }
}
