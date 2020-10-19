package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getServerStatusDescription;

public abstract class AbstractM2SysClient implements M2SysClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private M2SysHttpClient httpClient;

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
            responseEntity = getHttpClient().getServerStatus(properties.getCloudScanrUrl(), getToken());
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

    protected Logger getLogger() {
        return logger;
    }

    protected M2SysHttpClient getHttpClient() {
        return httpClient;
    }

    protected Token getToken() {
      //  String username = properties.getCloudScanrUsername();
     //   String password = properties.getCloudScanrPassword();
     //   String customerKey = properties.getCustomerKey();
        String cloudScanUrl = properties.getCloudScanrUrl();

        return httpClient.getToken(cloudScanUrl);
       // return httpClient.getToken(cloudScanUrl, username, password, customerKey);
    }

    protected boolean isSuccessfulStatus(HttpStatus httpStatus) {
        return httpStatus.equals(HttpStatus.OK);
    }
}
