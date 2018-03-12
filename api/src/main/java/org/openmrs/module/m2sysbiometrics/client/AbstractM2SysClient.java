package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.AbstractM2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.BiometricCaptureType;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.m2sysbiometrics.util.AccessPointIdResolver;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_URL;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getServerStatusDescription;

public abstract class AbstractM2SysClient implements M2SysClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private M2SysHttpClient httpClient;

    @Autowired
    private AccessPointIdResolver apIdResolver;

    @Autowired
    private M2SysProperties properties;

    /**
     * Gets a status of biometric server.
     */
    @Override
    public BiometricEngineStatus getStatus() {
        logger.info("Called getStatus method");
        BiometricEngineStatus result = new BiometricEngineStatus();

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = getHttpClient().getServerStatus(getCloudScanrUrl(), getToken());
        } catch (ResourceAccessException e) {
            logger.error(e.getMessage());
            responseEntity = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (null != responseEntity) {
            result.setStatusMessage(responseEntity.getStatusCode() + " " + responseEntity.getStatusCode().getReasonPhrase());
            result.setDescription(getServerStatusDescription(responseEntity.getStatusCode().value()));
            result.setEnabled(isSuccessfulStatus(responseEntity.getStatusCode()));
        }

        logger.debug(String.format("M2SysServer status: %s", result.getDescription()));
        return result;
    }

    protected M2SysHttpClient getHttpClient() {
        return httpClient;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void addCommonValues(M2SysRequest request) {
        addRequiredValues(request);

        request.setLocationId(getLocationID());
        request.setBiometricWith(BiometricCaptureType.None); // TODO; why none?
    }

    protected void addRequiredValues(AbstractM2SysRequest request) {
        request.setAccessPointId(getAccessPointId());
        request.setCaptureTimeout(getCaptureTimeOut());
        request.setCustomerKey(getCustomerKey());
    }

    protected String url(String path) {
        return getCloudScanrUrl() + path;
    }

    protected String getCloudScanrUrl() {
        return properties.getGlobalProperty(M2SYS_CLOUD_SCANR_URL);
    }

    protected String getCustomerKey() {
        return properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
    }

    protected String getAccessPointId() {
        return apIdResolver.getAccessPointId();
    }

    protected float getCaptureTimeOut() {
        return Float.parseFloat(properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT));
    }

    protected int getLocationID() {
        return Integer.parseInt(properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID));
    }

    protected Token getToken() {
        String username = properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_USERNAME);
        String password = properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_PASSWORD);
        String customerKey = properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
        return httpClient.getToken(getCloudScanrUrl(), username, password, customerKey);
    }

    protected boolean isSuccessfulStatus(HttpStatus httpStatus) {
        return httpStatus.equals(HttpStatus.OK);
    }
}
