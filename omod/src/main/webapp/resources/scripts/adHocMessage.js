/**
 * Handle optional inputs for channel configuration.
 */
$j(document).ready(function () {
    channelCheckboxClicked(document.getElementById("callChannel"), "callConfig");
    channelCheckboxClicked(document.getElementById("smsChannel"), "smsConfig");

    document.getElementById("send-btn").onclick = function (event) {
        const doSend = confirm($sendConfirmationMessage);
        if (!doSend) {
            event.preventDefault();
        }
    };

    /**
     * Initializes Patient Overview table.
     */
    $j("#patientOverview").dataTable({
        "bJQueryUI": true,
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": false,
        "bFilter": false,
        "iDisplayLength": 25,
        "aoColumns": [
            {"sClass": "identifierColumn", "bSortable": false},
            {"sClass": "givenNameColumn", "bSortable": false},
            {"sClass": "middleNameColumn", "bSortable": false},
            {"sClass": "familyNameColumn", "bSortable": false},
            {"sClass": "ageColumn", "bSortable": false},
            {"sClass": "genderColumn", "bSortable": false}
        ],
        "aaData": $patients,
        "sDom": '<"fg-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
    });
});

function channelCheckboxClicked(checkbox, channelConfigElementId) {
    const channelConfigElement = document.getElementById(channelConfigElementId);

    if (checkbox) {
        if (checkbox.checked) {
            channelConfigElement.style.display = "";
        } else {
            channelConfigElement.style.display = "none";
        }
    }
}
