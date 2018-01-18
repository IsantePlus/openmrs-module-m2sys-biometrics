package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class M2SysCaptureResponse extends AbstractM2SysResponse {
    private static final long serialVersionUID = -3527335270475362208L;

    @JsonProperty("TemplateData")
    private String templateData;

    @JsonProperty("BioImageData")
    private String bioImageData;

    @JsonProperty("FaceImageData")
    private String faceImageData;

    public String getTemplateData() {
        return templateData;
    }

    public void setTemplateData(String templateData) {
        this.templateData = templateData;
    }

    public String getBioImageData() {
        return bioImageData;
    }

    public void setBioImageData(String bioImageData) {
        this.bioImageData = bioImageData;
    }

    public String getFaceImageData() {
        return faceImageData;
    }

    public void setFaceImageData(String faceImageData) {
        this.faceImageData = faceImageData;
    }

    @JsonIgnore
    public Fingers getFingerData(JAXBContext jaxbContext) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            InputSource inputSource = new InputSource(new StringReader(templateData));

            return (Fingers) unmarshaller.unmarshal(inputSource);
        } catch (JAXBException e) {
            throw new M2SysBiometricsException("Unable to parse template data in capture response: "
                + templateData, e);
        }
    }
}
