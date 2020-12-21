package org.openmrs.module.m2sysbiometrics.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV105Client extends AbstractM2SysClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysV105Client.class);
    private final Log log = LogFactory.getLog(M2SysV105Client.class);

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
    public EnrollmentResult enroll(BiometricSubject subjectId) {
        M2SysCaptureResponse capture = scanDoubleFingers();
        return enroll(subjectId, capture.getTemplateData());
    }

    @Override
    public EnrollmentResult enroll(BiometricSubject subjectId, String fingerprintXmlTemplate) {
        BiometricSubject nationalSubject = new BiometricSubject("");
        M2SysCaptureResponse capture = convertXmlTemplateToCapture(fingerprintXmlTemplate);
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
        EnrollmentStatus enrollmentStatus = EnrollmentStatus.SUCCESS;
        Fingers fingers = capture.getFingerData(jaxbContext);
        subjectId.setFingerprints(fingers.toTwoOpenMrsFingerprints());
        nationalSubject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        if (!fingerScanStatus.isRegisteredLocally()) {
            if (fingerScanStatus.isRegisteredNationally()) {
                nationalSubject = fingerScanStatus.getNationalBiometricSubject();
                enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
                if(localBioServerClient.isServerUrlConfigured()){
                    registrationService.registerLocally(subjectId);
                    log.error("Local EnrollmentStatus : "+EnrollmentStatus.SUCCESS);
                }else{
                    log.error("Local Enrollment Aborted: Status - Local Server not configured!");
                    subjectId = new BiometricSubject("");;
                }
            } else {
//                register locally and nationally
                if(localBioServerClient.isServerUrlConfigured()){
                    registrationService.registerLocally(subjectId);
                    enrollmentStatus = EnrollmentStatus.SUCCESS;
                    log.error("Local EnrollmentStatus : "+EnrollmentStatus.SUCCESS);
                }else{
                    log.error("Local Enrollment Aborted: Status - Local Server not configured!");
                    subjectId = new BiometricSubject("");;
                }

                if(nationalBioServerClient.isServerUrlConfigured()){
                    registrationService.registerNationally(capture);
                    enrollmentStatus = EnrollmentStatus.SUCCESS;
                    log.error("National EnrollmentStatus : "+EnrollmentStatus.SUCCESS);
                }else{
                    log.error("National Enrollment Aborted: Status - National Server not configured!");
                }

//                retrieve the registered biometrics
                fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
                if (fingerScanStatus.isRegisteredNationally()) {
                    nationalSubject = fingerScanStatus.getNationalBiometricSubject();
                }

            }
        } else {
            subjectId = fingerScanStatus.getLocalBiometricSubject();
            enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
            if (!fingerScanStatus.isRegisteredNationally()) {
//                Enroll nationally
                registrationService.registerNationally(capture);
                fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
                if (fingerScanStatus.isRegisteredNationally()) {
                    nationalSubject = fingerScanStatus.getNationalBiometricSubject();
                }

            }else{
                nationalSubject = fingerScanStatus.getNationalBiometricSubject();
            }
        }
        return new EnrollmentResult(subjectId, nationalSubject, enrollmentStatus);
    }

    private M2SysCaptureResponse convertXmlTemplateToCapture(String fingerprintXmlTemplate) {
        M2SysCaptureResponse capture = new M2SysCaptureResponse();
        capture.setTemplateData(fingerprintXmlTemplate);
        return capture;
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        M2SysCaptureResponse fingerScan = scanDoubleFingers();
        updateService.updateLocally(subject);

     /*   if (nationalBioServerClient.isServerUrlConfigured()) {
            updateService.updateNationally(fingerScan);
        }
*/
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
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan.getTemplateData());
        List<BiometricMatch> results = new ArrayList<>();

        if (fingerScanStatus.isRegisteredLocally()) {
            results = searchService.searchLocally(fingerScan.getTemplateData());
        }

        if (nationalBioServerClient.isServerUrlConfigured()) {
            try {
                if (fingerScanStatus.isRegisteredNationally()) {
                    List<BiometricMatch> nationalResults = searchService.searchNationally(fingerScan.getTemplateData());
                    String biometricXml = fingerScan.getTemplateData();
                    for (BiometricMatch nationalResult : nationalResults) {
                        TempFingerprint fingerprint = new TempFingerprint(nationalResult.getSubjectId(), biometricXml);
                        tempFingerprintService.saveOrUpdate(fingerprint);
                    }
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
