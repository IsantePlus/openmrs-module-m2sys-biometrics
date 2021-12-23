package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.CloudAbisResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysData;
import org.openmrs.module.m2sysbiometrics.model.Request;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.WebServiceMessageFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Component
public class NationalBioServerClient extends AbstractBioServerClient {

    private final Log log = LogFactory.getLog(NationalBioServerClient.class);

    public NationalBioServerClient() {
        setMessageFactory(messageFactory);
    }

    @Override
    protected String getServerUrlPropertyName() {
        return M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL;
    }

    @Override
    protected Object getResponse(Object requestPayload) {
        return getWebServiceTemplate().marshalSendAndReceive(getServiceUrl(), requestPayload);
    }
}
