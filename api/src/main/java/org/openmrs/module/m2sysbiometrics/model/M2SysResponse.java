package org.openmrs.module.m2sysbiometrics.model;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import java.util.ArrayList;
import java.util.List;

public class M2SysResponse extends AbstractM2SysResponse {

    private static final long serialVersionUID = 5297535433389093052L;

    @JsonProperty("APIVersion")
    private String apiVersion;

    @JsonProperty("ClientPlatform")
    private Integer clientPlatform;

    @JsonProperty("ClientVersion")
    private String clientVersion;

    @JsonProperty("RegistrationID")
    private String registrationId;

    @JsonProperty("TransactionID")
    private String transactionId;

    @JsonProperty("AccessPointID")
    private String accessPointId;

    @JsonProperty("DeviceName")
    private String deviceName;

    @JsonProperty("PictureData")
    private String pictureData;

    @JsonProperty("TemplateData")
    private String templateData;

    @JsonProperty("TemplateData2")
    private String templateData2;

    @JsonProperty("LeftTemplate")
    private String leftTemplate;

    @JsonProperty("RightTemplate")
    private String rightTemplate;

    @JsonProperty("MatchingResult")
    private String matchingResult;

    @JsonProperty("CustomerKey")
    private String customerKey;

    @JsonProperty("LocationID")
    private Integer locationId;

    @JsonProperty("ServerTime")
    private String serverTime;

    @JsonProperty("NewRegistrationID")
    private String newRegistrationId;

    @JsonProperty("TransactionTime")
    private String transactionTime;

    @JsonProperty("LeftTemplatePosition")
    private Integer leftTemplatePosition;

    @JsonProperty("RightTemplatePosition")
    private Integer rightTemplatePosition;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Integer getClientPlatform() {
        return clientPlatform;
    }

    public void setClientPlatform(Integer clientPlatform) {
        this.clientPlatform = clientPlatform;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(String accessPointId) {
        this.accessPointId = accessPointId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPictureData() {
        return pictureData;
    }

    public void setPictureData(String pictureData) {
        this.pictureData = pictureData;
    }

    public String getTemplateData() {
        return templateData;
    }

    public void setTemplateData(String templateData) {
        this.templateData = templateData;
    }

    public String getTemplateData2() {
        return templateData2;
    }

    public void setTemplateData2(String templateData2) {
        this.templateData2 = templateData2;
    }

    public String getLeftTemplate() {
        return leftTemplate;
    }

    public void setLeftTemplate(String leftTemplate) {
        this.leftTemplate = leftTemplate;
    }

    public String getRightTemplate() {
        return rightTemplate;
    }

    public void setRightTemplate(String rightTemplate) {
        this.rightTemplate = rightTemplate;
    }

    public String getMatchingResult() {
        return matchingResult;
    }

    public void setMatchingResult(String matchingResult) {
        this.matchingResult = matchingResult;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getLeftTemplatePosition() {
        return leftTemplatePosition;
    }

    public void setLeftTemplatePosition(Integer leftTemplatePosition) {
        this.leftTemplatePosition = leftTemplatePosition;
    }

    public Integer getRightTemplatePosition() {
        return rightTemplatePosition;
    }

    public void setRightTemplatePosition(Integer rightTemplatePosition) {
        this.rightTemplatePosition = rightTemplatePosition;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getNewRegistrationId() {
        return newRegistrationId;
    }

    public void setNewRegistrationId(String newRegistrationId) {
        this.newRegistrationId = newRegistrationId;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public BiometricSubject toBiometricSubject(String subjectId) {
        BiometricSubject subject = null;

        if (StringUtils.isNotBlank(matchingResult)) {
            M2SysResults m2SysResults = parseMatchingResult();

            for (M2SysResult result : m2SysResults.getResults()) {
                result.checkCommonErrorValues();
                if (M2SysResult.FAILED.equals(result.getValue())) {
                    throw new M2SysBiometricsException("Failed or was cancelled before completion");
                } else if (M2SysResult.SUCCESS.equals(result.getValue())) {
                    subject = new BiometricSubject(subjectId);
                } else {
                    throw new M2SysBiometricsException("Failed - biometric template already exists in system."
                            + " Registration id: " + result.getValue());
                }
            }
        }

        return subject;
    }

    public List<BiometricMatch> toMatchList() {
        List<BiometricMatch> matches = new ArrayList<>();

        if (StringUtils.isNotBlank(matchingResult)) {
            M2SysResults m2SysMatches = parseMatchingResult();

            for (M2SysResult result : m2SysMatches.getResults()) {
                result.checkCommonErrorValues();
                if (!M2SysResult.FAILED.equals(result.getValue())) {
                    BiometricMatch match = new BiometricMatch(result.getValue(),
                            (double) result.getScore());
                    matches.add(match);
                }
            }
        }

        return matches;
    }

    public M2SysResults parseMatchingResult() {
        return XmlResultUtil.parse(matchingResult);
    }
}
