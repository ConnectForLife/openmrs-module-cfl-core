package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class AbstractFieldValueConverter implements PatientFilterConverter, PatientFilterConverterWithFieldPath {
    private String fieldPath;

    @Override
    public void initFilter(Map<String, Object> properties) {
        try {
            fieldPath = (String) properties.get(FIELD_PATH_PROP);
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("The 'fieldPath' property must be a String, but was: " +
                    properties.get(FIELD_PATH_PROP).getClass().getSimpleName(), cce);
        }

        if (fieldPath == null) {
            throw new IllegalArgumentException("The 'fieldPath' property must not be empty!");
        }
    }

    @Override
    public List<Condition<?, ?>> convert(AdHocMessagePatientFilterDTO dto) {
        if (isConfigurationInvalid()) {
            throw new IllegalArgumentException(
                    AbstractFieldValueStaticOperatorConverter.class.getSimpleName() + " not initialized!");
        }

        if (dto.hasValue()) {
            return createConditions(dto);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getFieldPath() {
        return fieldPath;
    }

    protected abstract List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto);

    protected boolean isConfigurationInvalid() {
        return isBlank(fieldPath);
    }
}
