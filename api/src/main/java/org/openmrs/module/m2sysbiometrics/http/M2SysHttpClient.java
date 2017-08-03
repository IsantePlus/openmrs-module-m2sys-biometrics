package org.openmrs.module.m2sysbiometrics.http;

import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.util.Token;
import org.springframework.http.ResponseEntity;

public interface M2SysHttpClient {
	
	ResponseEntity<String> getServerStatus(String url, Token token);
	
	M2SysResponse postRequest(String url, M2SysRequest request, Token token);
	
	Token getToken(String username, String password);
}
