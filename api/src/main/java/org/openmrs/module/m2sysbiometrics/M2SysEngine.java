package org.openmrs.module.m2sysbiometrics;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.BiometricCaptureType;
import org.openmrs.module.m2sysbiometrics.model.ChangeIdRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.UUID;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.ERROR_CODE_OF_SUBJECT_NOT_EXIST;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_CHANGE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_DELETE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SEARCH_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SERVER_URL;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_UPDATE_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getErrorMessage;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getServerStatusDescription;
import static org.openmrs.module.m2sysbiometrics.util.M2SysResponseUtil.checkDeleteResponse;
import static org.openmrs.module.m2sysbiometrics.util.M2SysResponseUtil.checkLookupResponse;
import static org.openmrs.module.m2sysbiometrics.util.M2SysResponseUtil.checkUpdateSubjectIdResponse;

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine implements BiometricEngine {

    @Autowired
    private AdministrationService adminService;

    @Autowired
    private M2SysHttpClient httpClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysEngine.class);

    /**
     * Gets a status of biometric server.
     */
    @Override
    public BiometricEngineStatus getStatus() {
        LOGGER.info("Called getStatus method");
        BiometricEngineStatus result = new BiometricEngineStatus();

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = httpClient.getServerStatus(getServerUrl(), getToken());
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            responseEntity = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (null != responseEntity) {
            result.setStatusMessage(responseEntity.getStatusCode() + " " + responseEntity.getStatusCode().getReasonPhrase());
            result.setDescription(getServerStatusDescription(responseEntity.getStatusCode().value()));
            result.setEnabled(isSuccessfulStatus(responseEntity.getStatusCode()));
        }

        LOGGER.debug(String.format("M2SysServer status: %s", result.getDescription()));
        return result;
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

        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        try {
            M2SysResponse response = httpClient.postRequest(url(M2SYS_REGISTER_ENDPOINT), request, getToken());
            BiometricSubject biometricSubject = response.toBiometricSubject(subject.getSubjectId());
            LOGGER.debug(String.format("A new BiometricsSubject with %s subjectId has been enrolled",
                    biometricSubject.getSubjectId()));
            return biometricSubject;
        } catch (Exception ex) {
            LOGGER.error(String.format("Updating BiometricSubject with %s id failed", subject.getSubjectId()));
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
        LOGGER.info("Called update method");
        BiometricSubject existingSubject = lookup(subject.getSubjectId());
        if (existingSubject == null) {
            LOGGER.error(String.format("BiometricSubject with %s subjectId doesn't exist", subject.getSubjectId()));
            throw new IllegalArgumentException(getErrorMessage(ERROR_CODE_OF_SUBJECT_NOT_EXIST));
        }

        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        try {
            M2SysResponse response = httpClient.postRequest(url(M2SYS_UPDATE_ENDPOINT), request, getToken());
            BiometricSubject biometricSubject = response.toBiometricSubject(subject.getSubjectId());
            LOGGER.debug(String.format("BiometricsSubject with %s subjectId has been updated",
                    biometricSubject.getSubjectId()));
            return biometricSubject;
        } catch (Exception ex) {
            LOGGER.error(String.format("Updating BiometricSubject with %s id failed", subject.getSubjectId()));
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
        LOGGER.info("Called updateSubjectId method");
        ChangeIdRequest request = new ChangeIdRequest();
        addCommonValues(request);
        request.setRegistrationId(oldId);
        request.setNewRegistrationId(newId);

        try {
            M2SysResponse response = httpClient.postRequest(url(M2SYS_CHANGE_ID_ENDPOINT), request, getToken());
            checkUpdateSubjectIdResponse(response);
            BiometricSubject biometricSubject = new BiometricSubject(newId);
            LOGGER.debug(String.format("subjectId of BiometricsSubject has been changed from %s to %s value", oldId, newId));
            return biometricSubject;
        } catch (Exception ex) {
            LOGGER.error(String.format("Changing subject id from %s to %s value failed", oldId, newId));
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
        LOGGER.info("Called search method");
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);

        try {
            M2SysResponse response = httpClient.postRequest(url(M2SYS_SEARCH_ENDPOINT), request, getToken());
            List<BiometricMatch> biometricMatches = response.toMatchList();
            LOGGER.debug(String.format("There are %d results of search method", biometricMatches.size()));
            return biometricMatches;
        } catch (Exception ex) {
            LOGGER.error("Error during search method");
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
        LOGGER.info("Called lookup method");
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subjectId);

        try {
            BiometricSubject biometricSubject = null;
            M2SysResponse response = httpClient.postRequest(url(M2SYS_LOOKUP_ENDPOINT), request, getToken());
            if (checkLookupResponse(response)) {
                biometricSubject = new BiometricSubject();
                biometricSubject.setSubjectId(subjectId);
            }
            LOGGER.debug(String.format("BiometricSubject with %s subjectId exists", subjectId));
            return biometricSubject;
        } catch (Exception ex) {
            LOGGER.debug(String.format("BiometricSubject with %s subjectId doesn't exist", subjectId));
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
        LOGGER.info("Called delete method");
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subjectId);

        try {
            M2SysResponse response = httpClient.postRequest(url(M2SYS_DELETE_ID_ENDPOINT), request, getToken());
            checkDeleteResponse(response);
            LOGGER.debug(String.format("BiometricsSubject with %s subjectId has been deleted", subjectId));
        } catch (Exception ex) {
            LOGGER.error(String.format(
                    "BiometricsSubject with %s subjectId hasn't been deleted. Probably the subject doesn't exist",
                    subjectId));
            throw ex;
        }
    }

    private void addCommonValues(M2SysRequest request) {
        request.setAccessPointId(getAccessPointID());
        request.setCaptureTimeout(getCaptureTimeOut());
        request.setCustomerKey(getCustomerKey());

        request.setLocationId(getLocationID());

        request.setBiometricWith(BiometricCaptureType.None); // TODO; why none?
    }

    private String url(String path) {
        return getServerUrl() + path;
    }

    private String getServerUrl() {
        return getProperty(M2SYS_SERVER_URL);
    }

    private String getCustomerKey() {
        return getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
    }

    private String getAccessPointID() {
        return getProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID);
    }

    private float getCaptureTimeOut() {
        return Float.parseFloat(getProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT));
    }

    private int getLocationID() {
        return Integer.parseInt(getProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID));
    }

    private Token getToken() {
        String username = getProperty(M2SysBiometricsConstants.M2SYS_USER);
        String password = getProperty(M2SysBiometricsConstants.M2SYS_PASSWORD);
        String customerKey = getProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY);
        return httpClient.getToken(getServerUrl(), username, password, customerKey);
    }

    private String getProperty(String propertyName) {
        String propertyValue = adminService.getGlobalProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            throw new APIException("Property value for '" + propertyName + "' is not set");
        }
        return propertyValue;
    }

    private boolean isSuccessfulStatus(HttpStatus httpStatus) {
        return httpStatus.equals(HttpStatus.OK);
    }
}
