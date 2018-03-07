package org.openmrs.module.m2sysbiometrics.model;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public class FingerScanStatus {

    private boolean registeredLocally;

    private boolean registeredNationally;

    private BiometricSubject biometricSubject;

    public FingerScanStatus(boolean registeredLocally, boolean registeredNationally,
            BiometricSubject biometricSubject) {
        this.registeredLocally = registeredLocally;
        this.registeredNationally = registeredNationally;
        this.biometricSubject = biometricSubject;
    }

    public boolean isRegisteredLocally() {
        return registeredLocally;
    }

    public boolean isRegisteredNationally() {
        return registeredNationally;
    }

    public BiometricSubject getBiometricSubject() {
        return biometricSubject;
    }
}
