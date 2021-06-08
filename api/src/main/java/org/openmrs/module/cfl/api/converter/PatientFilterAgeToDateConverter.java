package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.cfl.api.util.ConversionUtils;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.domain.criteria.Condition;
import org.openmrs.module.messages.domain.criteria.EntityFieldCondition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PatientFilterAgeToDateConverter extends AbstractFieldValueConverter {
    private final TimeZone defaultUserTimezone;

    public PatientFilterAgeToDateConverter() {
        this.defaultUserTimezone = DateUtil.getDefaultUserTimezone();
    }

    @Override
    protected List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto) {
        final Number fromAge = ConversionUtils.toNumber(dto.getValue());
        final Number toAge = ConversionUtils.toNumber(dto.getSecondValue());

        final List<Condition<?, ?>> results = new ArrayList<Condition<?, ?>>();
        if (toAge != null) {
            results.add(new EntityFieldCondition(getFieldPath(), ">=", convertAgeToDate(toAge.intValue() + 1)));
        }
        if (fromAge != null) {
            results.add(new EntityFieldCondition(getFieldPath(), "<=", convertAgeToDate(fromAge.intValue())));
        }
        return results;
    }

    private Date convertAgeToDate(final int age) {
        final Calendar calendar = Calendar.getInstance(defaultUserTimezone);
        calendar.add(Calendar.YEAR, -age);
        return calendar.getTime();
    }
}
