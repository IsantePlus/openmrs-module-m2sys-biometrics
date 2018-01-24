package org.openmrs.module.m2sysbiometrics.model;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.module.registrationcore.api.biometrics.model.Fingerprint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement(name = "Fingers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Fingers {

    @XmlElement(name = "Finger")
    private List<Finger> fingers;

    public List<Finger> getFingers() {
        return fingers;
    }

    public void setFingers(List<Finger> fingers) {
        this.fingers = fingers;
    }

    public boolean bothFingersCaptured() {
        return CollectionUtils.size(fingers) > 1;
    }

    public String getLeftFingerData() {
        return fingers.get(0).getTemplateData();
    }

    public String getRightFingerData() {
        return fingers.get(1).getTemplateData();
    }

    public List<Fingerprint> toTwoOpenMrsFingerprints() {
        Finger leftFinger = fingers.get(0);
        Finger rightFinger = fingers.get(1);

        return new ArrayList<>(Arrays.asList(leftFinger.toOpenMRSFingerprint(),
                rightFinger.toOpenMRSFingerprint()));
    }

    public void trimData() {
        fingers.get(0).setTemplateData(fingers.get(0).getTemplateData().trim());
        fingers.get(1).setTemplateData(fingers.get(1).getTemplateData().trim());
    }
}
