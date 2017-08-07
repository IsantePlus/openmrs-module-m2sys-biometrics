package org.openmrs.module.m2sysbiometrics.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class M2SysRequest extends M2SysData {

    private static final long serialVersionUID = -8902210266672985893L;

    @JsonProperty("CustomerKey")
    private String customerKey;

    @JsonProperty("AccessPointID")
    private String accessPointId;

    @JsonProperty("CaptureTimeOut")
    private Float captureTimeout;

    @JsonProperty("BioMetricWith")
    private BiometricCaptureType biometricWith;

    @JsonProperty("RegistrationID")
    private String registrationId;

    @JsonProperty("LocationID")
    private Integer locationId;

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(String accessPointId) {
        this.accessPointId = accessPointId;
    }

    public Float getCaptureTimeout() {
        return captureTimeout;
    }

    public void setCaptureTimeout(Float captureTimeout) {
        this.captureTimeout = captureTimeout;
    }

    public BiometricCaptureType getBiometricWith() {
        return biometricWith;
    }

    public void setBiometricWith(BiometricCaptureType biometricWith) {
        this.biometricWith = biometricWith;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

}
