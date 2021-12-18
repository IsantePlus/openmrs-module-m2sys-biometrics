package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.annotation.PostConstruct;

public abstract class AbstractBioServerClient extends WebServiceGatewaySupport implements BioServerClient {

    private static final String LOCATION_ID_PROPERTY = M2SysBiometricsConstants.M2SYS_LOCATION_ID;
    private static final Logger logger = LoggerFactory.getLogger(AbstractBioServerClient.class);

    @Autowired
    @Qualifier("m2sysbiometrics.jax2b")
    private Jaxb2Marshaller marshaller;

    @Autowired
    private M2SysProperties properties;

//    @Autowired
//    @Qualifier("m2sysbiometrics.messageFactory")
//    private WebServiceMessageFactory messageFactory;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMessageFactory(context.getBean("m2sysbiometrics.messageFactory", SaajSoapMessageFactory.class));
//        setMessageFactory(messageFactory);
    }

    @Override
    public String enroll(String subjectId, String biometricXml) {
        Register register = new Register();
        register.setLocationID(getLocationId());
        register.setID(subjectId);

        register.setBiometricXml(biometricXml);
        RegisterResponse response;
        try {
            response = (RegisterResponse) getResponse(register);
        }catch (Exception ex) {
            logger.error(ex.getMessage());
        	return null;
        }
        return response.getRegisterResult();
    }
//    @Override
//    public String enroll(BiometricSubject biometricSubject) {
//        Register register = new Register();
//        register.setLocationID(getLocationId());
//        register.setID(biometricSubject.getSubjectId());
//
//        register.setBiometricXml(marshaller.marshal(biometricSubject.));
//        RegisterResponse response;
//        try {
//            response = (RegisterResponse) getResponse(register);
//        }catch (Exception ex) {
//            logger.error(ex.getMessage());
//        	return null;
//        }
//        return response.getRegisterResult();
//    }

    @Override
    public String isRegistered(String subjectId) {
        IsRegistered isRegistered = new IsRegistered();
        isRegistered.setID(subjectId);

        IsRegisteredResponse response = (IsRegisteredResponse) getResponse(isRegistered);

        return response.getIsRegisteredResult();
    }

    @Override
    public String changeId(String oldId, String newId) {
        ChangeID changeID = new ChangeID();
        changeID.setNewID(newId);
        changeID.setOldID(oldId);

        ChangeIDResponse response = (ChangeIDResponse) getResponse(changeID);

        return response.getChangeIDResult();
    }

    @Override
    public String update(String subjectId, String biometricXml) {
        Update update = new Update();
        update.setLocationID(getLocationId());
        update.setID(subjectId);

        update.setBiometricXml(biometricXml);

        UpdateResponse response = (UpdateResponse) getResponse(update);

        return response.getUpdateResult();
    }

    @Override
    public String identify(String biometricXml) {
        Identify identify = new Identify();

        identify.setBiometricXml(biometricXml);
        identify.setLocationID(getLocationId());

        IdentifyResponse response = (IdentifyResponse) getResponse(identify);

        return response.identifyResult;
    }

    @Override
    public String delete(String subjectId) {
        DeleteID deleteID = new DeleteID();
        deleteID.setID(subjectId);

        DeleteIDResponse response = (DeleteIDResponse) getResponse(deleteID);
        return response.getDeleteIDResult();
    }

    @Override
    public boolean isServerUrlConfigured() {
        return properties.isGlobalPropertySet(getServerUrlPropertyName());
    }

    protected abstract String getServerUrlPropertyName();

    protected abstract Object getResponse(Object requestPayload) throws WebServiceException;

    protected String getProperty(String propertyName) {
        return properties.getGlobalProperty(propertyName);
    }

    protected String getServiceUrl() {
        return getProperty(getServerUrlPropertyName());
    }

    private int getLocationId() {
        return Integer.parseInt(getProperty(LOCATION_ID_PROPERTY));
    }
}
