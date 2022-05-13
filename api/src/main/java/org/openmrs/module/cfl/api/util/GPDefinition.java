/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.util;

public class GPDefinition {

    private final String key;
    private final String defaultValue;
    private final String description;

    public GPDefinition(String key, String defaultValue, String description) {
        this(key, defaultValue, description, false);
    }

    public GPDefinition(String key, String defaultValue, String description, boolean addDefaultValueToDesc) {
        this.key = key;
        this.defaultValue = defaultValue;
        if (addDefaultValueToDesc) {
            this.description = description + String.format(" Default value: '%s'", defaultValue);
        } else {
            this.description = description;
        }
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
}
