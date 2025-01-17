/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.cflcore.api.contract.CFLPerson;
import org.openmrs.module.cflcore.api.service.CFLPersonService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class CFLPersonServiceImpl extends HibernateOpenmrsDataDAO<PersonAttribute> implements CFLPersonService {

    private static final String CAREGIVER_RELATIONSHIP_UUID = "acec590b-825e-45d2-876a-0028f174903d";

    private static final String VALUE_COLUMN_NAME = "value";

    private static final String VOIDED_COLUMN_NAME = "voided";

    private DbSessionFactory sessionFactory;
    private PersonDAO personDAO;

    public CFLPersonServiceImpl() {
        super(PersonAttribute.class);
    }

    @Override
    public List<CFLPerson> findByPhone(String phone, boolean dead) {
        Criteria criteria = getSession().createCriteria(this.mappedClass);
        criteria.add(Restrictions.like(VALUE_COLUMN_NAME, phone, MatchMode.EXACT));
        criteria.add(Restrictions.eq(VOIDED_COLUMN_NAME, false));

        List<PersonAttribute> personAttributes = criteria.list();

        List<CFLPerson> cflPersonList = new ArrayList<>();
        for (PersonAttribute personAttribute : personAttributes) {
            Person person = personAttribute.getPerson();
            if (!person.getVoided() && (dead || !person.getDead())) {
                cflPersonList.add(new CFLPerson(person, isCaregiver(person)));
            }
        }

        return cflPersonList;
    }

    @Override
    public void savePersonAttribute(Integer personId, String attributeTypeName, String attributeValue) {
        Person person = personDAO.getPerson(personId);

        List<PersonAttributeType> personAttributeTypes = personDAO.getPersonAttributeTypes(
                attributeTypeName, null, null, null);

        if (CollectionUtils.isNotEmpty(personAttributeTypes)) {
            PersonAttribute personAttribute = new PersonAttribute();
            personAttribute.setAttributeType(personAttributeTypes.get(0));
            personAttribute.setValue(attributeValue);
            personAttribute.setCreator(Context.getAuthenticatedUser());
            person.addAttribute(personAttribute);
            personDAO.savePerson(person);
        }
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setPersonDAO(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    private boolean isCaregiver(Person person) {
        RelationshipType relationshipType = personDAO.getRelationshipTypeByUuid(CAREGIVER_RELATIONSHIP_UUID);

        if (relationshipType == null) {
            return false;
        } else {
            List<Relationship> relationships = personDAO.getRelationships(person, null, relationshipType);
            return !CollectionUtils.isEmpty(relationships);
        }
    }
}
