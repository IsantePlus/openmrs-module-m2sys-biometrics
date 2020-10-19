package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class AbstractM2SysRequest extends M2SysData {

    private static final long serialVersionUID = -8902210266672985893L;

  //  @JsonProperty("CustomerKey")
  //  private String customerKey;

 //   @JsonProperty("AccessPointID")
 //   private String accessPointId;

    @JsonProperty("CaptureTimeOut")
    private Float captureTimeout;
/*
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
*/
    public Float getCaptureTimeout() {
        return captureTimeout;
    }

    public void setCaptureTimeout(Float captureTimeout) {
        this.captureTimeout = captureTimeout;
    }
}
