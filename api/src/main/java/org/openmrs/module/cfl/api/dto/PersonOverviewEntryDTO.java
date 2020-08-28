package org.openmrs.module.cfl.api.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

import java.util.ArrayList;
import java.util.List;

public class PersonOverviewEntryDTO {

    private String gender;
    private Integer age;
    private String birthdate;
    private boolean birthdateEstimated;
    private String personName;
    private String uuid;
    private String patientIdentifier;
    private String personIdentifier;
    private List<PersonAttributeDTO> attributes = new ArrayList<PersonAttributeDTO>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public List<PersonAttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<PersonAttributeDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * Convenience method to get this person's first attribute that has a PersonAttributeType.name
     * equal to <code>attributeName</code>.
     * <p></p>
     * Returns null if this person has no non-voided {@link PersonAttribute} with the given type
     * name, the given name is null, or this person has no attributes.
     *
     * @param attributeName the name string to match on
     * @return PersonAttributeDTO whose {@link PersonAttributeType#getName()} match the given name
     *         string
     */
    public PersonAttributeDTO getAttribute(String attributeName) {
        if (StringUtils.isNotBlank(attributeName)) {
            for (PersonAttributeDTO attribute : getAttributes()) {
                String type = attribute.getName();
                if (StringUtils.isNotBlank(type) && attributeName.equals(type)) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public String getPersonIdentifier() {
        return personIdentifier;
    }

    public void setPersonIdentifier(String personIdentifier) {
        this.personIdentifier = personIdentifier;
    }
}
