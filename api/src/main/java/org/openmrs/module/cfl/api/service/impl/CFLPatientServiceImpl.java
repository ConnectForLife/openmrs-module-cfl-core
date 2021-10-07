package org.openmrs.module.cfl.api.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.cfl.api.service.CFLPatientService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class CFLPatientServiceImpl extends HibernateOpenmrsDataDAO<PersonAttribute> implements CFLPatientService {

    private static final String VALUE_PROPERTY_NAME = "value";

    private static final String VOIDED_PROPERTY_NAME = "voided";

    private static final String PERSON_PROPERTY_NAME = "person";

    private static final String DEAD_PROPERTY_NAME = "dead";

    private static final String PERSON_ATTRIBUTE_NAME = "personAttribute";

    private DbSessionFactory sessionFactory;

    public CFLPatientServiceImpl() {
        super(PersonAttribute.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Patient> findByVaccinationName(String vaccinationName) {

        List<PersonAttribute> personAttributes = getPersonAttributeList(vaccinationName);

        List<Patient> patientList = new ArrayList<>();
        for (PersonAttribute personAttribute : personAttributes) {
            patientList.add(new Patient(personAttribute.getPerson()));
        }

        return patientList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getPatientUuids(String vaccinationName) {
        return getSession()
                .createSQLQuery(
                        "select p.uuid from person p inner join person_attribute pa on pa.person_id = p.person_id\n" +
                                "where p.dead = 0\n and p.voided = 0\n and pa.value = :vaccinationName")
                .setParameter("vaccinationName", vaccinationName)
                .list();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    private List<PersonAttribute> getPersonAttributeList(String vaccinationName) {
        Criteria criteria = getSession().createCriteria(this.mappedClass, PERSON_ATTRIBUTE_NAME);
        criteria.createAlias(PERSON_ATTRIBUTE_NAME + "." + PERSON_PROPERTY_NAME, PERSON_PROPERTY_NAME);
        criteria.add(Restrictions.eq(VALUE_PROPERTY_NAME, vaccinationName));
        criteria.add(Restrictions.eq(VOIDED_PROPERTY_NAME, false));
        criteria.add(Restrictions.eq(PERSON_PROPERTY_NAME + "." + VOIDED_PROPERTY_NAME, false));
        criteria.add(Restrictions.eq(PERSON_PROPERTY_NAME + "." + DEAD_PROPERTY_NAME, false));
        return criteria.list();
    }
}
