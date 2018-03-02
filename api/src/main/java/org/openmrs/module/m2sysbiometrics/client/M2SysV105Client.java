package org.openmrs.module.m2sysbiometrics.client;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.Finger;
import org.openmrs.module.m2sysbiometrics.model.Fingers;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.List;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV105Client extends AbstractM2SysClient {

    private static final Logger LOG = LoggerFactory.getLogger(M2SysV105Client.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AdministrationService adminService;

    private JAXBContext jaxbContext;

    @PostConstruct
    public void init() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Fingers.class, Finger.class, M2SysResults.class,
                M2SysResult.class);
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        M2SysCaptureResponse capture = scanDoubleFingers();

        String response = localBioServerClient.enroll(this, subject.getSubjectId(), capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isRegisterSuccess()) {
            String responseValue = results.firstValue();

            LOG.info("Got error response from M2Sys: {}. Checking if tied to patient.", responseValue);

            Patient patient = checkIfPatientExists(responseValue);
            if (patient == null) {
                LOG.info("No patient matching fingerprint ID: {}", responseValue);

                String isRegisterResponse = localBioServerClient.isRegistered(this,
                        results.firstValue());
                M2SysResults isRegisterResults = XmlResultUtil.parse(isRegisterResponse);

                if (isRegisterResults.isLookupNotFound()) {
                    throw new M2SysBiometricsException("No success during fingerprint registration: "
                            + responseValue);
                } else {
                    LOG.info("Fingerprints are registered with ID {} but do not match any patient, fixing.",
                            responseValue);
                    if (!StringUtils.equals(subject.getSubjectId(), responseValue)) {
                        LOG.info("Changing existing fingerprint ID {} to {}",
                                responseValue, subject.getSubjectId());

                        localBioServerClient.changeId(this, responseValue, subject.getSubjectId());
                    } else {
                        LOG.info("User already has the same fingerprints registered under his fingerprint ID");
                    }
                }
            } else {
                throw new M2SysBiometricsException("Fingerprints already match patient: "
                        + patient.getPersonName().getFullName());
            }
        }

        Fingers fingers = capture.getFingerData(jaxbContext);

        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        M2SysCaptureResponse capture = scanDoubleFingers();

        String response = localBioServerClient.update(this, subject.getSubjectId(), capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isUpdateSuccess()) {
            throw new M2SysBiometricsException("Unable to update fingerprints for: "
                    + subject.getSubjectId());
        }

        Fingers fingers = capture.getFingerData(jaxbContext);
        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        String response = localBioServerClient.changeId(this, oldId, newId);
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isChangeIdSuccess()) {
            throw new M2SysBiometricsException("Unable to change ID from " + oldId
                + " to " + newId);
        }

        return new BiometricSubject(newId);
    }

    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        M2SysCaptureResponse capture = scanDoubleFingers();

        String response = localBioServerClient.identify(this, capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        return results.toOpenMrsMatchList();
    }

    @Override
    public BiometricSubject lookup(String subjectId) {
        String response = localBioServerClient.isRegistered(this, subjectId);
        M2SysResults results = XmlResultUtil.parse(response);

        return results.isLookupNotFound() ? null : new BiometricSubject(subjectId);
    }

    @Override
    public void delete(String subjectId) {
        String response = localBioServerClient.delete(this, subjectId);
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isDeleteSuccess()) {
            throw new M2SysBiometricsException("Unable to delete fingerprints for: " + subjectId);
        }
    }

    private M2SysCaptureResponse scanDoubleFingers() {
        M2SysCaptureRequest request = new M2SysCaptureRequest();
        addRequiredValues(request);
        request.setCaptureType(1);

        Token token = getToken();

        return getHttpClient().postRequest(
                getCloudScanrUrl() + M2SysBiometricsConstants.M2SYS_CAPTURE_ENDPOINT,
                request, token, M2SysCaptureResponse.class);
    }

    private Patient checkIfPatientExists(String fingerprintId) {
        String identifierUuid = adminService.getGlobalProperty(
                RegistrationCoreConstants.GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID, null);

        if (StringUtils.isNotBlank(identifierUuid)) {
            PatientIdentifierType idType = patientService.getPatientIdentifierTypeByUuid(identifierUuid);
            if (idType != null) {
                List<Patient> patients = patientService.getPatients(null, fingerprintId,
                        Collections.singletonList(idType), true);
                return CollectionUtils.isEmpty(patients) ? null : patients.get(0);
            }
        }

        return null;
    }
}
