/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.monitor;

/**
 * The ComponentMonitoringProvider Class is an interface of beans which provide Monitoring Status for components.
 * One implementation provides status of the one component.
 * <p>
 * The implementation must be a Spring bean.
 * </p>
 */
public interface ComponentMonitoringProvider {
    /**
     * Gets the name of component which this object provides status for.
     *
     * @return the component name, never null
     */
    String getComponentName();

    /**
     * Gets the priority of this instance.
     * <p>
     * In case if there are two implementations found for the same Component Name, the on with <b>higher</b> priority will
     * be used.
     * </p>
     *
     * @return the priority of this implementations
     */
    int getPriority();

    /**
     * Gets current status of the monitored component.
     * <p>
     * The implementor is responsible for:
     * <ul>
     * <li>handling transaction if transaction is needed to perform its job</li>
     * </ul>
     * </p>
     * <p>
     * Any Runtime Exception thrown by this method is interpreted as ERROR result.
     * </p>
     *
     * @return the Monitored Component Status, never null
     */
    MonitoredComponentStatusData getStatus();
}
