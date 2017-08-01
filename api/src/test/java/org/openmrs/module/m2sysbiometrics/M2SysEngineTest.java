package org.openmrs.module.m2sysbiometrics;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.module.registrationcore.api.biometrics.model.Fingerprint;
import org.openmrs.test.Verifies;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.*;

public class M2SysEngineTest extends M2SysBiometricSensitiveTestBase {
	
	private final String SEARCH_SUBJECT_RESPONSE = "/searchSubjectsResponse.json";
	
	private final String UPDATE_SUBJECT_ID_RESPONSE = "/updateSubjectIdResponse.json";
	
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
	
	@Test
	@Verifies(value = "updates an ID of subject on M2Sys Biometrics", method = "updateSubjectId(String, String)")
	public void shouldUpdateSubjectID() throws Exception {
		BiometricSubject actual, expected;
		
		when(Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL)).thenReturn(
		    "http://testServerAPI/");
		
		doReturn(readJsonFromFile(UPDATE_SUBJECT_ID_RESPONSE)).when(m2SysEngine).postRequest(
		    eq(M2SysBiometricsConstants.M2SYS_SERVER_URL + M2SYS_CHANGE_ID_ENDPOINT), anyString());
		
		PowerMockito.when(m2SysEngine, "parseResponse", anyString(), anyObject()).thenReturn(prepareDummyBiometricSubject());
		
		actual = m2SysEngine.updateSubjectId("2", "1");
		expected = prepareDummyBiometricSubject();
		
		assertNotNull(actual);
		assertEquals(expected.getSubjectId(), actual.getSubjectId());
		assertEquals(expected.getFingerprints().size(), actual.getFingerprints().size());
		for (int i = 0; i < expected.getFingerprints().size(); i++) {
			assertEquals(expected.getFingerprints().get(i).getImage(), actual.getFingerprints().get(i).getImage());
		}
	}
	
	@Test
	@Verifies(value = "searches a data on M2Sys Server using a subject", method = "search(BiometricSubject)")
	public void shouldSearchBiometricSubject() throws Exception {
		List<BiometricMatch> expected, actual;
		
		when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL)).thenReturn(
		    "http://testServerAPI/");
		doReturn(readJsonFromFile(SEARCH_SUBJECT_RESPONSE)).when(m2SysEngine).postRequest(
		    eq(M2SysBiometricsConstants.M2SYS_SERVER_URL + M2SYS_REGISTER_ENDPOINT), anyString());
		
		PowerMockito.when(m2SysEngine, "parseResponse", anyString(), anyObject()).thenReturn(prepareSearchResults());
		
		actual = m2SysEngine.search(prepareDummyBiometricSubject());
		expected = prepareSearchResults();
		
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getSubjectId(), actual.get(i).getSubjectId());
			assertEquals(expected.get(i).getMatchScore(), actual.get(i).getMatchScore());
		}
	}
	
	private BiometricSubject prepareDummyBiometricSubject() {
		BiometricSubject subject = new BiometricSubject();
		List<Fingerprint> fingerprints = new ArrayList<>();

		subject.setSubjectId("1");

		Fingerprint fingerprint1 = new Fingerprint();
		fingerprint1.setImage("Image 1");

		Fingerprint fingerprint2 = new Fingerprint();
		fingerprint2.setImage("Image 2");

		fingerprints.add(fingerprint1);
		fingerprints.add(fingerprint2);

		subject.setFingerprints(fingerprints);

		return subject;
	}
	
	private List<BiometricMatch> prepareSearchResults() {
		List<BiometricMatch> results = new ArrayList<>();

		BiometricMatch match1 = new BiometricMatch();
		match1.setSubjectId("1");
		match1.setMatchScore(0.5);

		BiometricMatch match2 = new BiometricMatch();
		match2.setSubjectId("2");
		match2.setMatchScore(1.0);

		results.add(match1);
		results.add(match2);

		return results ;
	}
	
	private String readJsonFromFile(String filename) throws Exception {
		Resource resource = new ClassPathResource(filename);
		String json;
		try(InputStream is = resource.getInputStream()) {
			json = IOUtils.toString(is);
		}

		return json;
	}
	
	private BiometricEngineStatus prepareDummyGetStatusResponse() {
		BiometricEngineStatus result = new BiometricEngineStatus();
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);

		result.setStatusMessage(response.getStatusCode() + " " + response.getStatusCode().getReasonPhrase());
		return result;
	}
}
