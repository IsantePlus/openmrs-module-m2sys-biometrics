package org.openmrs.module.m2sysbiometrics.util;

import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NationalUuidGenerator {

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    public String generate() {
        boolean isRegistered;
        String uuid;
        do {
            // unlikely it will repeat, but we must guard against it
            uuid = UUID.randomUUID().toString();

            String response = nationalBioServerClient.isRegistered(uuid);
            M2SysResults results = XmlResultUtil.parse(response);

            isRegistered = !results.isLookupNotFound();
        } while (isRegistered);

        return uuid;
    }
}
