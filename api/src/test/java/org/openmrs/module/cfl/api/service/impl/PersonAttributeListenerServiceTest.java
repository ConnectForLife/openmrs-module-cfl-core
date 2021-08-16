package org.openmrs.module.cfl.api.service.impl;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.VisitService;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.test.BaseContextMockTest;

import java.util.Date;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.module.cfl.CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME;

public class PersonAttributeListenerServiceTest extends BaseContextMockTest {

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private ConfigService configService;

    @Mock
    private VisitService visitService;

    @Mock
    private VaccinationService vaccinationService;

    @InjectMocks
    private PersonAttributeListenerServiceImpl personAttributeListenerService;

    @Test
    public void onPersonAttributeEvent_whenVisitListIsEmpty() {
        final Person person = PersonHelper.createPerson();
        final Patient patient = PatientHelper.createPatient(person, null);

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(emptyList());
        when(configService.getVaccinationProgram(patient)).thenReturn(
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME).getValue());

        personAttributeListenerService.onPersonAttributeEvent(Event.Action.UPDATED,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO, times(1)).getPatientByUuid(person.getUuid());
        verify(visitService, times(1)).getActiveVisitsByPatient(patient);
        verifyZeroInteractions(vaccinationService);
    }

    @Test
    public void onPersonAttributeEvent_shouldSetDateChangedForCreated() {
        internalShouldSetDateChangedFor(Event.Action.CREATED);
    }

    @Test
    public void onPersonAttributeEvent_shouldSetDateChangedForUpdated() {
        internalShouldSetDateChangedFor(Event.Action.UPDATED);
    }

    @Test
    public void onPersonAttributeEvent_shouldSetDateChangedForVoided() {
        internalShouldSetDateChangedFor(Event.Action.VOIDED);
    }

    private void internalShouldSetDateChangedFor(Event.Action action) {
        final Person person = PersonHelper.createPerson();
        final Patient patient = Mockito.spy(PatientHelper.createPatient(person, null));

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);

        personAttributeListenerService.onPersonAttributeEvent(action,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO, times(1)).getPatientByUuid(person.getUuid());
        verify(patient, times(1)).setDateChanged(any(Date.class));
        verify(patient, times(1)).setChangedBy(any(User.class));
        verify(patientDAO, times(1)).savePatient(patient);
    }

}
