package org.openmrs.module.cfl.api.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.RandomizationRegimen;
import org.openmrs.module.cfl.api.contract.Vaccine;
import org.openmrs.module.cfl.api.contract.Regimen;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.dto.RegimensPatientsDataDTO;
import org.openmrs.module.cfl.api.service.CFLPatientService;
import org.openmrs.module.cfl.api.service.CFLVisitService;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.PatientUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccinationServiceImpl implements VaccinationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaccinationServiceImpl.class);

    private static final String REGIMEN_CHANGE = "REGIMEN CHANGE";

    private static final String VISITS_FIELD_NAME = "visits";

    private static final String NUMBER_OF_DOSE_FIELD_NAME = "numberOfDose";

    private static final Integer BATCH_SIZE = 100;

    @Transactional
    @Override
    public void createFutureVisits(Visit occurredVisit, Date occurrenceDateTime) {
        final Patient patient = occurredVisit.getPatient();
        final Vaccination vaccination = getVaccinationForPatient(patient);
        if (vaccination != null) {
            rescheduleVisits(occurredVisit, occurrenceDateTime, vaccination);
        } else {
            LOGGER.warn(String.format("Vaccination program for patient with name: %s and id: %d not found",
                    PatientUtil.getPatientFullName(patient), patient.getId()));
        }
    }

    @Transactional
    @Override
    public void voidFutureVisits(Patient patient) {
        List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);

        for (Visit visit : visits) {
            if (CFLConstants.SCHEDULED_VISIT_STATUS.equals(VisitUtil.getVisitStatus(visit))) {
                Context.getVisitService().voidVisit(visit, REGIMEN_CHANGE);
            }
        }
    }

    @Transactional
    @Override
    public void rescheduleVisits(Visit latestDosingVisit, Patient patient) {
        voidFutureVisits(patient);
        createFutureVisits(latestDosingVisit, latestDosingVisit.getStartDatetime());
    }

    @Transactional
    @Override
    public void rescheduleVisitsBasedOnRegimenChanges(String previousVaccineGPValue, String currentVaccineGPValue) {
        Map<String, Boolean> regimensDiffsMap = getRegimensDiffsMap(previousVaccineGPValue, currentVaccineGPValue);
        processRegimensChanges(regimensDiffsMap);
    }

    @Transactional
    @Override
    public List<RegimensPatientsDataDTO> getResultsList(String regimenGP) {
        List<RegimensPatientsDataDTO> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(regimenGP)) {
            Randomization randomization = new Randomization(getGson().fromJson(regimenGP, Vaccination[].class));
            for (Vaccination vaccination : randomization.getVaccinations()) {
                List<Patient> patients = getCFLPatientService().findByVaccinationName(vaccination.getName());
                List<String> patientsUuids = getPatientsUuids(patients);
                resultList.add(new RegimensPatientsDataDTO(vaccination.getName(), patientsUuids, patients.size(),
                        CollectionUtils.isNotEmpty(patients))
                );
            }
        }
        return resultList;
    }

    @Transactional
    @Override
    public void rescheduleRegimenVisitsByPatient(Patient patient) {
        List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);
        if (CollectionUtils.isNotEmpty(visits)) {
            Visit lastOccurredDosingVisit = VisitUtil.getLastOccurredDosingVisit(visits);
            if (lastOccurredDosingVisit != null) {
                rescheduleVisits(lastOccurredDosingVisit, patient);
            }
        }
    }

    @Transactional
    @Override
    public List<RegimensPatientsDataDTO> getRegimenResultsList(String configGP) {
        List<RegimensPatientsDataDTO> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(configGP)) {
            RandomizationRegimen randomizationRegimen = new RandomizationRegimen(getGson().
                                                    fromJson(configGP, Regimen.class));

            Regimen regimen = randomizationRegimen.getRegimens();
            for (Vaccine vaccine : regimen.getVaccine()) {
                List<Patient> patients = getCFLPatientService().findByVaccinationName(vaccine.getName());
                List<String> patientsUuids = getPatientsUuids(patients);
                resultList.add(new RegimensPatientsDataDTO(vaccine.getName(), patientsUuids, patients.size(),
                        CollectionUtils.isNotEmpty(patients))
                );
            }
        }

        return resultList;
    }

    private void rescheduleVisits(Visit occurredVisit, Date occurrenceDateTime, Vaccination vaccination) {
        final int totalNumberOfDoses = vaccination.getNumberOfDose();

        final CountrySetting countrySetting = CountrySettingUtil.
                getCountrySettingForPatient(occurredVisit.getPatient().getPerson());

        final Visit lastDosingVisit = VisitUtil.getLastDosingVisit(occurredVisit.getPatient(), vaccination);

        // should it create future visits and is this the last dosage visit that this patient had scheduled and it is not
        // the last visit in program
        if (countrySetting.isShouldCreateFutureVisit() && occurredVisit.equals(lastDosingVisit) &&
                !isLastVisit(totalNumberOfDoses, occurredVisit)) {

            final List<VisitInformation> futureVisits = getInformationForFutureVisits(occurredVisit, vaccination);

            for (VisitInformation futureVisit : futureVisits) {
                prepareDataAndSaveVisit(occurredVisit.getPatient(), occurrenceDateTime, futureVisit);
            }
        }
    }

    private List<String> getPatientsUuids(List<Patient> patients) {
        List<String> patientUuids = new ArrayList<>();
        for (Patient patient : patients) {
            patientUuids.add(patient.getUuid());
        }
        return patientUuids;
    }

    private void processRegimensChanges(Map<String, Boolean> regimensDiffsMap) {
        for (Map.Entry<String, Boolean> entry : regimensDiffsMap.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                List<Patient> patients = getCFLPatientService().findByVaccinationName(entry.getKey());
                int numberOfIterations = (int) Math.ceil((float) patients.size() / BATCH_SIZE);
                for (int i = 0; i < numberOfIterations; i++) {
                    int endIndex = Math.min((i + 1) * BATCH_SIZE, patients.size());
                    List<Patient> subList = patients.subList(i * BATCH_SIZE, endIndex);
                    getCFLVisitService().rescheduleVisitsByPatients(subList);
                }
            }
        }
    }

    private Map<String, Boolean> getRegimensDiffsMap(String previousValue, String newValue) {
        Map<String, Map<String, Object>> previousValuesMap = createVaccinationMap(previousValue);
        Map<String, Map<String, Object>> newValuesMap = createVaccinationMap(newValue);

        Map<String, Boolean> resultMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : newValuesMap.entrySet()) {
            String vaccineName = entry.getKey();
            if (previousValuesMap.containsKey(vaccineName)) {
                Map<String, Object> previousVaccinationMap = previousValuesMap.get(vaccineName);
                Map<String, Object> newVaccinationMap = entry.getValue();
                resultMap.put(vaccineName, isVaccinesConfigChanged(previousVaccinationMap, newVaccinationMap));
            }
        }
        return resultMap;
    }

    private boolean isVaccinesConfigChanged(Map<String, Object> previousVaccinationMap,
                                            Map<String, Object> newVaccinationMap) {
        List<VisitInformation> previousVisits = (List<VisitInformation>) previousVaccinationMap.get(VISITS_FIELD_NAME);
        List<VisitInformation> newVisits = (List<VisitInformation>) newVaccinationMap.get(VISITS_FIELD_NAME);
        boolean isNumberOfDoseChanged = isNumberOfDoseChanged(previousVaccinationMap, newVaccinationMap);

        if (previousVisits.size() != newVisits.size() || isNumberOfDoseChanged) {
            return true;
        }

        for (VisitInformation newVisit : newVisits) {
            if (!previousVisits.contains(newVisit)) {
                return true;
            }
        }

        return false;
    }

    private Map<String, Map<String, Object>> createVaccinationMap(String gpValue) {
        Map<String, Map<String, Object>> vaccinationValueMap = new HashMap<>();
        if (StringUtils.isNotBlank(gpValue)) {
            Randomization randomization = new Randomization(getGson().fromJson(gpValue, Vaccination[].class));
            for (Vaccination vaccination : randomization.getVaccinations()) {
                Map<String, Object> innerMap = new HashMap<>();
                innerMap.put(VISITS_FIELD_NAME, vaccination.getVisits());
                innerMap.put(NUMBER_OF_DOSE_FIELD_NAME, vaccination.getNumberOfDose());
                vaccinationValueMap.put(vaccination.getName(), innerMap);
            }
        }

        return vaccinationValueMap;
    }

    private boolean isNumberOfDoseChanged(Map<String, Object> previousVaccinationMap,
                                          Map<String, Object> newVaccinationMap) {
        return !previousVaccinationMap.get(NUMBER_OF_DOSE_FIELD_NAME)
                .equals(newVaccinationMap.get(NUMBER_OF_DOSE_FIELD_NAME));
    }

    private Vaccination getVaccinationForPatient(Patient patient) {
        final Randomization randomization = getConfigService().getRandomizationGlobalProperty();
        final String patientVaccinationProgram = getConfigService().getVaccinationProgram(patient);
        return randomization.findByVaccinationProgram(patientVaccinationProgram);
    }

    private boolean isLastVisit(int totalNumberOfDoses, Visit occurredVisit) {
        final VisitAttribute visitDoseNumberAttr = VisitUtil.getDoseNumberAttr(occurredVisit);
        return visitDoseNumberAttr != null &&
                totalNumberOfDoses == Integer.parseInt(visitDoseNumberAttr.getValueReference());
    }

    private void prepareDataAndSaveVisit(Patient patient, Date occurrenceDateTime, VisitInformation futureVisit) {
        final Visit visit = VisitUtil.createVisitResource(patient, occurrenceDateTime, futureVisit);

        final PersonAttribute patientLocationAttribute =
                patient.getPerson().getAttribute(CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);

        if (patientLocationAttribute != null) {
            visit.setLocation(Context.getLocationService().getLocationByUuid(patientLocationAttribute.getValue()));
        } else {
            visit.setLocation(patient.getPatientIdentifier().getLocation());
        }
        Context.getVisitService().saveVisit(visit);
    }

    private List<VisitInformation> getInformationForFutureVisits(Visit lastDosingVisit, Vaccination vaccination) {
        final String lastDosingVisitType = lastDosingVisit.getVisitType().getName();
        final List<VisitInformation> visitInformation = vaccination.findByVisitType(lastDosingVisitType);

        if (CollectionUtils.isEmpty(visitInformation)) {
            return new ArrayList<>();
        } else if (visitInformation.size() == 1 && visitInformation.get(0).getNumberOfFutureVisit() == 0) {
            return new ArrayList<>();
        } else if (visitInformation.size() == 1) {
            //We send 1 as numberOfVisits, because in this case we have only one visit with this visitType
            return vaccination.findFutureVisits(lastDosingVisitType, 1);
        } else {
            return vaccination.findFutureVisits(lastDosingVisitType,
                    getNumberOfVisits(lastDosingVisit.getPatient(), lastDosingVisitType));
        }
    }

    private int getNumberOfVisits(Patient patient, String visitType) {
        return (int) Context
                .getVisitService()
                .getVisitsByPatient(patient)
                .stream()
                .map(Visit::getVisitType)
                .map(BaseOpenmrsMetadata::getName)
                .filter(visitTypeName -> StringUtils.equalsIgnoreCase(visitTypeName, visitType))
                .count();
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }

    private CFLPatientService getCFLPatientService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_PATIENT_SERVICE_BEAN_NAME, CFLPatientService.class);
    }

    private CFLVisitService getCFLVisitService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_VISIT_SERVICE_BEAN_NAME, CFLVisitService.class);
    }

    private Gson getGson() {
        return new GsonBuilder().setLenient().create();
    }
}

