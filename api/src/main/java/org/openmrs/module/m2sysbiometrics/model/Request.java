package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class Request extends M2SysData {
    private static final long serialVersionUID = 2642478746210327810L;
    @JsonProperty("CustomerKey")
    private String customerKey;
    @JsonProperty("EngineName")
    private String engineName;
    @JsonProperty("RegistrationId")
    private String registrationId;
    @JsonProperty("Format")
    private String format;
    @JsonProperty("BiometricXml")
    private String biometricXml;

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getBiometricXml() {
        return biometricXml;
    }

    public void setBiometricXml(String biometricXml) {
        this.biometricXml = biometricXml;
    }
}
