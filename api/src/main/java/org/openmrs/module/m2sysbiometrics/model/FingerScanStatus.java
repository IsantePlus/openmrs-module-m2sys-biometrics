package org.openmrs.module.m2sysbiometrics.model;

public class FingerScanStatus {

    private boolean registeredLocally;

    private boolean registeredNationally;

    public FingerScanStatus(boolean registeredLocally, boolean registeredNationally) {
        this.registeredLocally = registeredLocally;
        this.registeredNationally = registeredNationally;
    }

    public boolean isRegisteredLocally() {
        return registeredLocally;
    }

    public boolean isRegisteredNationally() {
        return registeredNationally;
    }
}
