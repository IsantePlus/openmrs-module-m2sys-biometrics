package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.client.M2SysClient;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalBioServerClient extends AbstractBioServerClient {

    @Autowired
    private M2SysHttpClient httpClient;

    @Override
    public String getServiceUrl(M2SysClient client) {
        return client.getProperty(M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL);
    }

    @Override
    public Token getToken(M2SysClient client) {
        String username = client.getProperty(M2SysBiometricsConstants.M2SYS_LOCAL_USERNAME);
        String password = client.getProperty(M2SysBiometricsConstants.M2SYS_LOCAL_PASSWORD);
        String customerKey = client.getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
        String cloudScannerUrl =
                client.getProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANNER_URL);
        return httpClient.getToken(cloudScannerUrl, username, password, customerKey);
    }
}

