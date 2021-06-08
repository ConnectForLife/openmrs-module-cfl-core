package org.openmrs.module.cfl.api.builder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.dto.PersonDTO;

import java.io.Serializable;

public class PersonDTOBuilder implements Serializable {

    private static final long serialVersionUID = 7385955668218830611L;

    private Person person;

    private String relationshipName;

    public PersonDTO build() {
        return new PersonDTO()
                .withUuid(person.getUuid())
                .withIsPatient(person.isPatient())
                .withGivenName(person.getGivenName())
                .withMiddleName(person.getMiddleName())
                .withFamilyName(person.getFamilyName())
                .withIdentifier(getPersonOrPatientIdentifier(person))
                .withRelationshipName(relationshipName)
                .withLocation(getPersonLocation(person.getPersonId()))
                .withAge(person.getAge())
                .withGender(person.getGender());
    }

    public Person getPerson() {
        return person;
    }

    public PersonDTOBuilder withPerson(Person person) {
        this.person = person;
        return this;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public PersonDTOBuilder withRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
        return this;
    }

    public String getPersonLocation(Integer personId) {
        Person relatedPerson = Context.getPersonService().getPerson(personId);
        if (relatedPerson == null) {
            return "";
        } else {
            PersonAttribute location = relatedPerson.getAttribute(
                    Context.getAdministrationService().getGlobalProperty(CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY));
            return location == null ? "" : location.getValue();
        }
    }

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

    private String getPersonOrPatientIdentifier(Person person) {
        if (person.isPatient()) {
            Patient patient = Context.getPatientService().getPatient(person.getId());
            if (patient == null || patient.getPatientIdentifier() == null) {
                return "";
            } else {
                return StringUtils.isNotBlank(patient.getPatientIdentifier().getIdentifier()) ?
                        patient.getPatientIdentifier().getIdentifier() + " -" : "";
            }
        } else {
            return StringUtils.isNotBlank(getPersonIdentifier(person)) ? getPersonIdentifier(person) + " -" : "";
        }
    }

    private PersonAttributeType getPersonIdentifierAttributeType() {
        String attributeTypeUUID =
                Context.getAdministrationService().getGlobalProperty(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY);
        return StringUtils.isBlank(attributeTypeUUID) ? null :
                Context.getPersonService().getPersonAttributeTypeByUuid(attributeTypeUUID);
    }

    private String getPersonIdentifier(Person person) {
        String personIdentifier = null;
        PersonAttributeType identifierAttributeType = getPersonIdentifierAttributeType();
        if (identifierAttributeType != null) {
            PersonAttribute attribute = person.getAttribute(identifierAttributeType);
            if (attribute != null) {
                personIdentifier = attribute.getValue();
            }
        }
        return personIdentifier;
    }
}
