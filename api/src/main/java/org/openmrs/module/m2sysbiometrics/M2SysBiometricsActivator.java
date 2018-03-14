/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.m2sysbiometrics;

import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.m2sysbiometrics.scheduler.impl.M2SysSchedulerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class M2SysBiometricsActivator extends BaseModuleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysBiometricsActivator.class);

    @Override
    public void started() {
        LOGGER.info("Starting m2sys Biometrics Module");
        Context.getRegisteredComponents(M2SysSchedulerServiceImpl.class).get(0).runM2SysScheduler();
    }

    @Override
    public void stopped() {
        LOGGER.info("Shutting down m2sys Biometrics Module");
    }

}
