/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.helper;

import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public final class ConceptHelper {

    public static Concept buildConcept(String name) {
        Concept concept = new Concept();
        concept.setFullySpecifiedName(buildConceptName(name, "eng"));
        concept.setDescriptions(buildConceptDescriptions());
        return concept;
    }

    private static ConceptName buildConceptName(String name, String language) {
        ConceptName conceptName = new ConceptName();
        conceptName.setName(name);
        conceptName.setLocale(new Locale(language));
        return conceptName;
    }

    private static Collection<ConceptDescription> buildConceptDescriptions() {
        Collection<ConceptDescription> conceptDescriptions = new ArrayList<ConceptDescription>();
        conceptDescriptions.add(buildConceptDescription("eng", "Hello patient"));
        conceptDescriptions.add(buildConceptDescription("nld", "Hallo patiënt"));
        conceptDescriptions.add(buildConceptDescription("spa", "Hola paciente"));
        return conceptDescriptions;
    }

    private static ConceptDescription buildConceptDescription(String language, String description) {
        ConceptDescription conceptDescription = new ConceptDescription();
        conceptDescription.setLocale(new Locale(language));
        conceptDescription.setDescription(description);
        return conceptDescription;
    }

    private ConceptHelper(){
    }
}
