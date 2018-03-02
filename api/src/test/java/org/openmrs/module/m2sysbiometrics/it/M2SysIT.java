package org.openmrs.module.m2sysbiometrics.it;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricSensitiveTestBase;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.M2SysEngine;
import org.openmrs.module.m2sysbiometrics.bioplugin.BioServerClient;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;


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

    @Autowired
    private BioServerClient bioServerClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String localServiceUrl;

    @Before
    public void setUp() {
        String custKey = System.getenv("m2sys-biometrics.customKey");
        String password = System.getenv("m2sys-biometrics.server.password");
        String username = System.getenv("m2sys-biometrics.server.user");
        String accessPointId = System.getenv("m2sys-biometrics.accessPointID");
        String apiUrl = System.getenv("m2sys-biometrics.server.url");

        localServiceUrl = System.getenv("m2sys-biometrics.local-service.url");

        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY, custKey);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCAL_PASSWORD, password);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCAL_USERNAME, username);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANNER_URL, apiUrl);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID, accessPointId);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT,
                String.valueOf(CAPTURE_TIMEOUT));
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID,
                String.valueOf(LOCATION_ID));
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL,
                localServiceUrl);
    }

    @Test
    //@Ignore
    public void test() {
        BiometricSubject subject = new BiometricSubject("SEARCH_TEST2");
        //engine.delete(subject.getSubjectId());
        //engine.enroll(subject);

        //engine.delete(subject.getSubjectId());
        engine.enroll(subject);

       // List<BiometricMatch> matches = engine.search(subject);

        //bioServerClient.isRegistered(localServiceUrl, subject.getSubjectId());

/*        subject = engine.enroll(subject);
        subject = engine.update(subject);*/
        //List<BiometricMatch> matches = engine.search(subject);

        //engine.enroll(subject);

        //List<BiometricMatch> matches = engine.search(subject);
        //assertNotNull(matches);
    }
}
