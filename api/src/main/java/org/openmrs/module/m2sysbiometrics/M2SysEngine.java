package org.openmrs.module.m2sysbiometrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.List;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_CHANGE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SERVER_URL;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.LOCATION_ID;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.REGISTRATION_ID;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.NEW_REGISTRATION_ID;

import java.util.HashMap;
import java.util.Map;

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine extends BaseResource implements BiometricEngine {
	
	private AdministrationService adminService = Context.getAdministrationService();
	
	protected M2SysEngine() {
		this(new RestTemplate());
	}
	
	protected M2SysEngine(RestOperations restOperations) {
		super(restOperations);
	}
	
	/**
	 * Gets a status of biometric server.
	 * 
	 * @should get response from M2Sys Biometrics
	 * @return a status of biometric engine
	 */
	public BiometricEngineStatus getStatus() {
		BiometricEngineStatus result = new BiometricEngineStatus();
		
		ResponseEntity<String> responseEntity = getServerStatus(adminService.getGlobalProperty(M2SYS_SERVER_URL));
		if (null != responseEntity) {
			result.setStatusMessage(responseEntity.getStatusCode() + " " + responseEntity.getStatusCode().getReasonPhrase());
		}
		
		return result;
	}
	
	public BiometricSubject enroll(BiometricSubject subject) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subject.getSubjectId());
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(adminService.getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_REGISTER_ENDPOINT, prepareJson(jsonElements));

		return parseResponse(response, BiometricSubject.class);
	}
	
	public BiometricSubject update(BiometricSubject subject) {
		return new BiometricSubject();
	}
	
	/**
	 * Updates subject identifier on M2Sys server
	 * 
	 * @param oldId an old ID
	 * @param newId a new ID
	 * @should updates an ID of subject on M2Sys Biometrics
	 * @return updated subject
	 */
	public BiometricSubject updateSubjectId(String oldId, String newId) {
		Map<String, String> jsonElements = new HashMap<>();

		jsonElements.put(REGISTRATION_ID, oldId);
		jsonElements.put(NEW_REGISTRATION_ID, newId);
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(adminService.getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_CHANGE_ID_ENDPOINT, prepareJson(jsonElements));
		return parseResponse(response, BiometricSubject.class);
	}
	
	/**
	 * Searching a biometric data using a given pattern subject
	 * 
	 * @param subject a pattern subject
	 * @should searches a data on M2Sys Server using a subject
	 * @return a list of matching data from M2Sys Server
	 */
	public List<BiometricMatch> search(BiometricSubject subject) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subject.getSubjectId());
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(adminService.getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_LOOKUP_ENDPOINT, prepareJson(jsonElements));
		return parseResponse(response, new TypeToken<List<BiometricMatch>>() {}.getType());
	}
	
	public BiometricSubject lookup(String subjectId) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subjectId);
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(adminService.getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_LOOKUP_ENDPOINT, prepareJson(jsonElements));
		return parseResponse(response, BiometricSubject.class);
	}
	
	public void delete(String subjectId) {
	}
	
	private <T> T parseResponse(String json, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}
}
