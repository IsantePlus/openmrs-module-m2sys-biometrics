package org.openmrs.module.m2sysbiometrics.model;


import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class M2SysResult {

    public static final String INVALID_ENGINE = "INVALID_ENGINE";
    public static final String UPDATE_SUBJECT_ID_SUCCESS = "CS";
    public static final String UPDATE_SUBJECT_ID_FAILURE = "CF";
    public static final String DELETE_SUCCESS = "DS";
    public static final String DELETE_FAILURE = "DF";
    public static final String FAILED = "-1";
    public static final String SUCCESS = "SUCCESS";
    public static final String LICENSE_ERROR = "LICENSE_ERROR";

    @XmlAttribute
    private int score;

    @XmlAttribute
    private String value;

    @XmlAttribute(name = "Instance")
    private int instance;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    public void checkCommonErrorValues() {
        if (M2SysResult.INVALID_ENGINE.equals(value)) {
            throw new M2SysBiometricsException("Invalid Engine  - the server is not licensed to"
                    + " handle the biometric engine");
        } else if (M2SysResult.LICENSE_ERROR.equals(value)) {
            throw new M2SysBiometricsException("License error - this enrollment would have"
                    + "exceeded the current server user license limit");
        }
    }
}
