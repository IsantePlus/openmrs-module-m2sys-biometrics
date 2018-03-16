package org.openmrs.module.m2sysbiometrics.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class M2SysProperties {

    @Autowired
    private AdministrationService adminService;

    public String getGlobalProperty(String propertyName) {
        String propertyValue = adminService.getGlobalProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            throw new APIException(String.format("Property value for '%s' is not set", propertyName));
        }
        return propertyValue;
    }

    public boolean isGlobalPropertySet(String propertyName) {
        return StringUtils.isNotBlank(adminService.getGlobalProperty(propertyName));
    }

    public String getCloudScanrUrl() {
        return getGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_URL);
    }

    public String getCloudScanrUsername() {
        return getGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_USERNAME);
    }

    public String getCloudScanrPassword() {
        return getGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_PASSWORD);
    }

    public String getCustomerKey() {
        return getGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
    }

    public float getCaptureTimeOut() {
        return Float.parseFloat(getGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT));
    }

    public String getAccessPointId() {
        return getGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID);
    }
}
