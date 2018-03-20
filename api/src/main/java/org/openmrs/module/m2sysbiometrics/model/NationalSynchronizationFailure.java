package org.openmrs.module.m2sysbiometrics.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "national_synchronization_failure")
public class NationalSynchronizationFailure extends BaseOpenmrsData {

    private static final long serialVersionUID = -5146973328798332082L;

    @Id
    @Column(name = "national_synchronization_failure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subject_id")
    private String subjectId;

    @Lob
    @Column(name = "biometric_xml")
    private String biometricXml;

    @Column(name = "update_failure")
    private boolean update;

    public NationalSynchronizationFailure() {
    }

    public NationalSynchronizationFailure(String subjectId, String biometricXml, boolean update) {
        this.subjectId = subjectId;
        this.biometricXml = biometricXml;
        this.update = update;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getBiometricXml() {
        return biometricXml;
    }

    public void setBiometricXml(String biometricXml) {
        this.biometricXml = biometricXml;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
