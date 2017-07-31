package org.openmrs.module.m2sysbiometrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.m2sysbiometrics.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Serves as a base for all implementation of the resource interfaces. Provides method for basic
 * REST operations with the M2Sys servers.
 */
public abstract class BaseResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseResource.class);

	private String REGISTRATION_ID_KEY = "RegistrationID";
	private String NEW_REGISTRATION_ID_KEY = "NewRegistrationID";
	private String LOCATION_ID_KEY = "LocationID";

	private RestOperations restOperations;
	
	private enum EnumBiometricCaptureType {
		None, BiometricOnly, BothBiometricAndPicture, PictureOnly;
	}
	
	protected BaseResource(RestOperations restOperationsrestOperations) {
		this.restOperations = restOperations;
	}
	
	protected ResponseEntity<String> getServerStatus(String url) {
		ResponseEntity<String> result;
		try {
			result = exchange(new URI(url), HttpMethod.GET);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	protected String enroll(String url, String registrationID) {
		String result;
		JsonObject requestJson = getRequestJson();

		requestJson.addProperty(REGISTRATION_ID_KEY, registrationID);
		requestJson.addProperty(LOCATION_ID_KEY, getLocationID());
		
		result = postRequest(url, requestJson.toString());
		
		return result;
	}

	protected String search(String url, String registrationID) {
		JsonObject requestJson = getRequestJson();

		requestJson.addProperty(REGISTRATION_ID_KEY, registrationID);
		requestJson.addProperty(LOCATION_ID_KEY, getLocationID());

		return postRequest(url, requestJson.toString());
	}

	protected String updateID(String url, String oldID, String newID) {
		JsonObject requestJson = getRequestJson();

		requestJson.addProperty(REGISTRATION_ID_KEY, oldID);
		requestJson.addProperty(NEW_REGISTRATION_ID_KEY, newID);
		requestJson.addProperty(LOCATION_ID_KEY, getLocationID());

		return postRequest(url, requestJson.toString());
	}
	
	/**
	 * Sends a get request to the M2Sys server using the given {@code config}.
	 * 
	 * @return the response json
	 */
	protected String getResponseJson(String url) {
		String responseJson = null;
		try {
			responseJson = exchange(new URI(url), HttpMethod.GET).getBody();
		}
		catch (URISyntaxException e) {}
		return responseJson;
	}
	
	protected JsonObject getRequestJson() {
		JsonObject requestJson = new JsonObject();
		requestJson.addProperty("CustomerKey", getCustomerKey());
		requestJson.addProperty("AccessPointID", getAccessPointID());
		requestJson.addProperty("CaptureTimeOut", getCaptureTimeOut());
		requestJson.addProperty("BioMetricWith", EnumBiometricCaptureType.None.toString());
		
		return requestJson;
	}
	
	/**
	 * Sends a post request to the M2Sys server.
	 * 
	 * @param json the json to be sent
	 * @return the response json
	 */
	protected String postRequest(String url, String json) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.ALL));
		LOGGER.debug("{} request body: {}", url, json);
		String responseJson;
		
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = exchange(new URI(url), HttpMethod.POST, json, headers);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		responseJson = responseEntity.getBody();
		
		return responseJson;
	}
	
	private ResponseEntity<String> exchange(URI url, HttpMethod method) {
		return exchange(url, method, null, new HttpHeaders());
	}
	
	private ResponseEntity<String> exchange(URI url, HttpMethod method, String body, HttpHeaders headers) {
		TokenUtil token = getToken();

		headers.add("Authorization", token.getToken_type() + " " + token.getAccess_token());

        return restOperations.exchange(url, method, new HttpEntity<>(body, headers), String.class);
    }
	
	private TokenUtil getToken() {
		String username = Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_USER);
		String password = Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_PASSWORD);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		String body = "grant_type=password&username=" + username + "&Password=" + password;
		
		ResponseEntity<String> response = restOperations.exchange(M2SysBiometricsConstants.M2SYS_SERVER_URL + "/cstoken",
		    HttpMethod.POST, new HttpEntity<Object>(body, headers), String.class);
		
		Gson gson = new GsonBuilder().create();
		TokenUtil token = gson.fromJson(response.getBody(), TokenUtil.class);
		
		return token;
	}
	
	private String getCustomerKey() {
		return Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOM_KEY);
	}
	
	private String getAccessPointID() {
		return Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID);
	}
	
	private String getCaptureTimeOut() {
		return Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT);
	}
	
	private String getLocationID() {
		return Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID);
	}
}
