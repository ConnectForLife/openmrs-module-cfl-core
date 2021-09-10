package org.openmrs.module.cfl.api.metadata;

import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/**
 * The Metadata Bundle responsible for adding visit attribute types.
 *
 * The VisitAttributeTypesBundle adds visit attribute types only once, for a first start.
 */
public class VisitAttributeTypesBundle extends VersionedMetadataBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installEveryTime() {
        // nothing to do
    }

    @Override
    protected void installNewVersion() {
        VisitAttributeType isLastDosingVisitAttrType = createVisitAttributeType(
                CFLConstants.IS_LAST_DOSING_VISIT_ATTRIBUTE_NAME,
                CFLConstants.IS_LAST_DOSING_VISIT_ATTR_TYPE_DATATYPE,
                CFLConstants.IS_LAST_DOSING_VISIT_ATTR_TYPE_DESCRIPTION,
                CFLConstants.IS_LAST_DOSING_VISIT_ATTR_TYPE_UUID);
        createVisitAttributeTypeIfNotExists(CFLConstants.IS_LAST_DOSING_VISIT_ATTR_TYPE_UUID, isLastDosingVisitAttrType);
    }

    private VisitAttributeType createVisitAttributeType(String name, String dataTypeClassName, String description,
                                                        String uuid) {
        VisitAttributeType type = new VisitAttributeType();
        type.setName(name);
        type.setDatatypeClassname(dataTypeClassName);
        type.setDescription(description);
        type.setUuid(uuid);
        return type;
    }

    private void createVisitAttributeTypeIfNotExists(String uuid, VisitAttributeType attributeType) {
        VisitService visitService = Context.getVisitService();
        VisitAttributeType actual = visitService.getVisitAttributeTypeByUuid(uuid);
        if (actual == null) {
            visitService.saveVisitAttributeType(attributeType);
        }
    }
}
