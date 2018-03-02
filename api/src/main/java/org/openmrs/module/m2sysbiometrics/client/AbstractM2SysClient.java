package org.openmrs.module.m2sysbiometrics.client;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.AbstractM2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.BiometricCaptureType;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.m2sysbiometrics.util.AccessPointIdResolver;
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
    private AdministrationService adminService;

    @Autowired
    private AccessPointIdResolver apIdResolver;

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
        return getProperty(M2SYS_CLOUD_SCANR_URL);
    }

    protected String getCustomerKey() {
        return getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
    }

    protected String getAccessPointId() {
        return apIdResolver.getAccessPointId();
    }

    protected float getCaptureTimeOut() {
        return Float.parseFloat(getProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT));
    }

    protected int getLocationID() {
        return Integer.parseInt(getProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID));
    }

    protected Token getToken() {
        String username = getProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_USERNAME);
        String password = getProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_PASSWORD);
        String customerKey = getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
        return httpClient.getToken(getCloudScanrUrl(), username, password, customerKey);
    }

    protected String getProperty(String propertyName) {
        String propertyValue = adminService.getGlobalProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            throw new APIException("Property value for '" + propertyName + "' is not set");
        }
        return propertyValue;
    }

    protected boolean isSuccessfulStatus(HttpStatus httpStatus) {
        return httpStatus.equals(HttpStatus.OK);
    }
}
