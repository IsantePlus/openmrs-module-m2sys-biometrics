package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class CloudAbisResult extends AbstractM2SysResponse {
    private static final long serialVersionUID = 2636478741110327810L;
    @JsonProperty("CustomerID")
    private String customerID;
    @JsonProperty("OperationName")
    private Integer operationName;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("OperationResult")
    private String operationResult;
    @JsonProperty("BestResult")
    private ScoreResult bestResult;
    @JsonProperty("DetailResult")
    private List<ScoreResult> detailResult;
    @JsonProperty("MatchCount")
    private Integer matchCount;


    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public Integer getOperationName() {
        return operationName;
    }

    public void setOperationName(Integer operationName) {
        this.operationName = operationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public ScoreResult getBestResult() {
        return bestResult;
    }

    public void setBestResult(ScoreResult bestResult) {
        this.bestResult = bestResult;
    }

    public List<ScoreResult> getDetailResult() {
        return detailResult;
    }

    public void setDetailResult(List<ScoreResult> detailResult) {
        this.detailResult = detailResult;
    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    @Override
    public String toString() {
        return "CloudAbisResult{" +
                "customerID='" + customerID + '\'' +
                ", operationName=" + operationName +
                ", status='" + status + '\'' +
                ", operationResult='" + operationResult + '\'' +
                ", bestResult=" + bestResult +
                ", detailResult=" + detailResult +
                ", matchCount=" + matchCount +
                '}';
    }
}
