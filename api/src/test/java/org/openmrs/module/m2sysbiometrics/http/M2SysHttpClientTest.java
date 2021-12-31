package org.openmrs.module.m2sysbiometrics.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

public class M2SysHttpClientTest {

	private static final int SERVER_PORT = 8034;

	private static final int LOCATION_ID = 13;

	private static final int LEFT_TEMPLATE_POSITION = 14;

	private static final int RIGHT_TEMPLATE_POSITION = 15;

	private static final String SERVER_URL = "http://localhost:" + SERVER_PORT;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(SERVER_PORT);

	private M2SysHttpClient httpClient = new M2SysHttpClientImpl();

	private ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setUp() {
		replaceJacksonMessageConverter();
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
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
		//assertEquals("sample string 4", response.getAccessPointId());
		assertEquals("sample string 5", response.getDeviceName());
		assertEquals("sample string 6", response.getPictureData());
		assertEquals("sample string 7", response.getTemplateData());
		assertEquals("sample string 8", response.getTemplateData2());
		assertEquals("sample string 9", response.getLeftTemplate());
		assertEquals("sample string 10", response.getRightTemplate());
		assertEquals("sample string 11", response.getMatchingResult());
		assertEquals("2017-08-02T05:47:22.3014417-07:00", response.getTransactionTime());
		//assertEquals("sample string 12", response.getCustomerKey());
		assertEquals(Integer.valueOf(LOCATION_ID), response.getLocationId());
		assertEquals(Integer.valueOf(LEFT_TEMPLATE_POSITION), response.getLeftTemplatePosition());
		assertEquals(Integer.valueOf(RIGHT_TEMPLATE_POSITION), response.getRightTemplatePosition());
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test
	public void shouldGetToken() throws IOException {
		stubFor(post("/cstoken")
				.withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
				.withRequestBody(equalTo("grant_type=password&username=username&Password=password&scope=custKey"))
				.willReturn(
						aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", "application/json;charset=UTF-8")
								.withBody(readFile("token.json"))));

		Token token = httpClient.getToken(SERVER_URL);

		//  Token token = httpClient.getToken(SERVER_URL,"username", "password", "custKey");
		assertNotNull(token);
		assertEquals("This is a token", token.getAccessToken());
		assertEquals("bearer", token.getTokenType());
		assertEquals(Integer.valueOf(10799), token.getExpiresIn());
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

	// hack to make RestTemplate use old Jackson libs
	// even though we have newer ones on the classpath, because of Wiremock
	private void replaceJacksonMessageConverter() {
		MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
		messageConverter.setPrettyPrint(false);
		messageConverter.setObjectMapper(objectMapper);

		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(httpClient,
				"restOperations");

		restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(
				MappingJackson2HttpMessageConverter.class.getName()));
		restTemplate.getMessageConverters().add(messageConverter);
	}
}
