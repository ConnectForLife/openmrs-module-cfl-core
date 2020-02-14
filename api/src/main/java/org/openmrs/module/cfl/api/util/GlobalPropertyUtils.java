package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;

public final class GlobalPropertyUtils {

    private static Log log = LogFactory.getLog(GlobalPropertyUtils.class);

    public static String getGlobalProperty(String key) {
        return Context.getAdministrationService().getGlobalProperty(key);
    }

    public static void createGlobalSettingIfNotExists(String key, String value) {
        createGlobalSettingIfNotExists(key, value, null);
    }

    public static void createGlobalSettingIfNotExists(String key, String value, String description) {
        String existSetting = getGlobalProperty(key);
        if (org.apache.commons.lang3.StringUtils.isBlank(existSetting)) {
            GlobalProperty gp = new GlobalProperty(key, value, description);
            Context.getAdministrationService().saveGlobalProperty(gp);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Message Module created '%s' global property with value - %s", key, value));
            }
        }
    }

    public static boolean isTrue(String key) {
        String gp = getGlobalProperty(key);
        return StringUtils.isNotBlank(gp) && Boolean.valueOf(gp);
    }

    private GlobalPropertyUtils() { }
}
