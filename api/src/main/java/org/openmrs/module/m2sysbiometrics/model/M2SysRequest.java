package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class M2SysRequest extends AbstractM2SysRequest {

    private static final long serialVersionUID = -8902210266672985893L;

    @JsonProperty("BioMetricWith")
    private BiometricCaptureType biometricWith;

    @JsonProperty("RegistrationID")
    private String registrationId;

    @JsonProperty("LocationID")
    private Integer locationId;

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
