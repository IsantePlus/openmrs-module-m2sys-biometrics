package org.openmrs.module.m2sysbiometrics.http;

import org.openmrs.module.m2sysbiometrics.model.AbstractM2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysData;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.springframework.http.ResponseEntity;

public interface M2SysHttpClient {

    ResponseEntity<String> getServerStatus(String url, Token token);

    M2SysResponse postRequest(String url, M2SysData request, Token token);

    <T extends AbstractM2SysResponse> T postRequest(String url, M2SysData request, Token token,
                                                    Class<T> responseClass);

    Token getToken(String host);

    Token getToken(String host, String appKey, String secretKey, String grantType);
}
