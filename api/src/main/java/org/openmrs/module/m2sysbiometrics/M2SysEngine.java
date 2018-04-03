package org.openmrs.module.m2sysbiometrics;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.m2sysbiometrics.client.M2SysClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.module.registrationcore.api.biometrics.model.EnrollmentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public EnrollmentResult enroll() {
        LOGGER.info("Called enroll()");
        BiometricSubject subjectId = generateSubjectId();
        return client.enroll(subjectId);
    }

    private BiometricSubject generateSubjectId() {
        BiometricSubject subjectId = new BiometricSubject(UUID.randomUUID().toString());
        LOGGER.debug(String.format("Generated a new SubjectId: %s", subjectId.getSubjectId()));
        return subjectId;
    }

    @Override
    public EnrollmentResult enroll(String fingerprintsXmlTemplate) {
        LOGGER.info("Called enroll(String fingerprintsXmlTemplate)");
        if (StringUtils.isBlank(fingerprintsXmlTemplate)) {
            throw new M2SysBiometricsException("Fingerprints XML cannot be blank");
        }
        BiometricSubject subjectId = generateSubjectId();
        return client.enroll(subjectId, fingerprintsXmlTemplate);
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
     * Scan fingerprints and return matching patients.
     *
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
