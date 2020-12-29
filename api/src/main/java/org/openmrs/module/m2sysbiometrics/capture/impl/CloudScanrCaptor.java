package org.openmrs.module.m2sysbiometrics.capture.impl;

import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.capture.M2SysFingerCaptor;
import org.openmrs.module.m2sysbiometrics.client.M2SysV105Client;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.AbstractM2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
//import org.openmrs.module.m2sysbiometrics.util.AccessPointIdResolver;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloudScanrCaptor implements M2SysFingerCaptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudScanrCaptor.class);
    @Autowired
    private M2SysProperties properties;

    @Autowired
    private M2SysHttpClient httpClient;

  //  @Autowired
 //   private AccessPointIdResolver apIdResolver;

    @Override
    public M2SysCaptureResponse scanDoubleFingers() {

        M2SysCaptureRequest request = new M2SysCaptureRequest();
        addRequiredValues(request);
        request.setCaptureType(1);
        request.setDeviceName("DigitalPersona");

//        Token token = getToken();


        return httpClient.postRequest(
                properties.getCloudScanrUrl() + M2SysBiometricsConstants.M2SYS_CAPTURE_ENDPOINT,
                request, null, M2SysCaptureResponse.class);
    }

    private void addRequiredValues(AbstractM2SysRequest request) {
        //request.setAccessPointId(apIdResolver.getAccessPointId());
        request.setCaptureTimeout(properties.getCaptureTimeOut());
        //request.setCustomerKey(properties.getCustomerKey());
    }
    
}
