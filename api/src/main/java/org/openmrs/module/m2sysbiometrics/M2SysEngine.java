package org.openmrs.module.m2sysbiometrics;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.m2sysbiometrics.model.BiometricCaptureType;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.ChangeIdRequest;
import org.openmrs.module.m2sysbiometrics.util.Token;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.ERROR_CODE_OF_SUBJECT_NOT_EXIST;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_CHANGE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_DELETE_ID_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_LOOKUP_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_REGISTER_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_SERVER_URL;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_UPDATE_ENDPOINT;
import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.getErrorMessage;

@Component("m2sysbiometrics.M2SysEngine")
public class M2SysEngine implements BiometricEngine {

    @Autowired
    private AdministrationService adminService;

    @Autowired
    private M2SysHttpClient httpClient;

    /**
     * Gets a status of biometric server.
     */
    @Override
    public BiometricEngineStatus getStatus() {
        BiometricEngineStatus result = new BiometricEngineStatus();

        ResponseEntity<String> responseEntity = httpClient.getServerStatus(getServerUrl(), getToken());
        if (null != responseEntity) {
            result.setStatusMessage(responseEntity.getStatusCode() + " " + responseEntity.getStatusCode().getReasonPhrase());
        }

        return result;
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        M2SysResponse response = httpClient.postRequest(url(M2SYS_REGISTER_ENDPOINT), request, getToken());

        return response.toBiometricSubject();
    }

    /**
     * Updates subject on M2Sys server.
     *
     * @param subject to update
     * @return updated subject
     */
    @Override
    public BiometricSubject update(BiometricSubject subject) {
        BiometricSubject existingSubject = lookup(subject.getSubjectId());
        if (existingSubject == null) {
            throw new IllegalArgumentException(getErrorMessage(ERROR_CODE_OF_SUBJECT_NOT_EXIST));
        }

        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        M2SysResponse response = httpClient.postRequest(url(M2SYS_UPDATE_ENDPOINT), request, getToken());

        return response.toBiometricSubject();
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
        ChangeIdRequest request = new ChangeIdRequest();
        addCommonValues(request);
        request.setRegistrationId(oldId);
        request.setNewRegistrationId(newId);

        M2SysResponse response = httpClient.postRequest(url(M2SYS_CHANGE_ID_ENDPOINT), request, getToken());

        return response.toBiometricSubject();
    }

    /**
     * Searching a biometric data using a given pattern subject.
     *
     * @param subject a pattern subject
     * @return a list of matching data from M2Sys Server
     */
    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subject.getSubjectId());

        M2SysResponse response = httpClient.postRequest(url(M2SYS_LOOKUP_ENDPOINT), request, getToken());

        return response.toMatchList();
    }

    /**
     * Lookup a biometric data using a given id of pattern subject.
     *
     * @param subjectId a pattern subject Id
     * @return a biometric subject object
     */
    @Override
    public BiometricSubject lookup(String subjectId) {
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subjectId);

        M2SysResponse response = httpClient.postRequest(url(M2SYS_LOOKUP_ENDPOINT), request, getToken());
        return response.toBiometricSubject();
    }

    /**
     * Deleting a biometric subject with a specific id
     *
     * @param subjectId a biometric subject id
     */
    @Override
    public void delete(String subjectId) {
        M2SysRequest request = new M2SysRequest();
        addCommonValues(request);
        request.setRegistrationId(subjectId);
        httpClient.postRequest(url(M2SYS_DELETE_ID_ENDPOINT), request, getToken());
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
        return httpClient.getToken(username, password);
    }

    private String getProperty(String propertyName) {
        String propertyValue = adminService.getGlobalProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            throw new APIException("Property value for '" + propertyName + "' is not set");
        }
        return propertyValue;
    }
}
