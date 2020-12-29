package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class CloudScanrStatus extends M2SysData {

    private static final long serialVersionUID = 5297535433389093032L;

    @JsonProperty("Success")
    private Boolean success;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("CloudScanrAPIVersion")
    private String cloudScanrAPIVersion;

    @JsonProperty("CloudABISAPIVersion")
    private String cloudABISAPIVersion;

    @JsonProperty("ElapsedTimeInSeconds")
    private String elapsedTimeInSeconds;



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

    public String getCloudScanrAPIVersion() {
        return cloudScanrAPIVersion;
    }

    public void setCloudScanrAPIVersion(String cloudScanrAPIVersion) {
        this.cloudScanrAPIVersion = cloudScanrAPIVersion;
    }

    public String getCloudABISAPIVersion() {
        return cloudABISAPIVersion;
    }

    public void setCloudABISAPIVersion(String cloudABISAPIVersion) {
        this.cloudABISAPIVersion = cloudABISAPIVersion;
    }

    public String getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }

    public void setElapsedTimeInSeconds(String elapsedTimeInSeconds) {
        this.elapsedTimeInSeconds = elapsedTimeInSeconds;
    }
}
