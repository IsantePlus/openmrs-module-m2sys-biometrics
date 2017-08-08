package org.openmrs.module.m2sysbiometrics;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.BiometricCaptureType;
import org.openmrs.module.m2sysbiometrics.model.M2SysMatchingResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.ChangeIdRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.test.Verifies;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_DELETE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_UPDATE_ENDPOINT;

public class M2SysEngineTest extends M2SysBiometricSensitiveTestBase {

    private static final String SERVER_URL = "http://testServerAPI/";

    private static final String LOCATION_ID = "13";

    private static final String CUSTOMER_KEY = "DO-NOT-COMMIT-ME-TO-GH";

    private static final String CAPTURE_TIMEOUT = "110.4";

    private static final String ACCESS_POINT_ID = "A01";

    private static final String USERNAME = "user";

    private static final String PASSWORD = "pass";

    @Mock
    private AdministrationService administrationService;

    @Mock
    private M2SysHttpClient httpClient;

    @Mock
    private M2SysResponse response;

    @Mock
    private BiometricSubject expectedSubject;

    @Mock
    private M2SysMatchingResult expectedMatchingResult;

    @Mock
    private Token token;

    @InjectMocks
    private M2SysEngine m2SysEngine;

    @Captor
    private ArgumentCaptor<M2SysRequest> requestCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL)).thenReturn(SERVER_URL);
        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID)).thenReturn(LOCATION_ID);
        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY)).thenReturn(CUSTOMER_KEY);
        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT)).thenReturn(
                CAPTURE_TIMEOUT);
        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID)).thenReturn(
                ACCESS_POINT_ID);

        when(response.toBiometricSubject()).thenReturn(expectedSubject);
        when(response.parseMatchingResult()).thenReturn(expectedMatchingResult);

        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_USER)).thenReturn(USERNAME);
        when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_PASSWORD)).thenReturn(PASSWORD);

        when(httpClient.getToken(SERVER_URL, USERNAME, PASSWORD)).thenReturn(token);
    }

    @Test
    @Verifies(value = "get response from M2Sys Biometrics", method = "getStatus()")
    public void shouldGetStatus() throws IOException {

        doReturn(new ResponseEntity<String>(HttpStatus.OK)).when(httpClient).getServerStatus(SERVER_URL, token);

        BiometricEngineStatus status = m2SysEngine.getStatus();

        assertNotNull(status);
        assertEquals("200 OK", status.getStatusMessage());
    }

    @Test
    @Verifies(value = "updates an ID of subject on M2Sys Biometrics", method = "updateSubjectId(String, String)")
    public void shouldUpdateSubjectID() throws Exception {
        final String url = SERVER_URL + M2SysBiometricsConstants.M2SYS_CHANGE_ID_ENDPOINT;
        when(httpClient.postRequest(eq(url), any(ChangeIdRequest.class), eq(token))).thenReturn(response);

        BiometricSubject subject = m2SysEngine.updateSubjectId("2", "1");

        assertEquals(expectedSubject, subject);
        verify(httpClient).postRequest(eq(url), requestCaptor.capture(), eq(token));

        ChangeIdRequest request = (ChangeIdRequest) requestCaptor.getValue();
        verifyRequestCommonFields(request);
        assertEquals("2", request.getRegistrationId());
        assertEquals("1", request.getNewRegistrationId());
    }

    @Test
    @Verifies(value = "register subject on M2Sys Biometrics", method = "enroll(BiometricSubject)")
    public void shouldRegisterBiometricSubject() throws Exception {
        final String url = SERVER_URL + M2SYS_REGISTER_ENDPOINT;

        when(httpClient.postRequest(eq(url), any(M2SysRequest.class), eq(token))).thenReturn(response);

        BiometricSubject reqSubject = new BiometricSubject("ID1");
        BiometricSubject subject = m2SysEngine.enroll(reqSubject);

        verifyBiometricSubjectRequest(url, subject);
    }

    @Test
    public void shouldLookupBiometricSubject() throws Exception {
        final String url = SERVER_URL + M2SYS_LOOKUP_ENDPOINT;

        M2SysResult expectedResult = new M2SysResult();
        expectedResult.setValue("11");

        when(httpClient.postRequest(eq(url), any(M2SysRequest.class), eq(token))).thenReturn(response);
        when(expectedMatchingResult.getResults()).thenReturn(Lists.newArrayList(expectedResult));

        BiometricSubject reqSubject = new BiometricSubject("ID1");
        BiometricSubject subject = m2SysEngine.lookup(reqSubject.getSubjectId());

        assertEquals(reqSubject.getSubjectId(), subject.getSubjectId());
    }

    @Test
    @Verifies(value = "updates subject on M2Sys Biometrics", method = "update(BiometricSubject)")
    public void shouldUpdateBiometricSubject() throws Exception {
        final String url = SERVER_URL + M2SYS_UPDATE_ENDPOINT;
        final String lookupUrl = SERVER_URL + M2SYS_LOOKUP_ENDPOINT;

        when(httpClient.postRequest(eq(url), any(M2SysRequest.class), eq(token))).thenReturn(response);
        when(httpClient.postRequest(eq(lookupUrl), any(M2SysRequest.class), eq(token))).thenReturn(response);

        BiometricSubject reqSubject = new BiometricSubject("ID1");
        BiometricSubject subject = m2SysEngine.update(reqSubject);

        assertEquals(expectedSubject, subject);
        verify(httpClient, times(2)).postRequest(anyString(), requestCaptor.capture(), eq(token));

        M2SysRequest lookupRequest = requestCaptor.getAllValues().get(0);
        verifyRequestCommonFields(lookupRequest);
        assertEquals("ID1", lookupRequest.getRegistrationId());

        M2SysRequest request = requestCaptor.getAllValues().get(1);
        verifyRequestCommonFields(request);
        assertEquals("ID1", request.getRegistrationId());
    }

    @Test(expected = IllegalArgumentException.class)
    @Verifies(value = "updating a non-existent subject", method = "update(BiometricSubject)")
    public void shouldNotUpdateBiometricSubjectIfItDoesNotExist() throws Exception {
        final String url = SERVER_URL + M2SYS_UPDATE_ENDPOINT;
        final String lookupUrl = SERVER_URL + M2SYS_LOOKUP_ENDPOINT;

        when(httpClient.postRequest(eq(url), any(M2SysRequest.class), eq(token))).thenReturn(response);
        when(httpClient.postRequest(eq(lookupUrl), any(M2SysRequest.class), eq(token)))
                .thenReturn(mock(M2SysResponse.class)); // won't return subject

        BiometricSubject reqSubject = new BiometricSubject("ID1");
        m2SysEngine.update(reqSubject);
    }

    @Test
    @Verifies(value = "delete biometrics subject with specific id from M2Sys Biometrics", method = "delete(String)")
    public void shouldDeleteSubject() throws IOException {
        final String url = SERVER_URL + M2SYS_DELETE_ID_ENDPOINT;

        when(httpClient.postRequest(eq(url), any(M2SysRequest.class), eq(token))).thenReturn(response);

        m2SysEngine.delete("XXX");

        verify(httpClient).postRequest(eq(url), requestCaptor.capture(), eq(token));

        M2SysRequest request = requestCaptor.getValue();
        verifyRequestCommonFields(request);
        assertEquals("XXX", request.getRegistrationId());

    }

    private void verifyRequestCommonFields(M2SysRequest request) {
        assertEquals(CUSTOMER_KEY, request.getCustomerKey());
        assertEquals(Float.valueOf(CAPTURE_TIMEOUT), request.getCaptureTimeout());
        assertEquals(Integer.valueOf(LOCATION_ID), request.getLocationId());
        assertEquals(ACCESS_POINT_ID, request.getAccessPointId());
        assertEquals(BiometricCaptureType.None, request.getBiometricWith());
    }

    private void verifyBiometricSubjectRequest(String url, BiometricSubject subject) {
        assertEquals(expectedSubject, subject);
        verify(httpClient).postRequest(eq(url), requestCaptor.capture(), eq(token));

        M2SysRequest request = requestCaptor.getValue();
        verifyRequestCommonFields(request);
        assertEquals("ID1", request.getRegistrationId());
    }
}
