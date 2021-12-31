package org.openmrs.module.m2sysbiometrics;

import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath*:applicationContext-service.xml", "classpath*:moduleApplicationContext.xml",
		"classpath:m2sys-biometric-test-applicationContext.xml" }, inheritLocations = false)
public class M2SysBiometricSensitiveTestBase extends BaseModuleContextSensitiveTest {

}
