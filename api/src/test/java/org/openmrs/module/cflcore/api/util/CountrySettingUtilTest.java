/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.cflcore.api.contract.CountrySetting;
import org.openmrs.module.cflcore.builder.LocationAttributeTypeBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

public class CountrySettingUtilTest extends BaseModuleContextSensitiveTest {

  @Mock private PatientService patientService;
  @Mock private LocationService locationService;
  @Mock private AdministrationService administrationService;
  @Mock private MessageSourceService messageSourceService;

  private Patient patient;

  private static final String COUNTRY_DECODED_ATTR_TYPE_NAME = "Country decoded";

  @Before
  public void setUp() throws Exception {
    executeDataSet("datasets/CountrySettingUtilTest_concepts.xml");
    executeDataSet("datasets/CountrySettingUtilTest.xml");

    contextMockHelper.setService(PatientService.class, patientService);
    contextMockHelper.setService(LocationService.class, locationService);
    contextMockHelper.setService(AdministrationService.class, administrationService);
    contextMockHelper.setService(AdministrationService.class, administrationService);
    when(locationService.getLocationAttributeTypeByName(COUNTRY_DECODED_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeTypeBuilder().build());

    contextMockHelper.setService(MessageSourceService.class, messageSourceService);
    when(messageSourceService.getMessage(any())).then(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void shouldReturnProperSettingsForArgentinaCountry() {
    patient = createPatient("Argentina");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
    CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

    Assert.assertThat(countrySetting.getCall(), is("voxeo"));
    Assert.assertThat(countrySetting.getSms(), is("turnIO"));
    Assert.assertFalse(countrySetting.isPerformCallOnPatientRegistration());
    Assert.assertFalse(countrySetting.isSendSmsOnPatientRegistration());
  }

  @Test
  public void shouldReturnProperSettingsForIndiaCountry() {
    patient = createPatient("India");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
    CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

    Assert.assertThat(countrySetting.getCall(), is("IMI_MOBILE"));
    Assert.assertThat(countrySetting.getSms(), is("turnIO"));
    Assert.assertTrue(countrySetting.isPerformCallOnPatientRegistration());
    Assert.assertTrue(countrySetting.isSendSmsOnPatientRegistration());
  }

  @Test
  public void shouldReturnProperSettingsForPolandCountry() {
    patient = createPatient("Poland");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
    CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

    Assert.assertThat(countrySetting.getCall(), is("voxeo"));
    Assert.assertThat(countrySetting.getSms(), is("turnIO"));
    Assert.assertTrue(countrySetting.isPerformCallOnPatientRegistration());
    Assert.assertFalse(countrySetting.isSendSmsOnPatientRegistration());
  }

  @Test
  public void shouldReturnDefaultSettingsForCountryThatDoesNotExistInMap() {
    patient = createPatient("China");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
    CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

    Assert.assertThat(countrySetting.getCall(), is("nexmo"));
    Assert.assertThat(countrySetting.getSms(), is("turnIO"));
    Assert.assertFalse(countrySetting.isPerformCallOnPatientRegistration());
    Assert.assertTrue(countrySetting.isSendSmsOnPatientRegistration());
  }

  private Patient createPatient(String countryValue) {
    final PersonAddress personAddress = new PersonAddress();
    personAddress.setCountry(countryValue);
    patient = new Patient();
    patient.addAddress(personAddress);
    return patient;
  }
}