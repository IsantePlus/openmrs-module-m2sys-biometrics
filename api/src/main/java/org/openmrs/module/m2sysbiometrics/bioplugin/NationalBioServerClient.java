package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.springframework.stereotype.Component;

@Component
public class NationalBioServerClient extends AbstractBioServerClient {

    @Override
    protected String getServerUrlPropertyName() {
        return M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL;
    }
}
