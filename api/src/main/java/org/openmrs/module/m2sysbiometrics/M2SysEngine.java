package org.openmrs.module.m2sysbiometrics;

import org.openmrs.module.m2sysbiometrics.client.M2SysV1Client;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine implements BiometricEngine {

    @Autowired
    private M2SysV1Client v1Client;

    /**
     * Gets a status of biometric server.
     */
    @Override
    public BiometricEngineStatus getStatus() {
        return v1Client.getStatus();
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        return v1Client.enroll(subject);
    }

    /**
     * Updates subject on M2Sys server.
     *
     * @param subject to update
     * @return updated subject
     */
    @Override
    public BiometricSubject update(BiometricSubject subject) {
        return v1Client.update(subject);
    }

    /**
     * Updates subject identifier on M2Sys server.
     *
     * @param oldId an old ID
     * @param newId a new ID
     * @return updated subject
     */
    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        return v1Client.updateSubjectId(oldId, newId);
    }

    /**
     * Searching a biometric data using a given pattern subject.
     *
     * @param subject a pattern subject
     * @return a list of matching data from M2Sys Server
     */
    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        return v1Client.search(subject);
    }

    /**
     * Lookup a biometric data using a given id of pattern subject.
     *
     * @param subjectId a pattern subject Id
     * @return a biometric subject object
     */
    @Override
    public BiometricSubject lookup(String subjectId) {
        return v1Client.lookup(subjectId);
    }

    /**
     * Deleting a biometric subject with a specific id
     *
     * @param subjectId a biometric subject id
     */
    @Override
    public void delete(String subjectId) {
        v1Client.delete(subjectId);
    }
}
