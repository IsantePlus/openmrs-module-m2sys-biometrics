package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class M2SysRequest implements Serializable {

    private static final long serialVersionUID = -8902210266672985893L;

    @JsonProperty("CustomerKey")
    private String customerKey;

    @JsonProperty("AccessPointID")
    private String accessPointId;

    @JsonProperty("CaptureTimeOut")
    private String captureTimeout;

    @JsonProperty("BioMeticWith")
    private BiometricCaptureType biometricWith;

    @JsonProperty("RegistrationID")
    private String registrationId;

    @JsonProperty("LocationID")
    private String locationId;

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

    public String getCaptureTimeout() {
        return captureTimeout;
    }

    public void setCaptureTimeout(String captureTimeout) {
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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

}
