package org.openmrs.module.m2sysbiometrics.model;

public class FingerScanStatus {

    private boolean existsLocally;

    private boolean existsNationally;

    public FingerScanStatus(boolean existLocally, boolean existNationally) {
        this.existsLocally = existLocally;
        this.existsNationally = existNationally;
    }

    public boolean isExistsLocally() {
        return existsLocally;
    }

    public boolean isExistsNationally() {
        return existsNationally;
    }
}
