package org.openmrs.module.m2sysbiometrics;

import org.openmrs.api.context.Context;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SERVER_URL;

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine extends BaseResource implements BiometricEngine {
	
	protected M2SysEngine() {
		this(new RestTemplate());
	}
	
	protected M2SysEngine(RestOperations restOperations) {
		super(restOperations);
	}
	
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
	
	public BiometricSubject updateSubjectId(String oldId, String newId) {
		return null;
	}
	
	public List<BiometricMatch> search(BiometricSubject subject) {
		return null;
	}
	
	public BiometricSubject lookup(String subjectId) {
		BiometricSubject result = new BiometricSubject();
		
		postRequest(Context.getAdministrationService().getGlobalProperty(M2SYS_SERVER_URL) + M2SYS_LOOKUP_ENDPOINT, "");
		return result;
	}
	
	public void delete(String subjectId) {
	}
}
