package org.openmrs.module.m2sysbiometrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine extends BaseResource implements BiometricEngine {
	

	
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
		
		ResponseEntity<String> responseEntity = getServerStatus(Context.getAdministrationService().getGlobalProperty(
		    M2SYS_SERVER_URL));
		if (null != responseEntity) {
			result.setStatusMessage(responseEntity.getStatusCode() + " " + responseEntity.getStatusCode().getReasonPhrase());
		}
		
		return result;
	}
	
	public BiometricSubject enroll(BiometricSubject subject) {
		return new BiometricSubject();
	}
	
	public BiometricSubject update(BiometricSubject subject) {
		return new BiometricSubject();
	}

	/**
	 * Updates subject identifier on M2Sys server
	 *
	 * @param oldId  an old ID
	 * @param newId  a new ID
	 * @should updates an ID of subject on M2Sys Biometrics
	 * @return updated subject
	 */
	public BiometricSubject updateSubjectId(String oldId, String newId) {
		String response = updateID(Context.getAdministrationService().getGlobalProperty(M2SYS_SERVER_URL)
				+ M2SYS_CHANGE_ID_ENDPOINT, oldId, newId);
		return parseResponse(response, BiometricSubject.class);
	}

	/**
	 * Searching a biometric data using a given pattern subject
	 *
	 * @param subject  a pattern subject
	 * @should searches a data on M2Sys Server using a subject
	 * @return a list of matching data from M2Sys Server
	 */
	public List<BiometricMatch> search(BiometricSubject subject) {
		String response = search(Context.getAdministrationService().getGlobalProperty(M2SYS_SERVER_URL)
				+ M2SYS_LOOKUP_ENDPOINT, subject.getSubjectId());
		return parseResponse(response, new TypeToken<List<BiometricMatch>>() {}.getType());
	}
	
	public BiometricSubject lookup(String subjectId) {
		BiometricSubject result = new BiometricSubject();
		
		postRequest(Context.getAdministrationService().getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_LOOKUP_ENDPOINT, "");
		return result;
	}
	
	public void delete(String subjectId) {
	}
	
	private <T> T parseResponse(String json, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}
}
