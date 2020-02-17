package org.openmrs.module.cfl;

public final class CFLConstants {

    public static final String MODULE_ID = "cfl";

    public static final String PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME = "cfl.redirectToPersonDashboard";

    public static final String PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE = "true";

    public static final String PATIENT_DASHBOARD_REDIRECT_DESCRIPTION = "true/false flag that decides if CFL module can" +
            " override the patient's coreapps dashboard by redirecting to CFLm person dashboard. Default value: true";

    public static final String LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME = "locationbasedaccess.locationAttributeUuid";

    public static final String LOCATION_ATTRIBUTE_TYPE_UUID = "bfdf8ed0-87e3-437e-897d-81434393a233";

    public static final String TELEPHONE_ATTRIBUTE_NAME = "Telephone Number";

    public static final String DISABLED_CONTROL_KEY = "cfl.shouldDisableAppsAndExtensions";

    public static final String DISABLED_CONTROL_DEFAULT_VALUE = "false";

    public static final String DISABLED_CONTROL_DESCRIPTION = "Used to determine if the module should disable "
            + "specified apps and extensions on module startup. Possible values: true/false. Note: the server need to "
            + "be restart after change this GP and in order to revert those changes you need to manually clean the "
            + "appframework_component_state table";

    public static final String CAREGIVER_RELATIONSHIP_UUID = "acec590b-825e-45d2-876a-0028f174903d";

    public static final String TRUE = "true";

    public static final String DATETIME_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String POSSIBLE_RELATIONSHIP_TYPES_KEY = "cfl.possibleRelationshipTypes";

    public static final String POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION = "Comma separate list of relationship types "
            + "UUIDs which can be use with the CFL person relationship fragment. "
            + "If null then all possible types will be used.";

    public static final String POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE = "";

    private CFLConstants() { }
}
