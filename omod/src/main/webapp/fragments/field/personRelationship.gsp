<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.6.0.min.js")
    ui.includeJavascript("uicommons", "services/personService.js")
    ui.includeJavascript("cfl", "field/personRelationship.js")
%>

<div ng-app="personRelationships" ng-controller="PersonRelationshipController"
     ng-init='relationships = ${ groovy.json.JsonOutput.toJson(initialRelationships) }'>

    <div ng-repeat="(index, relationship) in relationships">
        <p class="left">
            <select id="{{'relationship_type-' + index}}" name="relationship_type" class="rel_type" ng-model="relationship.type">
                <option value="">${ui.message('registrationapp.person.relationship.selectRelationshipType')}</option>
                <% relationshipTypes.each { type -> %>
                <option value="${type.uuid}-A">${type.aIsToB}</option>
                <% } %>
                <% relationshipTypes.each { type -> %>
                <% if (type.aIsToB != type.bIsToA) { %>
                <option value="${type.uuid}-B">${type.bIsToA}</option>
                <% } %>
                <% } %>
            </select>
        </p>

        <p class="left">
            <input type="text" id="{{'other_person_uuid-' + index}}" class="person-typeahead" placeholder="${ui.message('registrationapp.person.name')}"
                   ng-model="relationship.name"
                   typeahead="person as person.display for person in getPersons(\$viewValue) | limitTo:5"
                   typeahead-min-length="3"
                   typeahead-on-select="selectPerson(\$item, \$index)"/>
            <input type="text" name="other_person_uuid" ng-model="relationship.uuid" ng-show="false"/>
        </p>

        <p style="padding: 10px">
            <a ng-click="addNewRelationship()">
                <i class="icon-plus-sign edit-action"></i>
            </a>
            <a ng-click="removeRelationship(relationship)">
                <i class="icon-minus-sign edit-action"></i>
            </a>
        </p>
    </div>

    <% if (person) { %>
        <script>
            jq("#registration-section-form").on("submit", function() {
                var formData = jq('#registration-section-form').serialize();
                formData += "&personId=" + ${ person.personId };
                var url = '/' + OPENMRS_CONTEXT_PATH + '/cfl/field/personRelationship/updateRelationships.action';
                jq.ajax({
                    async: false,
                    url: url,
                    type: "POST",
                    data: formData,
                    dataType: "json",
                    error: function(response) {
                        jq('#submit').removeAttr('disabled');
                        jq('#cancelSubmission').removeAttr('disabled');
                    }
                });
            })
        </script>
    <% } %>
</div>
