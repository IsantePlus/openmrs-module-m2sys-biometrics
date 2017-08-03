package org.openmrs.module.m2sysbiometrics.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.util.Token;

import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertNotNull;

public class M2SysHttpClientTest {
	
	private static final int SERVER_PORT = 8034;
	
	private static final String SERVER_URL = "http://localhost:" + SERVER_PORT;
	
	private M2SysHttpClient httpClient = new M2SysHttpClientImpl();
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(SERVER_PORT);
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void shouldPostAndGetM2SysResponse() throws IOException {
		stubFor(post(M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT)
		        .withHeader("Content-Type", equalTo("application/json"))
		        .withHeader("Authorization", equalTo("Bearer XXX"))
		        //.withRequestBody(equalTo(readFile("sampleRequest.json")))
		        .willReturn(
		            aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", "application/json")
		                    .withBody(readFile("sampleResponse.json"))));
		M2SysRequest request = objectMapper.readValue(readFile("sampleRequest.json"), M2SysRequest.class);
		
		M2SysResponse response = httpClient.postRequest(SERVER_URL + M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT,
		    request, token());
		
		assertNotNull(response);
	}
	
	private String readFile(String file) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            return IOUtils.toString(in);
        }
    }
	
	private Token token() {
		Token token = new Token();
		token.setAccessToken("XXX");
		token.setTokenType("Bearer");
		return token;
	}
}
