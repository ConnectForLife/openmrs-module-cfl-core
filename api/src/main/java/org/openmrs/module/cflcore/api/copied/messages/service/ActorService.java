/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.copied.messages.service;

import org.openmrs.Person;
import org.openmrs.module.cflcore.api.copied.messages.model.ActorType;

import java.util.List;

public interface ActorService {

    boolean isInCaregiverRole(Person patient);

    List<ActorType> getAllActorTypes();
}
