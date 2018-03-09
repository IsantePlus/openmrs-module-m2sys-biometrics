package org.openmrs.module.m2sysbiometrics.util.impl;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.util.AccessPointIdResolver;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestAddressBasedResolver implements AccessPointIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(RequestAddressBasedResolver.class);

    @Autowired
    private M2SysProperties properties;

    @Override
    public String getAccessPointId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes) {
            ServletRequestAttributes servAttrs = (ServletRequestAttributes) attrs;
            HttpServletRequest request = servAttrs.getRequest();

            String callerAddress = request.getRemoteAddr();

            return getAccessPointId(callerAddress);
        } else {
            LOG.warn("No HTTP address available, using default Access Point ID");
            return defaultId("<NO ADDRESS AVAILABLE>");
        }
    }

    private String getAccessPointId(String callerAddress) {
        LOG.debug("Retrieving Access Point ID mapped for {}", callerAddress);

        if (properties.isGlobalPropertySet(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_MAP)) {
            String idMapProp = properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_MAP);
            Map<String, String> idMap = idMap(idMapProp);

            if (idMap.containsKey(callerAddress)) {
                LOG.debug("Found mapped access point for {}", callerAddress);
                return idMap.get(callerAddress);
            } else {
                LOG.warn("{} is not mapped to any Access Point ID, using default", callerAddress);
                return defaultId(callerAddress);
            }
        } else {
            LOG.debug("{} is not defined, using default address", M2SysBiometricsConstants.M2SYS_ACCESS_POINT_MAP);
            return defaultId(callerAddress);
        }
    }

    private String defaultId(String callerAddress) {
        return properties.getGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID);
    }

    private Map<String, String> idMap(String idMapProp) {
        Map<String, String> idMap = new HashMap<>();

        String[] mappings = idMapProp.split(";");
        for (String mapping : mappings) {
            if (mapping.indexOf(':') >= 0) {
                String[] parts = mapping.split(":");
                if (parts.length == 2) {
                    String ip = parts[0].trim();
                    String apId = parts[1].trim();

                    idMap.put(ip, apId);
                } else {
                    LOG.warn("Invalid mapping in {}: {}", M2SysBiometricsConstants.M2SYS_ACCESS_POINT_MAP,
                            mapping);
                }
            }
        }

        return idMap;
    }
}
