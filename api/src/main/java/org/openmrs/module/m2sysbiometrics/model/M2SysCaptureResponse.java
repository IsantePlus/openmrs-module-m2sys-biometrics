package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class M2SysCaptureResponse extends AbstractM2SysResponse {
    private static final long serialVersionUID = -3527335270475362208L;

    @JsonProperty("TemplateData")
    private String templateData;

    @JsonProperty("BioImageData")
    private String bioImageData;

    @JsonProperty("TemplateData")
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
}
