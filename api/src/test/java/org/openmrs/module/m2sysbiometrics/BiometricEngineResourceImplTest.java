package org.openmrs.module.m2sysbiometrics;

import org.apache.commons.httpclient.HttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.db.AdministrationDAO;
import org.openmrs.api.db.hibernate.HibernateAdministrationDAO;
import org.openmrs.api.impl.AdministrationServiceImpl;
import org.openmrs.test.Verifies;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.Mockito.when;

public class BiometricEngineResourceImplTest extends M2SysBiometricSensitiveTestBase {
	
	@Mock
	private AdministrationService administrationService;
	
	@Mock
	private AdministrationDAO administrationDAO;
	
	private RestOperations restOperations;
	
	private HttpClient httpClient;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		httpClient = new HttpClient();
		restOperations = new RestTemplate();
		//administrationService = new AdministrationServiceImpl();
		//administrationDAO = new HibernateAdministrationDAO();
		
		//administrationService.setAdministrationDAO(administrationDAO);
		
	}
	
	@Test
	@Verifies(value = "get response from M2Sys Biometrics", method = "getStatus()")
	public void shouldGetStatus() throws IOException {
		ResponseEntity<String> expected, actual;
		
		when(administrationService.getGlobalProperty(M2SysBiometricsConstants.M2SYS_SERVER_URL)).thenReturn(
		    "http://testServerAPI/");
		
		expected = prepareDummyGetStatusResponse();
		
		//assertEquals(actual.getIdentifyResult(), expected);
	}
	
	private ResponseEntity<String> prepareDummyGetStatusResponse() {
		ResponseEntity<String> result = new ResponseEntity<>(HttpStatus.OK);

		return result;
	}
}
