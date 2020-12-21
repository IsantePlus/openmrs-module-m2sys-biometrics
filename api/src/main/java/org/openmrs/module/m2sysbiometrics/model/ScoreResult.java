package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class ScoreResult extends M2SysData{
    private static final long serialVersionUID = 2642478741112327810L;
    @JsonProperty("Score")
    private Integer score;

    @JsonProperty("ID")
    private String matchId;

    @JsonProperty("FingerPosition")
    private Integer fingerPosition;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getFingerPosition() {
        return fingerPosition;
    }

    public void setFingerPosition(Integer fingerPosition) {
        this.fingerPosition = fingerPosition;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}