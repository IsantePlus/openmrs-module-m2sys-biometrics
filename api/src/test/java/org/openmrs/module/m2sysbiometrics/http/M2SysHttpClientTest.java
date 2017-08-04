package org.openmrs.module.m2sysbiometrics.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
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
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class M2SysHttpClientTest {

  private static final int SERVER_PORT = 8034;
  private static final int LOCATION_ID = 13;
  private static final int LEFT_TEMPLATE_POSITION = 14;
  private static final int RIGHT_TEMPLATE_POSITION = 15;

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
            .withRequestBody(equalToJson(readFile("sampleRequest.json")))
            .willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", "application/json")
                        .withBody(readFile("sampleResponse.json"))));
    M2SysRequest request = objectMapper.readValue(readFile("sampleRequest.json"), M2SysRequest.class);

    M2SysResponse response = httpClient.postRequest(SERVER_URL + M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT,
        request, token());

    assertNotNull(response);
    assertEquals(Integer.valueOf(1), response.getClientPlatform());
    assertEquals("sample string 2", response.getApiVersion());
    assertEquals("sample string 3", response.getClientVersion());
    assertTrue(response.getSuccess());
    assertEquals("sample string 5", response.getMessage());
    assertEquals("sample string 6", response.getResponseCode());
    assertEquals("2017-08-02T05:47:22.3014417-07:00", response.getServerTime());
    assertEquals("sample string 1", response.getRegistrationId());
    assertEquals("sample string 2", response.getNewRegistrationId());
    assertEquals("sample string 3", response.getTransactionId());
    assertEquals("sample string 4", response.getAccessPointId());
    assertEquals("sample string 5", response.getDeviceName());
    assertEquals("sample string 6", response.getPictureData());
    assertEquals("sample string 7", response.getTemplateData());
    assertEquals("sample string 8", response.getTemplateData2());
    assertEquals("sample string 9", response.getLeftTemplate());
    assertEquals("sample string 10", response.getRightTemplate());
    assertEquals("sample string 11", response.getMatchingResult());
    assertEquals("2017-08-02T05:47:22.3014417-07:00", response.getTransactionTime());
    assertEquals("sample string 12", response.getCustomerKey());
    assertEquals(Integer.valueOf(LOCATION_ID), response.getLocationId());
    assertEquals(Integer.valueOf(LEFT_TEMPLATE_POSITION), response.getLeftTemplatePosition());
    assertEquals(Integer.valueOf(RIGHT_TEMPLATE_POSITION), response.getRightTemplatePosition());
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
