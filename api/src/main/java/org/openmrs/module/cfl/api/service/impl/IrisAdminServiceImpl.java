/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisAdminService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisAdminServiceImpl extends BaseOpenmrsService implements IrisAdminService {

    private AdministrationService adminService;

    public void setAdminService(AdministrationService adminService) {
        this.adminService = adminService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setGlobalProperty(String key, String val) {
        adminService.setGlobalProperty(key, val);
    }
}
