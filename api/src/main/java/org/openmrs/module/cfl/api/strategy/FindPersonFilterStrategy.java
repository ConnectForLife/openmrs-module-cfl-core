package org.openmrs.module.cfl.api.strategy;

import org.openmrs.Person;

public interface FindPersonFilterStrategy {

    boolean shouldBeReturned(Person person);
}
