package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class LoggingMixin {

    @JsonIgnore
    @JsonProperty("CustomerKey")
    private String customerKey;

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }
}
