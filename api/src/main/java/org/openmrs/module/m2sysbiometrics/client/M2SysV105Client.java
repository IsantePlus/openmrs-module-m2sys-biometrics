package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.capture.impl.CloudScanrCaptor;
import org.openmrs.module.m2sysbiometrics.capture.impl.TestEnvCaptor;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.Finger;
import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.Fingers;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.TempFingerprint;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.service.TempFingerprintService;
import org.openmrs.module.m2sysbiometrics.service.UpdateService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.module.registrationcore.api.biometrics.model.EnrollmentResult;
import org.openmrs.module.registrationcore.api.biometrics.model.EnrollmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV105Client extends AbstractM2SysClient {

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private CloudScanrCaptor cloudScanrCaptor;

    @Autowired
    private TestEnvCaptor testEnvCaptor;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private TempFingerprintService tempFingerprintService;

    private JAXBContext jaxbContext;

    @PostConstruct
    public void init() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Fingers.class, Finger.class, M2SysResults.class,
                M2SysResult.class);
    }

    @Override
    public EnrollmentResult enroll(BiometricSubject localSubject) {
        M2SysCaptureResponse capture = scanDoubleFingers();
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(capture);
        EnrollmentStatus enrollmentStatus = EnrollmentStatus.SUCCESS;

        if (!fingerScanStatus.isRegisteredLocally()) {
            if (fingerScanStatus.isRegisteredNationally()) {
                registrationService.fetchFromMpiByNationalFpId(fingerScanStatus.getNationalBiometricSubject(), capture);
                localSubject.setSubjectId(fingerScanStatus.getNationalBiometricSubject().getSubjectId());
                enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
            } else {
                registrationService.registerLocally(localSubject, capture);
            }
        } else {
            localSubject.setSubjectId(fingerScanStatus.getLocalBiometricSubject().getSubjectId());
            enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
        }

        BiometricSubject nationalSubject = new BiometricSubject("");
        if (fingerScanStatus.isRegisteredNationally()) {
            nationalSubject = fingerScanStatus.getNationalBiometricSubject();
        } else if (nationalBioServerClient.isServerUrlConfigured()) {
            registrationService.registerNationally(capture);
            fingerScanStatus = searchService.checkIfFingerScanExists(capture);
            if (fingerScanStatus.isRegisteredNationally()) {
                nationalSubject = new BiometricSubject(fingerScanStatus.getNationalBiometricSubject().getSubjectId());
            }
        }

        Fingers fingers = capture.getFingerData(jaxbContext);
        localSubject.setFingerprints(fingers.toTwoOpenMrsFingerprints());
        nationalSubject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return new EnrollmentResult(localSubject, nationalSubject, enrollmentStatus);
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        M2SysCaptureResponse fingerScan = scanDoubleFingers();
        updateService.updateLocally(subject, fingerScan);

        if (nationalBioServerClient.isServerUrlConfigured()) {
            updateService.updateNationally(fingerScan);
        }

        Fingers fingers = fingerScan.getFingerData(jaxbContext);
        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        String response = localBioServerClient.changeId(oldId, newId);
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isChangeIdSuccess()) {
            throw new M2SysBiometricsException("Unable to change ID from " + oldId
                + " to " + newId);
        }

        return new BiometricSubject(newId);
    }

    @Override
    public List<BiometricMatch> search() {
        M2SysCaptureResponse fingerScan = scanDoubleFingers();
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan);
        List<BiometricMatch> results = new ArrayList<>();

        if (fingerScanStatus.isRegisteredLocally()) {
            results = searchService.searchLocally(fingerScan);
        }

        if (nationalBioServerClient.isServerUrlConfigured()) {
            try {
                if (fingerScanStatus.isRegisteredNationally()) {
                    List<BiometricMatch> nationalResults = searchService.searchNationally(fingerScan);
                    String biometricXml = fingerScan.getTemplateData();
                    nationalResults.forEach(nationalResult -> {
                        TempFingerprint fingerprint = new TempFingerprint(nationalResult.getSubjectId(), biometricXml);
                        tempFingerprintService.saveOrUpdate(fingerprint);
                    });
                    results.addAll(nationalResults);
                }
                if (fingerScanStatus.isRegisteredLocally()) {
                    registrationService.synchronizeFingerprints(fingerScan, fingerScanStatus);
                }
            } catch (RuntimeException exception) {
                getLogger().error("Connection failure to national server.", exception);
            }
        }


        return results;
    }

    @Override
    public BiometricSubject lookup(String subjectId) {
        String response = localBioServerClient.isRegistered(subjectId);
        M2SysResults results = XmlResultUtil.parse(response);

        return results.isLookupNotFound() ? null : new BiometricSubject(subjectId);
    }

    @Override
    public void delete(String subjectId) {
        String response = localBioServerClient.delete(subjectId);
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isDeleteSuccess()) {
            throw new M2SysBiometricsException("Unable to delete fingerprints for: " + subjectId);
        }
    }

    private M2SysCaptureResponse scanDoubleFingers() {
        M2SysCaptureResponse response = testEnvCaptor.scanDoubleFingers();

        if (response == null) {
            response = cloudScanrCaptor.scanDoubleFingers();
        } else {
            getLogger().warn("Using test template from the environment, skipping capture");
        }

        return response;
    }
}
