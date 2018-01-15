package org.openmrs.module.m2sysbiometrics.it;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.M2SysEngine;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClientImpl;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class M2SysIT {

    private static final int CAPTURE_TIMEOUT = 120;
    private static final int LOCATION_ID = 1;

    private M2SysHttpClient m2SysHttpClient = new M2SysHttpClientImpl();
    private M2SysEngine engine = new M2SysEngine();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AdministrationService adminService;

    @Before
    public void setUp() {
        String custKey = System.getenv("m2sys-biometrics.customKey");
        String password = System.getenv("m2sys-biometrics.server.password");
        String username = System.getenv("m2sys-biometrics.server.user");
        String apiUrl = System.getenv("m2sys-biometrics.server.url");
        String accessPointId = System.getenv("m2sys-biometrics.accessPointID");

        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY))
                .thenReturn(custKey);
        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_PASSWORD))
                .thenReturn(password);
        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_USER))
                .thenReturn(username);
        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL))
                .thenReturn(apiUrl);
        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID))
                .thenReturn(accessPointId);
        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT))
                .thenReturn(String.valueOf(CAPTURE_TIMEOUT));
        when(adminService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID))
                .thenReturn(String.valueOf(LOCATION_ID));

        replaceJacksonMessageConverter();

        ReflectionTestUtils.setField(engine, "adminService", adminService);
        ReflectionTestUtils.setField(engine, "httpClient", m2SysHttpClient);
    }

    @Test
    @Ignore
    public void test() {
/*        Token token = m2SysHttpClient.getToken(apiUrl, username, password, custKey);
        assertNotNull(token);*/
        BiometricSubject subject = new BiometricSubject("MIT");

        subject = engine.enroll(subject);
        //List<BiometricMatch> matches = engine.search(subject);

        assertNotNull(subject);
        //assertNotNull(matches);
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
