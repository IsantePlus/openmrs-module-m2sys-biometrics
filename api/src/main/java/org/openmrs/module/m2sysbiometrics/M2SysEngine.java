package org.openmrs.module.m2sysbiometrics;

import org.openmrs.module.m2sysbiometrics.client.M2SysClient;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine implements BiometricEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysEngine.class);

    @Autowired
    private M2SysClient client;

    /**
     * Gets a status of biometric server.
     */
    @Override
    public BiometricEngineStatus getStatus() {
        return client.getStatus();
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        LOGGER.info("Called getStatus enroll");
        if (subject == null) {
            subject = new BiometricSubject();
        }
        if (subject.getSubjectId() == null) {
            subject.setSubjectId(UUID.randomUUID().toString());
            LOGGER.debug(String.format("Generated a new SubjectId: %s", subject.getSubjectId()));
        }

        return client.enroll(subject);
    }

    /**
     * Updates subject on M2Sys server.
     *
     * @param subject to update
     * @return updated subject
     */
    @Override
    public BiometricSubject update(BiometricSubject subject) {
        return client.update(subject);
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
        return client.updateSubjectId(oldId, newId);
    }

    /**
     * Searching a biometric data using a given pattern subject.
     *
     * @param subject a pattern subject
     * @return a list of matching data from M2Sys Server
     */
    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        return client.search();
    }

    /**
     * Lookup a biometric data using a given id of pattern subject.
     *
     * @param subjectId a pattern subject Id
     * @return a biometric subject object
     */
    @Override
    public BiometricSubject lookup(String subjectId) {
        return client.lookup(subjectId);
    }

    /**
     * Deleting a biometric subject with a specific id
     *
     * @param subjectId a biometric subject id
     */
    @Override
    public void delete(String subjectId) {
        client.delete(subjectId);
    }
}
