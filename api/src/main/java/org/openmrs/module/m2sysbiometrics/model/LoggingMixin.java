package org.openmrs.module.m2sysbiometrics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
