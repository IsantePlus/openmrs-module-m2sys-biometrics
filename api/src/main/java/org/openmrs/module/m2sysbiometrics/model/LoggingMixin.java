package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public abstract class LoggingMixin {

    @JsonIgnore
    private String customerKey;

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }
}
