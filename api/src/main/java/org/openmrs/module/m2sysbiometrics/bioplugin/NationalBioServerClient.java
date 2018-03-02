package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.client.M2SysClient;
import org.springframework.stereotype.Component;

@Component
public class NationalBioServerClient extends AbstractBioServerClient {

    @Override
    public String getServiceUrl(M2SysClient client) {
        return client.getProperty(M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL);
    }
}
