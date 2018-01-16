package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class AbstractM2SysResponse extends M2SysData {

    private static final long serialVersionUID = 5297535433389093052L;

    @JsonProperty("Success")
    private Boolean success;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("ResponseCode")
    private String responseCode;

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
}
