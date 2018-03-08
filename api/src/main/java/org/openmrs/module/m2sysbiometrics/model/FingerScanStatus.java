package org.openmrs.module.m2sysbiometrics.model;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public class FingerScanStatus {

    private BiometricSubject localBiometricSubject;

    private BiometricSubject nationalBiometricSubject;

    public FingerScanStatus(BiometricSubject localBiometricSubject, BiometricSubject nationalBiometricSubject) {
        this.localBiometricSubject = localBiometricSubject;
        this.nationalBiometricSubject = nationalBiometricSubject;
    }

    public boolean isRegisteredLocally() {
        return localBiometricSubject != null;
    }

    public boolean isRegisteredNationally() {
        return nationalBiometricSubject != null;
    }

    public BiometricSubject getLocalBiometricSubject() {
        return localBiometricSubject;
    }

    public BiometricSubject getNationalBiometricSubject() {
        return nationalBiometricSubject;
    }
}
