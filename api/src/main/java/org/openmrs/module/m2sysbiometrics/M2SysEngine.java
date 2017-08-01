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

import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.ERROR_CODE_OF_SUBJECT_NOT_EXIST;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.LOCATION_ID;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_CHANGE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SERVER_URL;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_UPDATE_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.NEW_REGISTRATION_ID;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.REGISTRATION_ID;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getErrorMessage;

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
	 */
	public BiometricEngineStatus getStatus() {
		BiometricEngineStatus result = new BiometricEngineStatus();
		
		ResponseEntity<String> responseEntity = getServerStatus(getServerUrl());
		if (null != responseEntity) {
			result.setStatusMessage(responseEntity.getStatusCode() + " " + responseEntity.getStatusCode().getReasonPhrase());
		}
		
		return result;
	}
	
	public BiometricSubject enroll(BiometricSubject subject) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subject.getSubjectId());
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(getServerUrl() + M2SYS_REGISTER_ENDPOINT, prepareJson(jsonElements));

		return parseResponse(response, BiometricSubject.class);
	}
	
	/**
	 * Updates subject on M2Sys server.
	 * 
	 * @param subject to update
	 * @return updated subject
	 */
	public BiometricSubject update(BiometricSubject subject) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subject.getSubjectId());
		jsonElements.put(LOCATION_ID, getLocationID());

		BiometricSubject existingSubject = lookup(subject.getSubjectId());
		if (existingSubject == null) {
			throw new IllegalArgumentException(getErrorMessage(ERROR_CODE_OF_SUBJECT_NOT_EXIST));
		}
		String response = postRequest(getServerUrl() + M2SYS_UPDATE_ENDPOINT, prepareJson(jsonElements));

		return parseResponse(response, BiometricSubject.class);
	}
	
	/**
	 * Updates subject identifier on M2Sys server.
	 * 
	 * @param oldId an old ID
	 * @param newId a new ID
	 * @return updated subject
	 */
	public BiometricSubject updateSubjectId(String oldId, String newId) {
		Map<String, String> jsonElements = new HashMap<>();

		jsonElements.put(REGISTRATION_ID, oldId);
		jsonElements.put(NEW_REGISTRATION_ID, newId);
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(getServerUrl() + M2SYS_CHANGE_ID_ENDPOINT, prepareJson(jsonElements));
		return parseResponse(response, BiometricSubject.class);
	}
	
	/**
	 * Searching a biometric data using a given pattern subject.
	 * 
	 * @param subject a pattern subject
	 * @return a list of matching data from M2Sys Server
	 */
	public List<BiometricMatch> search(BiometricSubject subject) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subject.getSubjectId());
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(getServerUrl() + M2SYS_LOOKUP_ENDPOINT, prepareJson(jsonElements));
		return parseResponse(response, new TypeToken<List<BiometricMatch>>() {}.getType());
	}
	
	public BiometricSubject lookup(String subjectId) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subjectId);
		jsonElements.put(LOCATION_ID, getLocationID());

		String response = postRequest(getServerUrl() + M2SYS_LOOKUP_ENDPOINT, prepareJson(jsonElements));
		return parseResponse(response, BiometricSubject.class);
	}

	/**
	 * Deleting a biometric subject with a specific id
	 *
	 * @param subjectId a biometric subject id
	 */
	public void delete(String subjectId) {
		Map<String, String> jsonElements = new HashMap<>();
		jsonElements.put(REGISTRATION_ID, subjectId);
		postRequest(adminService.getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_LOOKUP_ENDPOINT, prepareJson(jsonElements));
	}
	
	protected <T> T parseResponse(String json, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}

	private String getServerUrl() {
		return adminService.getGlobalProperty(M2SYS_SERVER_URL);
	}
}
