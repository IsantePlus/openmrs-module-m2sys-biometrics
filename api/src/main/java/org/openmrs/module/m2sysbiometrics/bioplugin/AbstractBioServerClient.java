package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
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
    private AdministrationService adminService;

    @Autowired
    private WebServiceMessageFactory messageFactory;

    @PostConstruct
    public void init() {
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMessageFactory(messageFactory);
    }

    @Override
    public String enroll(String subjectId, String biometricXml) {
        Register register = new Register();
        register.setLocationID(getLocationId());
        register.setID(subjectId);

        register.setBiometricXml(biometricXml);

        RegisterResponse response = (RegisterResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), register);

        return response.getRegisterResult();
    }

    @Override
    public String isRegistered(String subjectId) {
        IsRegistered isRegistered = new IsRegistered();
        isRegistered.setID(subjectId);

        IsRegisteredResponse response = (IsRegisteredResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), isRegistered);

        return response.getIsRegisteredResult();
    }

    @Override
    public String changeId(String oldId, String newId) {
        ChangeID changeID = new ChangeID();
        changeID.setNewID(newId);
        changeID.setOldID(oldId);

        ChangeIDResponse response = (ChangeIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), changeID);

        return response.getChangeIDResult();
    }

    @Override
    public String update(String subjectId, String biometricXml) {
        Update update = new Update();
        update.setLocationID(getLocationId());
        update.setID(subjectId);

        update.setBiometricXml(biometricXml);

        UpdateResponse response = (UpdateResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), update);

        return response.getUpdateResult();
    }

    @Override
    public String identify(String biometricXml) {
        Identify identify = new Identify();

        identify.setBiometricXml(biometricXml);
        identify.setLocationID(getLocationId());

        IdentifyResponse response = (IdentifyResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), identify);

        return response.identifyResult;
    }

    @Override
    public String delete(String subjectId) {
        DeleteID deleteID = new DeleteID();
        deleteID.setID(subjectId);

        DeleteIDResponse response = (DeleteIDResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), deleteID);

        return response.getDeleteIDResult();
    }

    private int getLocationId() {
        return Integer.parseInt(getProperty(LOCATION_ID_PROPERTY));
    }

    protected String getProperty(String propertyName) {
        String propertyValue = adminService.getGlobalProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            throw new APIException("Property value for '" + propertyName + "' is not set");
        }
        return propertyValue;
    }
}
