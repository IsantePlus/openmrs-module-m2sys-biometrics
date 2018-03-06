package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.apache.commons.codec.binary.Base64;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.springframework.stereotype.Component;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import java.nio.charset.Charset;


@Component
public class NationalBioServerClient extends AbstractBioServerClient {

    @Override
    protected String getServerUrlPropertyName() {
        return M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL;
    }

    @Override
    protected Object getResponse(Object requestPayload) {
        return getWebServiceTemplate()
                .marshalSendAndReceive(getServiceUrl(), requestPayload, message -> addAuthorizationHeader());
    }

    private void addAuthorizationHeader() {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
        connection.getConnection().addRequestProperty("Authorization",
                generateBasicAuthenticationHeader(getProperty(M2SysBiometricsConstants.M2SYS_NATIONAL_USERNAME),
                        getProperty(M2SysBiometricsConstants.M2SYS_NATIONAL_PASSWORD)));
    }

    private static String generateBasicAuthenticationHeader(String userName, String userPassword) {
        byte[] bytesEncoded = Base64.encodeBase64((userName + ":" + userPassword).getBytes(Charset.forName("UTF-8")));
        return "Basic " + new String(bytesEncoded, Charset.forName("UTF-8"));
    }
}
