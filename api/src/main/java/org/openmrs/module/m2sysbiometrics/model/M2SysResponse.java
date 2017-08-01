package org.openmrs.module.m2sysbiometrics.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class M2SysResponse implements Serializable {

    private static final long serialVersionUID = 5297535433389093052L;

    @SerializedName("APIVersion")
    private String apiVersion;

    private Integer clientPlatform;
    private String cientVersion;
    private Boolean success;
    private String message;
    private String responseCode;
    private String registrationID;
    private String transactionID;
    private String accessPointID;
    private String deviceName;
    private String pictureData;
    private String templateData;
    private String templateData2;
    private String leftTemplate;
    private String rightTemplate;
    private String matchingResult;
    private String customerKey;
    private Integer locationID;

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

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getAccessPointID() {
        return accessPointID;
    }

    public void setAccessPointID(String accessPointID) {
        this.accessPointID = accessPointID;
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

    public Integer getLocationID() {
        return locationID;
    }

    public void setLocationID(Integer locationID) {
        this.locationID = locationID;
    }
}
