package org.openmrs.module.m2sysbiometrics.it;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricSensitiveTestBase;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.M2SysEngine;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;


import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class M2SysIT extends M2SysBiometricSensitiveTestBase {

    private static final int CAPTURE_TIMEOUT = 120;
    private static final int LOCATION_ID = 1;

    @Autowired
    private M2SysHttpClient m2SysHttpClient;

    @Autowired
    private M2SysEngine engine;

    @Autowired
    private AdministrationService adminService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        String custKey = System.getenv("m2sys-biometrics.customKey");
        String password = System.getenv("m2sys-biometrics.server.password");
        String username = System.getenv("m2sys-biometrics.server.user");
        String apiUrl = System.getenv("m2sys-biometrics.server.url");
        String accessPointId = System.getenv("m2sys-biometrics.accessPointID");
        String localServiceUrl = System.getenv("m2sys-biometrics.local-service.url");

        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY, custKey);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_PASSWORD, password);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_USER, username);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL, apiUrl);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID, accessPointId);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT,
                String.valueOf(CAPTURE_TIMEOUT));
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID,
                String.valueOf(LOCATION_ID));
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL,
                localServiceUrl);

        replaceJacksonMessageConverter();
    }

    @Test
    //@Ignore
    public void test() {
        BiometricSubject subject = new BiometricSubject("SEARCH_TEST");
        //engine.enroll(subject);

/*        subject = engine.enroll(subject);
        subject = engine.update(subject);*/

        List<BiometricMatch> matches = engine.search(subject);
        assertNotNull(matches);
    }

    private void replaceJacksonMessageConverter() {
        MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
        messageConverter.setPrettyPrint(false);
        messageConverter.setObjectMapper(objectMapper);

        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(m2SysHttpClient,
                "restOperations");

        restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(
                MappingJackson2HttpMessageConverter.class.getName()));
        restTemplate.getMessageConverters().add(messageConverter);
    }
}
