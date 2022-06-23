/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.action;

import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

/**
 * The VisitStatusUpdateActionAdapter Class.
 * <p>
 * This implementation delegates work to {@link VisitStatusUpdateActionBridge} bean which only exists if the visits
 * module is loaded. The VisitStatusUpdateActionBridge delegates to the actual Update Action implemented in visits
 * module. If there is no visits module, then this Form Submission Action does nothing.
 * </p>
 */
public class VisitStatusUpdateActionAdapter implements CustomFormSubmissionAction {
    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Context
                .getRegisteredComponents(VisitStatusUpdateActionBridge.class)
                .stream()
                .findFirst()
                .ifPresent(c -> c.applyAction(formEntrySession));
    }
}