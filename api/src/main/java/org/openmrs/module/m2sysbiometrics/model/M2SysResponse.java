package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.module.registrationcore.api.biometrics.model.Fingerprint;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class M2SysResponse implements Serializable {
	
	private static final long serialVersionUID = 5297535433389093052L;
	
	@JsonProperty("APIVersion")
	private String apiVersion;
	
	@JsonProperty("ClientPlatform")
	private Integer clientPlatform;
	
	@JsonProperty("ClientVersion")
	private String cientVersion;
	
	@JsonProperty("Success")
	private Boolean success;
	
	@JsonProperty("Message")
	private String message;
	
	@JsonProperty("ResponseCode")
	private String responseCode;
	
	@JsonProperty("RegistrationID")
	private String registrationId;
	
	@JsonProperty("TransactionID")
	private String transactionId;
	
	@JsonProperty("AccessPointID")
	private String accessPointId;
	
	@JsonProperty("DeviceName")
	private String deviceName;
	
	@JsonProperty("PictureData")
	private String pictureData;
	
	@JsonProperty("TemplateData")
	private String templateData;
	
	@JsonProperty("TemplateData2")
	private String templateData2;
	
	@JsonProperty("LeftTemplate")
	private String leftTemplate;
	
	@JsonProperty("RightTemplate")
	private String rightTemplate;
	
	@JsonProperty("MatchingResult")
	private String matchingResult;
	
	@JsonProperty("CustomerKey")
	private String customerKey;
	
	@JsonProperty("LocationID")
	private Integer locationId;
	
	public String getApiVersion() {
		return apiVersion;
	}
	
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	
	public Integer getClientPlatform() {
		return clientPlatform;
	}
	
	public void setClientPlatform(Integer clientPlatform) {
		this.clientPlatform = clientPlatform;
	}
	
	public String getCientVersion() {
		return cientVersion;
	}
	
	public void setCientVersion(String cientVersion) {
		this.cientVersion = cientVersion;
	}
	
	public Boolean getSuccess() {
		return success;
	}
	
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getRegistrationId() {
		return registrationId;
	}
	
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public String getAccessPointId() {
		return accessPointId;
	}
	
	public void setAccessPointId(String accessPointId) {
		this.accessPointId = accessPointId;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getPictureData() {
		return pictureData;
	}
	
	public void setPictureData(String pictureData) {
		this.pictureData = pictureData;
	}
	
	public String getTemplateData() {
		return templateData;
	}
	
	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}
	
	public String getTemplateData2() {
		return templateData2;
	}
	
	public void setTemplateData2(String templateData2) {
		this.templateData2 = templateData2;
	}
	
	public String getLeftTemplate() {
		return leftTemplate;
	}
	
	public void setLeftTemplate(String leftTemplate) {
		this.leftTemplate = leftTemplate;
	}
	
	public String getRightTemplate() {
		return rightTemplate;
	}
	
	public void setRightTemplate(String rightTemplate) {
		this.rightTemplate = rightTemplate;
	}
	
	public String getMatchingResult() {
		return matchingResult;
	}
	
	public void setMatchingResult(String matchingResult) {
		this.matchingResult = matchingResult;
	}
	
	public String getCustomerKey() {
		return customerKey;
	}
	
	public void setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
	}
	
	public Integer getLocationId() {
		return locationId;
	}
	
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	public BiometricSubject toBiometricSubject() {
		BiometricSubject subject = new BiometricSubject(registrationId);

		Fingerprint fingerprint = new Fingerprint();

		fingerprint.setImage(getTemplateData());
		subject.addFingerprint(fingerprint);

		fingerprint.setImage(getTemplateData2());
		subject.addFingerprint(fingerprint);

		fingerprint.setImage(getLeftTemplate());
		subject.addFingerprint(fingerprint);

		fingerprint.setImage(getRightTemplate());
		subject.addFingerprint(fingerprint);

		// TODO: if lookup fails, we expect null return
		
		return subject;
	}
	
	public List<BiometricMatch> toMatchList() {
		// TODO:
		return Collections.emptyList();
	}
}
