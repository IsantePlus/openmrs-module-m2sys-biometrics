package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.client.M2SysClient;
import org.springframework.stereotype.Component;

@Component
public class LocalBioServerClient extends AbstractBioServerClient {

    @Override
    public String getServiceUrl(M2SysClient client) {
        return client.getProperty(M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL);
    }
}

