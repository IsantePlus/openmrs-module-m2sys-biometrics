package org.openmrs.module.m2sysbiometrics.http;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.AbstractM2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.LoggingMixin;
import org.openmrs.module.m2sysbiometrics.model.M2SysData;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getErrorMessage;

/**
 * Serves as a base for all implementation of the resource interfaces. Provides method for basic
 * REST operations with the M2Sys servers.
 */
@Component
public class M2SysHttpClientImpl implements M2SysHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysHttpClientImpl.class);

    private RestOperations restOperations = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        // don't log customer key
        objectMapper.getSerializationConfig().addMixInAnnotations(M2SysRequest.class, LoggingMixin.class);
        objectMapper.getSerializationConfig().addMixInAnnotations(M2SysResponse.class, LoggingMixin.class);

        configureJackson((RestTemplate) restOperations);
    }

    @Override
    public ResponseEntity<String> getServerStatus(String url, Token token) {
        try {
            return exchange(new URI(url), HttpMethod.GET, String.class, token);
        } catch (URISyntaxException e) {
            throw new M2SysBiometricsException(e);
        }
    }

    @Override
    public M2SysResponse postRequest(String url, M2SysData request, Token token) {
        return postRequest(url, request, token, M2SysResponse.class);
    }

    /**
     * Sends a post request to the M2Sys server.
     *
     * @param request the request to be sent
     * @return the response json
     */
    @Override
    public <T extends AbstractM2SysResponse> T postRequest(String url, M2SysData request, Token token,
                                                           Class<T> responseClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        debugRequest(url, request);

        try {
            ResponseEntity<T> responseEntity = exchange(new URI(url), HttpMethod.POST, request, headers,
                    responseClass, token);
            T response = responseEntity.getBody();
            checkResponse(response);
            return response;
        } catch (HttpStatusCodeException e) {
            throw new M2SysBiometricsException("Error response, status: " + e.getStatusCode() + ""
                    + " " + e.getStatusText() + ". Body: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new M2SysBiometricsException(e);
        }
    }

    private <T> ResponseEntity<T> exchange(URI url, HttpMethod method, Class<T> responseClass, Token token) {
        return exchange(url, method, null, new HttpHeaders(), responseClass, token);
    }

    private <T> ResponseEntity<T> exchange(URI url, HttpMethod method, Object body,
                                           HttpHeaders headers, Class<T> responseClass, Token token) {

        headers.add("Authorization", token.getTokenType() + " " + token.getAccessToken());

        return restOperations.exchange(url, method, new HttpEntity<>(body, headers), responseClass);
    }

    @Override
    public Token getToken(String host, String username, String password, String customerKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("Password", password);
        body.add("scope", customerKey);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        return restOperations.exchange(host + "/cstoken", HttpMethod.POST, entity, Token.class)
                .getBody();
    }

    private void checkResponse(AbstractM2SysResponse response) {
        if (BooleanUtils.isNotTrue(response.getSuccess())) {
            String errorCode = response.getResponseCode();
            throw new M2SysBiometricsException("Failure response: " + errorCode + " - " + getErrorMessage(errorCode));
        }
    }

    private void debugRequest(String url, Object request) {
        if (LOGGER.isDebugEnabled()) {
            if (request == null) {
                LOGGER.debug("{} called");
            } else {
                try {
                    String json = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(request);
                    LOGGER.debug("{} called, request body:\n {}", url, json);
                } catch (IOException e) {
                    throw new M2SysBiometricsException(e);
                }
            }
        }
    }

    private void configureJackson(RestTemplate restTemplate) {
        MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
        messageConverter.setPrettyPrint(false);

        restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(
                MappingJackson2HttpMessageConverter.class.getName()));
        restTemplate.getMessageConverters().add(messageConverter);
    }
}
