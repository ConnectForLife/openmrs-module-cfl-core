<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/cfl/scripts/manageCountries.js"/>

<style type="text/css">
	#enterClusterMembersSpan {
	    font-style: italic;
	}
</style>

<h2><spring:message code="cfl.addNewCountry.title"/></h2>

<form method="post">
    <fieldset>
        <table>
            <tr>
                <td><spring:message code="cfl.countryName.label"/><span class="required">*</span></td>
                <td>
                    <spring:bind path="model.name">
                        <input required type="text" name="${status.expression}" value="${status.value}" size="20" />
                        <c:if test="${status.errorMessage != ''}">
                            <c:if test="${status.errorMessage != ''}">
                                <span class="error">${status.errorMessage}</span>
                            </c:if>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td><spring:message code="cfl.countryCode.label"/></td>
                <td>
                    <spring:bind path="model.countryCode">
                        <input type="text" name="${status.expression}" value="${status.value}" size="20" />
                        <c:if test="${status.errorMessage != ''}">
                            <c:if test="${status.errorMessage != ''}">
                                <span class="error">${status.errorMessage}</span>
                            </c:if>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
        </table>
        <br/>
        <table>
            <tr>
                <td id="addClusterMembersField">
                    <span>
                        <openmrs:message code="cfl.addClusterMembers.label"/>
                    </span>
                </td>
            </tr>

            <tr id="enterClusterMembersSpan">
                <td><span><openmrs:message code="cfl.enterClusterMembers.hint"/></span></td>
            </tr>

            <tr id="clusterMembersRow">
                <td>
                    <form:textarea path="model.clusterMembers" name="${status.expression}" value="${status.value}"
                        cols="50" rows="5" />
                </td>
            </tr>
        </table>
        <br/>
        <input type="submit" value="Save" name="save">
    </fieldset>
</form>

<br/>
<a href="countryList.form"><openmrs:message code="cfl.backToPreviousPage.label"/></a>

<%@ include file="/WEB-INF/template/footer.jsp" %>
