package org.openmrs.module.m2sysbiometrics.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Results")
@XmlAccessorType(XmlAccessType.FIELD)
public class M2SysMatchingResult {

    @XmlElement(name = "result")
    private List<M2SysResult> results;

    public List<M2SysResult> getResults() {
        return results;
    }

    public void setResults(List<M2SysResult> results) {
        this.results = results;
    }
}
