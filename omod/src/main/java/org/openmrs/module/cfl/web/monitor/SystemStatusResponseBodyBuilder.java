package org.openmrs.module.cfl.web.monitor;

import org.openmrs.module.cfl.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The SystemStatusResponseBodyBuilder Class provides static method to get instances of {@link SystemStatusResponseBody}.
 */
public final class SystemStatusResponseBodyBuilder {
    private SystemStatusResponseBodyBuilder() {

    }

    /**
     * Creates new instance of SystemStatusResponseBody containing only the status from {@code allMonitoredStatusData}.
     *
     * @param allMonitoredStatusData the All Monitored Status data, not null
     * @return new instance of SystemStatusResponseBody, never null
     * @throws IllegalArgumentException if {@code allMonitoredStatusData} is null
     * @see #withAll(AllMonitoredStatusData)
     */
    public static SystemStatusResponseBody withStatusOnly(final AllMonitoredStatusData allMonitoredStatusData) {
        if (allMonitoredStatusData == null) {
            throw new IllegalArgumentException("'allMonitoredStatusData' must not be null!");
        }

        return new SystemStatusResponseBodyStatusOnly(allMonitoredStatusData.getMonitoringStatus().toString());
    }

    /**
     * Creates new instance of SystemStatusResponseBody initialized from {@code allMonitoredStatusData}.
     *
     * @param allMonitoredStatusData the All Monitored Status data, not null
     * @return new instance of SystemStatusResponseBody, never null
     * @throws IllegalArgumentException if {@code allMonitoredStatusData} is null
     * @see #withStatusOnly(AllMonitoredStatusData)
     */
    public static SystemStatusResponseBody withAll(final AllMonitoredStatusData allMonitoredStatusData) {
        if (allMonitoredStatusData == null) {
            throw new IllegalArgumentException("'allMonitoredStatusData' must not be null!");
        }

        final List<SystemStatusResponseBodyWithMessages.Message> messages =
                new ArrayList<>();

        for (final Map.Entry<String, MonitoredComponentStatusData> componentsStatusEntry : allMonitoredStatusData
                .getComponentsStatus().entrySet()) {
            messages.add(new SystemStatusResponseBodyWithMessages.Message(componentsStatusEntry.getKey(),
                    componentsStatusEntry.getValue().getMessage()));
        }

        return new SystemStatusResponseBodyWithMessages(allMonitoredStatusData.getMonitoringStatus().toString(),
                Collections.unmodifiableList(messages));
    }

    /**
     * Creates new instance of SystemStatusResponseBody containing the OK status.
     *
     * @return new instance of SystemStatusResponseBody, never null
     * @throws IllegalArgumentException if {@code allMonitoredStatusData} is null
     */
    public static SystemStatusResponseBody statusOk() {
        return new SystemStatusResponseBodyStatusOnly(MonitoringStatus.OK.toString());
    }
}
