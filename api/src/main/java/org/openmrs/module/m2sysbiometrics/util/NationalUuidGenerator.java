package org.openmrs.module.m2sysbiometrics.util;

import java.util.UUID;

import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NationalUuidGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(NationalUuidGenerator.class);

	@Autowired
	private NationalBioServerClient nationalBioServerClient;

	public String generate() {
		boolean isRegistered;
		String uuid;
		LOGGER.info("About to fetch UUID");
		do {
			// unlikely it will repeat, but we must guard against it
			uuid = UUID.randomUUID().toString();
			String response = nationalBioServerClient.isRegistered(uuid);
			isRegistered = response.equals("YES");
		} while (isRegistered);

		return uuid;
	}
}
