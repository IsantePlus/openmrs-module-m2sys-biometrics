package org.openmrs.module.m2sysbiometrics.client;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.BiometricCaptureType;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SERVER_URL;

public abstract class AbstractM2SysClient implements M2SysClient {

    @Autowired
    private M2SysHttpClient httpClient;

    @Autowired
    private AdministrationService adminService;

    protected M2SysHttpClient getHttpClient() {
        return httpClient;
    }

    protected void addCommonValues(M2SysRequest request) {
        request.setAccessPointId(getAccessPointID());
        request.setCaptureTimeout(getCaptureTimeOut());
        request.setCustomerKey(getCustomerKey());

        request.setLocationId(getLocationID());

        request.setBiometricWith(BiometricCaptureType.None); // TODO; why none?
    }

    protected String url(String path) {
        return getServerUrl() + path;
    }

    protected String getServerUrl() {
        return getProperty(M2SYS_SERVER_URL);
    }

    protected String getCustomerKey() {
        return getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
    }

    protected String getAccessPointID() {
        return getProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID);
    }

    protected float getCaptureTimeOut() {
        return Float.parseFloat(getProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT));
    }

    protected int getLocationID() {
        return Integer.parseInt(getProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID));
    }

    protected Token getToken() {
        String username = getProperty(M2SysBiometricsConstants.M2SYS_USER);
        String password = getProperty(M2SysBiometricsConstants.M2SYS_PASSWORD);
        String customerKey = getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
        return httpClient.getToken(getServerUrl(), username, password, customerKey);
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
