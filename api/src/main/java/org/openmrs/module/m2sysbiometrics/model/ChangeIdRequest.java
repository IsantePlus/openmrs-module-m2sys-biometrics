package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class ChangeIdRequest extends M2SysRequest {

    private static final long serialVersionUID = 6750595360135161824L;

    @JsonProperty("NewRegistrationID")
    private String newRegistrationId;

    public String getNewRegistrationId() {
        return newRegistrationId;
    }

    public void setNewRegistrationId(String newRegistrationId) {
        this.newRegistrationId = newRegistrationId;
    }
}
