package org.openmrs.module.m2sysbiometrics.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
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
}
