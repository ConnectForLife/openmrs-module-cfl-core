package org.openmrs.module.cfl.api.contract;

public class CountrySetting {
  private String sms;
  private String call;
  private boolean performCallOnPatientRegistration;
  private boolean sendSmsOnPatientRegistration;
  private boolean shouldSendReminderViaCall;
  private boolean shouldSendReminderViaSms;
  private boolean shouldCreateFirstVisit;
  private boolean shouldCreateFutureVisit;
  private String patientNotificationTimeWindowFrom;
  private String patientNotificationTimeWindowTo;

  public CountrySetting() {}

  @SuppressWarnings({"checkstyle:ParameterNumber", "PMD.ExcessiveParameterList"})
  CountrySetting(
      String sms,
      String call,
      boolean performCallOnPatientRegistration,
      boolean sendSmsOnPatientRegistration,
      boolean shouldSendReminderViaCall,
      boolean shouldSendReminderViaSms,
      boolean shouldCreateFirstVisit,
      boolean shouldCreateFutureVisit,
      String patientNotificationTimeWindowFrom,
      String patientNotificationTimeWindowTo) {
    this.sms = sms;
    this.call = call;
    this.performCallOnPatientRegistration = performCallOnPatientRegistration;
    this.sendSmsOnPatientRegistration = sendSmsOnPatientRegistration;
    this.shouldSendReminderViaCall = shouldSendReminderViaCall;
    this.shouldSendReminderViaSms = shouldSendReminderViaSms;
    this.shouldCreateFirstVisit = shouldCreateFirstVisit;
    this.shouldCreateFutureVisit = shouldCreateFutureVisit;
    this.patientNotificationTimeWindowFrom = patientNotificationTimeWindowFrom;
    this.patientNotificationTimeWindowTo = patientNotificationTimeWindowTo;
  }

  public String getSms() {
    return sms;
  }

  public void setSms(String sms) {
    this.sms = sms;
  }

  public String getCall() {
    return call;
  }

  public void setCall(String call) {
    this.call = call;
  }

  public boolean isPerformCallOnPatientRegistration() {
    return performCallOnPatientRegistration;
  }

  public void setPerformCallOnPatientRegistration(boolean performCallOnPatientRegistration) {
    this.performCallOnPatientRegistration = performCallOnPatientRegistration;
  }

  public boolean isSendSmsOnPatientRegistration() {
    return sendSmsOnPatientRegistration;
  }

  public void setSendSmsOnPatientRegistration(boolean sendSmsOnPatientRegistration) {
    this.sendSmsOnPatientRegistration = sendSmsOnPatientRegistration;
  }

  public boolean isShouldSendReminderViaCall() {
    return shouldSendReminderViaCall;
  }

  public void setShouldSendReminderViaCall(boolean shouldSendReminderViaCall) {
    this.shouldSendReminderViaCall = shouldSendReminderViaCall;
  }

  public boolean isShouldSendReminderViaSms() {
    return shouldSendReminderViaSms;
  }

  public void setShouldSendReminderViaSms(boolean shouldSendReminderViaSms) {
    this.shouldSendReminderViaSms = shouldSendReminderViaSms;
  }

  public boolean isShouldCreateFirstVisit() {
    return shouldCreateFirstVisit;
  }

  public void setShouldCreateFirstVisit(boolean shouldCreateFirstVisit) {
    this.shouldCreateFirstVisit = shouldCreateFirstVisit;
  }

  public boolean isShouldCreateFutureVisit() {
    return shouldCreateFutureVisit;
  }

  public void setShouldCreateFutureVisit(boolean shouldCreateFutureVisit) {
    this.shouldCreateFutureVisit = shouldCreateFutureVisit;
  }

  public String getPatientNotificationTimeWindowFrom() {
    return patientNotificationTimeWindowFrom;
  }

  public void setPatientNotificationTimeWindowFrom(String patientNotificationTimeWindowFrom) {
    this.patientNotificationTimeWindowFrom = patientNotificationTimeWindowFrom;
  }

  public String getPatientNotificationTimeWindowTo() {
    return patientNotificationTimeWindowTo;
  }

  public void setPatientNotificationTimeWindowTo(String patientNotificationTimeWindowTo) {
    this.patientNotificationTimeWindowTo = patientNotificationTimeWindowTo;
  }
}
