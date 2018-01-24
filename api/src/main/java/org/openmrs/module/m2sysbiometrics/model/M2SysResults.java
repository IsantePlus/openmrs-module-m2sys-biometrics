package org.openmrs.module.m2sysbiometrics.model;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "Results")
@XmlAccessorType(XmlAccessType.FIELD)
public class M2SysResults {

    @XmlElement(name = "result")
    private List<M2SysResult> results = new ArrayList<>();

    public List<M2SysResult> getResults() {
        return results;
    }

    public void setResults(List<M2SysResult> results) {
        this.results = results;
    }

    public void addResult(M2SysResult result) {
        results.add(result);
    }

    public boolean isRegisterSuccess() {
        return firstValueEqualsIgnoreCase(M2SysResult.SUCCESS);
    }

    public boolean isUpdateSuccess() {
        return firstValueEqualsIgnoreCase(M2SysResult.SUCCESS);
    }

    public boolean isDeleteSuccess() {
        return firstValueEqualsIgnoreCase(M2SysResult.DELETE_SUCCESS);
    }

    public boolean isLookupNotFound() {
        return firstValueEqualsIgnoreCase(M2SysResult.FAILED);
    }

    public boolean isChangeIdSuccess() {
        return firstValueEqualsIgnoreCase(M2SysResult.UPDATE_SUBJECT_ID_SUCCESS);
    }

    public String firstValue() {
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0).getValue();
        }
    }

    public boolean firstValueEqualsIgnoreCase(String value) {
        return Objects.equals(firstValue(), value);
    }

    public List<BiometricMatch> toOpenMrsMatchList() {
        List<BiometricMatch> matches = new ArrayList<>();

        for (M2SysResult result : results) {
            if (!result.getValue().equalsIgnoreCase(M2SysResult.FAILED)) {
                BiometricMatch match = new BiometricMatch(result.getValue(),
                        (double) result.getScore());

                matches.add(match);
            }
        }

        return matches;
    }
}
