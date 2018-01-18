package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.m2sysbiometrics.model.ChangeIdRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.ERROR_CODE_OF_SUBJECT_NOT_EXIST;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_CHANGE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_DELETE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SEARCH_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_UPDATE_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getErrorMessage;
import static org.openmrs.module.m2sysbiometrics.util.M2SysResponseUtil.checkDeleteResponse;
import static org.openmrs.module.m2sysbiometrics.util.M2SysResponseUtil.checkLookupResponse;
import static org.openmrs.module.m2sysbiometrics.util.M2SysResponseUtil.checkUpdateSubjectIdResponse;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV1Client extends AbstractM2SysClient {

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        getLogger().info("Called getStatus enroll");
        if (subject == null) {
            subject = new BiometricSubject();
        }
        if (subject.getSubjectId() == null) {
            subject.setSubjectId(UUID.randomUUID().toString());
            getLogger().debug(String.format("Generated a new SubjectId: %s", subject.getSubjectId()));
        }

        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        try {
            M2SysResponse response = getHttpClient().postRequest(url(M2SYS_REGISTER_ENDPOINT), request, getToken());
            BiometricSubject biometricSubject = response.toBiometricSubject(subject.getSubjectId());
            getLogger().debug(String.format("A new BiometricsSubject with %s subjectId has been enrolled",
                    biometricSubject.getSubjectId()));
            return biometricSubject;
        } catch (Exception ex) {
            getLogger().error(String.format("Updating BiometricSubject with %s id failed", subject.getSubjectId()));
            throw ex;
        }
    }

    /**
     * Updates subject on M2Sys server.
     *
     * @param subject to update
     * @return updated subject
     */
    @Override
    public BiometricSubject update(BiometricSubject subject) {
        getLogger().info("Called update method");
        BiometricSubject existingSubject = lookup(subject.getSubjectId());
        if (existingSubject == null) {
            getLogger().error(String.format("BiometricSubject with %s subjectId doesn't exist", subject.getSubjectId()));
            throw new IllegalArgumentException(getErrorMessage(ERROR_CODE_OF_SUBJECT_NOT_EXIST));
        }

        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        try {
            M2SysResponse response = getHttpClient().postRequest(url(M2SYS_UPDATE_ENDPOINT), request, getToken());
            BiometricSubject biometricSubject = response.toBiometricSubject(subject.getSubjectId());
            getLogger().debug(String.format("BiometricsSubject with %s subjectId has been updated",
                    biometricSubject.getSubjectId()));
            return biometricSubject;
        } catch (Exception ex) {
            getLogger().error(String.format("Updating BiometricSubject with %s id failed", subject.getSubjectId()));
            throw ex;
        }
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
        getLogger().info("Called updateSubjectId method");
        ChangeIdRequest request = new ChangeIdRequest();
        addCommonValues(request);
        request.setRegistrationId(oldId);
        request.setNewRegistrationId(newId);

        try {
            M2SysResponse response = getHttpClient().postRequest(url(M2SYS_CHANGE_ID_ENDPOINT), request, getToken());
            checkUpdateSubjectIdResponse(response);
            BiometricSubject biometricSubject = new BiometricSubject(newId);
            getLogger().debug(String.format("subjectId of BiometricsSubject has been changed from %s to %s value",
                    oldId, newId));
            return biometricSubject;
        } catch (Exception ex) {
            getLogger().error(String.format("Changing subject id from %s to %s value failed", oldId, newId));
            throw ex;
        }
    }

    /**
     * Searching a biometric data using a given pattern subject.
     *
     * @param subject a pattern subject
     * @return a list of matching data from M2Sys Server
     */
    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        getLogger().info("Called search method");
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);

        try {
            M2SysResponse response = getHttpClient().postRequest(url(M2SYS_SEARCH_ENDPOINT), request, getToken());
            List<BiometricMatch> biometricMatches = response.toMatchList();
            getLogger().debug(String.format("There are %d results of search method", biometricMatches.size()));
            return biometricMatches;
        } catch (Exception ex) {
            getLogger().error("Error during search method");
            throw ex;
        }
    }

    /**
     * Lookup a biometric data using a given id of pattern subject.
     *
     * @param subjectId a pattern subject Id
     * @return a biometric subject object
     */
    @Override
    public BiometricSubject lookup(String subjectId) {
        getLogger().info("Called lookup method");
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subjectId);

        try {
            BiometricSubject biometricSubject = null;
            M2SysResponse response = getHttpClient().postRequest(url(M2SYS_LOOKUP_ENDPOINT), request, getToken());
            if (checkLookupResponse(response)) {
                biometricSubject = new BiometricSubject();
                biometricSubject.setSubjectId(subjectId);
            }
            getLogger().debug(String.format("BiometricSubject with %s subjectId exists", subjectId));
            return biometricSubject;
        } catch (Exception ex) {
            getLogger().debug(String.format("BiometricSubject with %s subjectId doesn't exist", subjectId));
            throw ex;
        }
    }

    /**
     * Deleting a biometric subject with a specific id
     *
     * @param subjectId a biometric subject id
     */
    @Override
    public void delete(String subjectId) {
        getLogger().info("Called delete method");
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subjectId);

        try {
            M2SysResponse response = getHttpClient().postRequest(url(M2SYS_DELETE_ID_ENDPOINT), request, getToken());
            checkDeleteResponse(response);
            getLogger().debug(String.format("BiometricsSubject with %s subjectId has been deleted", subjectId));
        } catch (Exception ex) {
            getLogger().error(String.format(
                    "BiometricsSubject with %s subjectId hasn't been deleted. Probably the subject doesn't exist",
                    subjectId));
            throw ex;
        }
    }
}
