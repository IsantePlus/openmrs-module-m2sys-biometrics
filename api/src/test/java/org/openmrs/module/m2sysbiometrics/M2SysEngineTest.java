package org.openmrs.module.m2sysbiometrics;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.test.Verifies;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class M2SysEngineTest extends M2SysBiometricSensitiveTestBase {
	
	@Mock
	private AdministrationService administrationService;
	
	@Spy
	@InjectMocks
	private M2SysEngine m2SysEngine;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	@Verifies(value = "get response from M2Sys Biometrics", method = "getStatus()")
	public void shouldGetStatus() throws IOException {
		BiometricEngineStatus actual, expected;
		
		when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL)).thenReturn(
		    "http://testServerAPI/");
		
		doReturn(new ResponseEntity<String>(HttpStatus.OK)).when(m2SysEngine).getServerStatus(anyString());
		
		actual = m2SysEngine.getStatus();
		
		expected = prepareDummyGetStatusResponse();
		
		assertNotNull(actual);
		assertEquals(expected.getStatusMessage(), actual.getStatusMessage());
		assertEquals(expected.getDescription(), actual.getDescription());
	}
	
	private BiometricEngineStatus prepareDummyGetStatusResponse() {
		BiometricEngineStatus result = new BiometricEngineStatus();
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);

		result.setStatusMessage(response.getStatusCode() + " " + response.getStatusCode().getReasonPhrase());
		return result;
	}
}
