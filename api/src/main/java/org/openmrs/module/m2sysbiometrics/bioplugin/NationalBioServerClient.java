package org.openmrs.module.m2sysbiometrics.bioplugin;

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

    @Autowired
    private M2SysProperties properties;

    @Autowired
    private M2SysHttpClient httpClient;

    @Override
    protected String getServerUrlPropertyName() {
        return M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NationalBioServerClient.class);

    private RestOperations restOperations = new RestTemplate();

    @Override
    protected Object getResponse(Object requestPayload) {
        String authHeader = generateBearerAuthorizationHeader(getToken().getAccessToken());
        URL url = null;
        try {
            url = new URL(getServiceUrl() + M2SysBiometricsConstants.M2SYS_SEARCH_ENDPOINT);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", authHeader);
            con.setDoOutput(true);


            try (OutputStream os = con.getOutputStream()) {
                byte[] input = parseToJson(requestPayload).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    protected Object getResponse(Object requestPayload, String apiEndpoint) {
        Token token = getToken();
        if(httpClient.getServerStatus(getServiceUrl(),token).getStatusCode().equals(HttpStatus.OK)){
            String authHeader = generateBearerAuthorizationHeader(token.getAccessToken());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", authHeader);
            return httpClient.postRequest(getServiceUrl() + apiEndpoint, (M2SysData) requestPayload, token, CloudAbisResult.class);
        }else{
//            Queue for later push to the national server

            return null;
        }
    }


    private String parseToJson(Object requestPayload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(requestPayload);
        return jsonString;
    }

//    private void addAuthorizationHeader() {
//        TransportContext context = TransportContextHolder.getTransportContext();
//        HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
//        String accessToken = getToken().getAccess_token();
//        String authHeader = generateBearerAuthorizationHeader(accessToken);
//        connection.getConnection().addRequestProperty("Authorization", authHeader);
//        connection.getConnection().addRequestProperty("Content-Type", "application/json");
//    }


    private static String generateBearerAuthorizationHeader(String token) {
        return "Bearer " + token;
    }

    private Token getToken() {
        String appKey = getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_APP_KEY);
        String secretKey = getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_SECRET_KEY);
        String grantType = getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_GRANT_TYPE);
        String cloudAbisUrl = getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_API_URL);
        return httpClient.getToken(cloudAbisUrl, appKey, secretKey, grantType);
    }

    @Override
    public String isRegistered(String subjectId) {

        CloudAbisResult isRegisteredResponse;
        Request request = new Request();
        request.setCustomerKey(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_CUSTOMER_KEY));
        request.setEngineName(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_ENGINE_NAME));
        request.setRegistrationId(subjectId);

        try {
            isRegisteredResponse = (CloudAbisResult) getResponse(request, M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT);
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
        return isRegisteredResponse.getOperationResult();
    }

    @Override
    public String enroll(String subjectId, String biometricXml) {
        CloudAbisResult response;
        Request request = new Request();
        request.setCustomerKey(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_CUSTOMER_KEY));
        request.setEngineName(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_ENGINE_NAME));
        request.setRegistrationId(subjectId);
        request.setBiometricXml(biometricXml);
        request.setFormat(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_TEMPLATE_FORMAT));
        try {
            response = (CloudAbisResult) getResponse(request,M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT);
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
        return response.getOperationResult();
    }

    @Override
    public String identify(String biometricXml) {
        CloudAbisResult response;
        Request request = new Request();

        request.setCustomerKey(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_CUSTOMER_KEY));
        request.setEngineName(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_ENGINE_NAME));
        request.setFormat(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_TEMPLATE_FORMAT));
        request.setBiometricXml(biometricXml);

        try {
            response = (CloudAbisResult) getResponse(request,M2SysBiometricsConstants.M2SYS_SEARCH_ENDPOINT);
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
        return response.getOperationResult();
    }

    public CloudAbisResult identifyAbis(String biometricXml) {
        CloudAbisResult response;
        Request request = new Request();

        request.setCustomerKey(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_CUSTOMER_KEY));
        request.setEngineName(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_ENGINE_NAME));
        request.setFormat(getProperty(M2SysBiometricsConstants.M2SYS_CLOUDABIS_TEMPLATE_FORMAT));
        request.setBiometricXml(biometricXml);

            response = (CloudAbisResult) getResponse(request,M2SysBiometricsConstants.M2SYS_SEARCH_ENDPOINT);
        return response;
    }
}
