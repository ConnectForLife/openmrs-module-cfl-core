package org.openmrs.module.cfl;

public final class CFLConstants {

    public static final String MODULE_ID = "cfl";

    public static final String ACTOR_TYPES_KEY = "messages.actor.types";

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

    public static final String SUPPORTED_ACTOR_TYPE = "cfl.supportedActorType";
    public static final String SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE = CAREGIVER_RELATIONSHIP_UUID;
    public static final String SUPPORTED_ACTOR_TYPE_DESCRIPTION = "The supported actor type which "
            + "will be used to determine if the redirection to separate dashboard should be shown.";

    public static final String SUPPORTED_ACTOR_TYPE_DIRECTION = "cfl.supportedActorType.actorPosition";
    public static final String SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE = "A";
    public static final String SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION = "Determine the position of actor in "
            + "the supported relationship type. Possible values: A, B.";

    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_NAME = "Person identifier";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_DESCRIPTION = "Person identifier attribute";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_FORMAT = "java.lang.String";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_UUID = "ffb6b2bc-ac7b-4807-8afd-f9464cb14003";

    public static final String PERSON_IDENTIFIER_ATTRIBUTE_KEY = "cfl.person.attribute.identifier";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE = "ffb6b2bc-ac7b-4807-8afd-f9464cb14003";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION = "Used to specify the UUID of person attribute "
            + "type which will be used to store the additional person identifier value.";

    public static final String PERSON_IDENTIFIER_SOURCE_KEY = "cfl.person.identifier.source";
    public static final String PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE = "1";
    public static final String PERSON_IDENTIFIER_SOURCE_DESCRIPTION = "Used to specify the ID or UUID of idgen identifier "
            + "source which will be used to generate person identifier.";

    public static final String USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS = "cfl.lastViewedPersonIds";
    public static final String EVENT_TOPIC_NAME_PERSON_VIEWED = "org.openmrs.module.cfl.api.event.PersonViewed";
    public static final String EVENT_KEY_PERSON_UUID = "personUuid";
    public static final String EVENT_KEY_USER_UUID = "userUuid";

    public static final String PERSON_HEADER_IDENTIFIER_LABEL_KEY = "cfl.personHeader.identifier.label";
    public static final String PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE = "cfl.personHeader.identifier.label";
    public static final String PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION = "Store the label for person id "
            + "displayed on person header.";

    public static final String HTML_FORM_DATE_FORMAT_KEY = "htmlformentry.dateFormat";
    public static final String HTML_FORM_DATE_FORMAT_DEFAULT_VALUE = "d M yy";
    public static final String HTML_FORM_DATE_FORMAT_DESCRIPTION = "Display dates in HTML Forms in jQuery (js)" +
     " date format. E.g. 'd M yy' for 31 Jan 2012.";

    private CFLConstants() { }
}
