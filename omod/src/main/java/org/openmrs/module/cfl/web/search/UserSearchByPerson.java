package org.openmrs.module.cfl.web.search;

import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNumeric;

/**
 * The UserSearchByPerson Class.
 *
 * <p>The OpenMRS SearchHandler for User search by Person ID.
 *
 * <p>Regular URL: {@code
 * openmrs/ws/rest/v1/user?s=byPerson&personId=<id>&includeRetired=true|false}
 */
@Component
public class UserSearchByPerson implements SearchHandler {

  private static final String PERSON_ID_URL_PARAM = "personId";
  private static final String INCLUDE_RETIRED_URL_PARAM = "includeRetired";

  private final SearchConfig searchConfig =
      new SearchConfig(
          "byPerson",
          RestConstants.VERSION_1 + "/user",
          Arrays.asList(
              "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*",
              "2.4.*"),
          new SearchQuery.Builder("Allows you to find users by Person")
              .withRequiredParameters(PERSON_ID_URL_PARAM)
              .withOptionalParameters(INCLUDE_RETIRED_URL_PARAM)
              .build());

  @Autowired private PersonService personService;

  @Autowired private UserService userService;

  @Override
  public SearchConfig getSearchConfig() {
    return searchConfig;
  }

  @Override
  public PageableResult search(RequestContext requestContext) throws ResponseException {
    final String personIdRaw = requestContext.getParameter(PERSON_ID_URL_PARAM);
    final boolean includeRetired =
        Boolean.parseBoolean(requestContext.getParameter(INCLUDE_RETIRED_URL_PARAM));

    if (!isNumeric(personIdRaw)) {
      throw new InvalidSearchException(
          "The 'personId' query parameter must be a number, but was: " + personIdRaw);
    }

    final Person person = personService.getPerson(Integer.parseInt(personIdRaw));

    final List<UserAndPassword1_8> resultDTOs =
        userService.getUsersByPerson(person, includeRetired).stream()
            .map(UserAndPassword1_8::new)
            .collect(Collectors.toList());

    return new NeedsPaging<>(resultDTOs, requestContext);
  }
}
