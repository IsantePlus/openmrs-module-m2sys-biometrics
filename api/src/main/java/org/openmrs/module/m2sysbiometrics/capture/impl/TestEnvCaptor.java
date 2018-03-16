package org.openmrs.module.m2sysbiometrics.capture.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.capture.M2SysFingerCaptor;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.springframework.stereotype.Component;

@Component
public class TestEnvCaptor implements M2SysFingerCaptor {

    @Override
    public M2SysCaptureResponse scanDoubleFingers() {
        String testTemplate = System.getenv(M2SysBiometricsConstants.CONST_TEST_TEMPLATE);

        if (StringUtils.isNotBlank(testTemplate)) {
            M2SysCaptureResponse response = new M2SysCaptureResponse();

            response.setTemplateData(testTemplate);
            response.setSuccess(true);

            return response;
        } else {
            return null;
        }
    }
}
