package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.PersonUtil;
import org.openmrs.module.messages.api.constants.ConfigConstants;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.event.CallFlowParamConstants;
import org.openmrs.module.messages.api.event.MessagesEvent;
import org.openmrs.module.messages.api.event.SmsEventParamConstants;
import org.openmrs.module.messages.api.service.MessagesEventService;
import org.openmrs.module.messages.api.service.impl.VelocityNotificationTemplateServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.messages.api.event.CallFlowParamConstants.CONFIG;
import static org.openmrs.module.messages.api.event.CallFlowParamConstants.FLOW_NAME;
import static org.openmrs.module.messages.api.event.CallFlowParamConstants.ADDITIONAL_PARAMS;

public class WelcomeServiceImpl implements WelcomeService {

    private static final String MESSAGES_EVENT_SERVICE_BEAN_NAME = "messages.messagesEventService";
    private static final String VELOCITY_NOTIFICATION_TEMPLATE_SERVICE_BEAN_NAME =
            "messages.velocityNotificationTemplateServiceImpl";
    private static final String SMS_INITIATE_EVENT = "send_sms";
    private static final String PATIENT_ACTOR_TYPE = "patient";
    private static final String PATIENT_PARAM = "patient";

    @Override
    public void sendWelcomeMessages(Person person) {
        if (StringUtils.isNotBlank(PersonUtil.getPhoneNumber(person))) {
            if (isSmsEnabled()) {
                sendSms(person);
            }
            if (isCallEnabled()) {
                performCall(person);
            }
        }
    }

    private boolean isSmsEnabled() {
        return Boolean.parseBoolean(getAdministrationService()
                .getGlobalProperty(CFLConstants.SEND_SMS_ON_PATIENT_REGISTRATION_KEY));
    }

    private void sendSms(Person person) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SmsEventParamConstants.RECIPIENTS,
                new ArrayList<String>(Collections.singletonList(PersonUtil.getPhoneNumber(person))));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PATIENT_PARAM, Context.getPatientService().getPatient(person.getPersonId()));
        properties.put(SmsEventParamConstants.MESSAGE, getWelcomeMessage(params));

        Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
                .sendEventMessage(new MessagesEvent(SMS_INITIATE_EVENT, properties));
    }

    private String getWelcomeMessage(Map<String, Object> params) {
        return Context.getRegisteredComponent(VELOCITY_NOTIFICATION_TEMPLATE_SERVICE_BEAN_NAME,
                VelocityNotificationTemplateServiceImpl.class).buildMessageByPatient(params);
    }

    private boolean isCallEnabled() {
        return Boolean.parseBoolean(getAdministrationService()
                .getGlobalProperty(CFLConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_KEY));
    }

    private void performCall(Person person) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CONFIG, getCallConfig());
        params.put(FLOW_NAME, getCallFlowName());

        Map<String, Object> additionalParams = new HashMap<String, Object>();
        additionalParams.put(CallFlowParamConstants.ACTOR_TYPE, PATIENT_ACTOR_TYPE);
        additionalParams.put(CallFlowParamConstants.PHONE, PersonUtil.getPhoneNumber(person));
        additionalParams.put(CallFlowParamConstants.ACTOR_ID, person.getPersonId().toString());
        additionalParams.put(CallFlowParamConstants.REF_KEY, person.getPersonId().toString());

        params.put(ADDITIONAL_PARAMS, additionalParams);

        Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
                .sendEventMessage(new MessagesEvent(MessagesConstants.CALL_FLOW_INITIATE_CALL_EVENT, params));
    }

    private String getCallFlowName() {
        return getAdministrationService().getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
    }

    private String getCallConfig() {
        return getAdministrationService().getGlobalProperty(ConfigConstants.CALL_CONFIG,
                ConfigConstants.CALL_CONFIG_DEFAULT_VALUE);
    }

    private AdministrationService getAdministrationService() {
        return Context.getAdministrationService();
    }
}