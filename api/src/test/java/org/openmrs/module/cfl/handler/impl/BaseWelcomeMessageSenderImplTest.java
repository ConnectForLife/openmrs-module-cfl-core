package org.openmrs.module.cfl.handler.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.impl.ConfigServiceImpl;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class BaseWelcomeMessageSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {

    @Spy
    private ConfigServiceImpl configService = new ConfigServiceImpl();

    @Before
    public void setUp() {
        configService.setPersonService(personService);
    }

    @Test
    public void send_shouldScheduleSendingForNow() {
        final CountrySetting testCountrySetting = new CountrySetting();
        testCountrySetting.setPatientNotificationTimeWindowFrom("10:00");
        testCountrySetting.setPatientNotificationTimeWindowTo("18:00");

        final TestWelcomeMessageSenderImpl testWelcomeMessageSender = prepareTestWelcomeMessageSenderImpl();

        // When
        testWelcomeMessageSender.send(testPatient, testCountrySetting);

        // Then
        ArgumentCaptor<ScheduledExecutionContext> argumentCaptor = ArgumentCaptor.forClass(ScheduledExecutionContext.class);
        verify(messagesDeliveryService).scheduleDelivery(argumentCaptor.capture());

        final ScheduledExecutionContext resultScheduledExecutionContext = argumentCaptor.getValue();
        assertThat(resultScheduledExecutionContext.getChannelType(), is("TEST"));
        assertThat(resultScheduledExecutionContext.getActorId(), is(testPatient.getId()));
        assertThat(resultScheduledExecutionContext.getActorType(), is("Patient"));
        assertThat(resultScheduledExecutionContext.getPatientId(), is(testPatient.getId()));
        assertNotNull(resultScheduledExecutionContext.getServiceIdsToExecute());
        assertThat(resultScheduledExecutionContext.getServiceIdsToExecute().size(), is(1));
        assertThat(Date.from(resultScheduledExecutionContext.getExecutionDate()), is(now));
    }

    @Test
    public void send_shouldScheduleSendingForTomorrowBestContactTime() {
        final CountrySetting testCountrySetting = new CountrySetting();
        testCountrySetting.setPatientNotificationTimeWindowFrom("00:00");
        testCountrySetting.setPatientNotificationTimeWindowTo("00:00");

        final TestWelcomeMessageSenderImpl testWelcomeMessageSender = prepareTestWelcomeMessageSenderImpl();

        // When
        testWelcomeMessageSender.send(testPatient, testCountrySetting);

        // Then
        ArgumentCaptor<ScheduledExecutionContext> argumentCaptor = ArgumentCaptor.forClass(ScheduledExecutionContext.class);
        verify(messagesDeliveryService).scheduleDelivery(argumentCaptor.capture());

        final ScheduledExecutionContext resultScheduledExecutionContext = argumentCaptor.getValue();
        assertThat(resultScheduledExecutionContext.getChannelType(), is("TEST"));
        assertThat(resultScheduledExecutionContext.getActorId(), is(testPatient.getId()));
        assertThat(resultScheduledExecutionContext.getActorType(), is("Patient"));
        assertThat(resultScheduledExecutionContext.getPatientId(), is(testPatient.getId()));
        assertNotNull(resultScheduledExecutionContext.getServiceIdsToExecute());
        assertThat(resultScheduledExecutionContext.getServiceIdsToExecute().size(), is(1));
        assertThat(Date.from(resultScheduledExecutionContext.getExecutionDate()), is(tomorrowBestContactTime));
    }

    private TestWelcomeMessageSenderImpl prepareTestWelcomeMessageSenderImpl() {
        final TestWelcomeMessageSenderImpl testWelcomeMessageSender = new TestWelcomeMessageSenderImpl("TEST", true);
        testWelcomeMessageSender.setMessagesDeliveryService(messagesDeliveryService);
        testWelcomeMessageSender.setMessagingGroupService(messagingGroupService);
        testWelcomeMessageSender.setPatientTemplateService(patientTemplateService);
        testWelcomeMessageSender.setPersonService(personService);
        testWelcomeMessageSender.setTemplateService(templateService);
        testWelcomeMessageSender.setConfigService(configService);
        return testWelcomeMessageSender;
    }
}
