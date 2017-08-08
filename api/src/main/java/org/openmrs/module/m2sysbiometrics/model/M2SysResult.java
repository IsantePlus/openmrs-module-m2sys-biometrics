package org.openmrs.module.m2sysbiometrics.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class M2SysResult {

    public static final String INVALID_ENGINE = "INVALID_ENGINE";
    public static final String NO_MATCH = "-1";

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
}
