/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.cfl.page.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.AppUiConstants;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.referenceapplication.ReferenceApplicationConstants;
import org.openmrs.module.referenceapplication.ReferenceApplicationWebConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.web.user.CurrentUsers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.openmrs.module.referenceapplication.ReferenceApplicationWebConstants.COOKIE_NAME_LAST_SESSION_LOCATION;
import static org.openmrs.module.referenceapplication.ReferenceApplicationWebConstants.REQUEST_PARAMETER_NAME_REDIRECT_URL;
import static org.openmrs.module.referenceapplication.ReferenceApplicationWebConstants.SESSION_ATTRIBUTE_REDIRECT_URL;

/**
 * Login page controller copied from the Reference Application Module
 */
@Controller
public class CflLoginPageController {

    //see TRUNK-4536 for details why we need this
    private static final String GET_LOCATIONS = "Get Locations";

    // RA-592: don't use PrivilegeConstants.VIEW_LOCATIONS
    private static final String VIEW_LOCATIONS = "View Locations";

    private static final Log LOGGER = LogFactory.getLog(CflLoginPageController.class);

    /**
     * @should redirect the user to the home page if they are already authenticated
     * @should show the user the login page if they are not authenticated
     * @should set redirectUrl in the page model if any was specified in the request
     * @should set the referer as the redirectUrl in the page model if no redirect param exists
     * @should set redirectUrl in the page model if any was specified in the session
     * @should not set the referer as the redirectUrl in the page model if referer URL is outside
     *         context path
     * @should set the referer as the redirectUrl in the page model if referer URL is within context
     *         path
     */
    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:ParameterAssignment",
            "PMD.CyclomaticComplexity"})
    public String get(PageModel model, UiUtils ui, PageRequest pageRequest,
                      @CookieValue(value = COOKIE_NAME_LAST_SESSION_LOCATION, required = false) String lastSessionLocationId,
                      @SpringBean("locationService") LocationService locationService,
                      @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                      @SpringBean("adminService") AdministrationService administrationService) {

        final String redirectUrl = getRedirectUrl(pageRequest);
        final String methodResult;

        if (Context.isAuthenticated() && Context.getUserContext().getLocationId() != null) {
            methodResult = getRedirectForAuthenticated(redirectUrl, pageRequest, ui);
        } else {
            initLoginPageModel(model, redirectUrl, pageRequest, lastSessionLocationId, locationService, appFrameworkService,
                    administrationService);
            methodResult = null;
        }

        return methodResult;
    }

    private String getRedirectForAuthenticated(String redirectUrl, PageRequest pageRequest, UiUtils ui) {
        if (StringUtils.isNotBlank(redirectUrl)) {
            return "redirect:" + getRelativeUrl(redirectUrl, pageRequest);
        }
        return "redirect:" + ui.pageLink(ReferenceApplicationConstants.MODULE_ID, "home");
    }

    private void initLoginPageModel(PageModel model, String redirectUrl, PageRequest pageRequest,
                                    String lastSessionLocationId, LocationService locationService,
                                    AppFrameworkService appFrameworkService, AdministrationService administrationService) {
        model.addAttribute(REQUEST_PARAMETER_NAME_REDIRECT_URL, getRelativeUrl(redirectUrl, pageRequest));

        boolean isLocationUserPropertyAvailable = isLocationUserPropertyAvailable(administrationService);
        Object showLocation = pageRequest.getAttribute("showSessionLocations");
        if (showLocation != null && showLocation.toString().equals("true")) {
            // if the request contains a attribute as showSessionLocations,
            // then ignore isLocationUserPropertyAvailable
            isLocationUserPropertyAvailable = false;
        }

        Location lastSessionLocation = null;

        try {
            Context.addProxyPrivilege(VIEW_LOCATIONS);
            Context.addProxyPrivilege(GET_LOCATIONS);
            if (!isLocationUserPropertyAvailable) {
                model.addAttribute("locations", appFrameworkService.getLoginLocations());
            }
            lastSessionLocation = locationService.getLocation(Integer.valueOf(lastSessionLocationId));
        } catch (NumberFormatException ex) {
            // pass
            LOGGER.error(ex.getMessage());
        } finally {
            Context.removeProxyPrivilege(VIEW_LOCATIONS);
            Context.removeProxyPrivilege(GET_LOCATIONS);
        }

        boolean showSessionLocations = !isLocationUserPropertyAvailable;
        boolean selectLocation = false;
        if (isSelectLocationRequest(isLocationUserPropertyAvailable)) {
            selectLocation = true;
            showSessionLocations = true;
            List<Location> locations = getUserLocations(administrationService, locationService);
            if (locations.isEmpty()) {
                locations = appFrameworkService.getLoginLocations();
            }
            model.addAttribute("locations", locations);
        }

        model.addAttribute("showSessionLocations", showSessionLocations);
        model.addAttribute("selectLocation", selectLocation);
        model.addAttribute("lastSessionLocation", lastSessionLocation);
    }

    private boolean isLocationUserPropertyAvailable(AdministrationService administrationService) {
        String locationUserPropertyName = administrationService
                .getGlobalProperty(ReferenceApplicationConstants.LOCATION_USER_PROPERTY_NAME);

        return StringUtils.isNotBlank(locationUserPropertyName);
    }

    private boolean isUrlWithinOpenmrs(PageRequest pageRequest, String redirectUrl) {
        if (StringUtils.isNotBlank(redirectUrl)) {
            if (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://")) {
                try {
                    URL url = new URL(redirectUrl);
                    String urlPath = url.getFile();
                    String urlContextPath = urlPath.substring(0, urlPath.indexOf('/', 1));
                    if (StringUtils.equals(pageRequest.getRequest().getContextPath(), urlContextPath)) {
                        return true;
                    }
                } catch (MalformedURLException e) {
                    LOGGER.error(e.getMessage());
                }
            } else if (redirectUrl.startsWith(pageRequest.getRequest().getContextPath())) {
                return true;
            }
        }
        return false;
    }

    private String getRedirectUrlFromReferer(PageRequest pageRequest) {
        String manualLogout = pageRequest.getSession().getAttribute(AppUiConstants.SESSION_ATTRIBUTE_MANUAL_LOGOUT,
                String.class);
        String redirectUrl = "";
        if (!Boolean.valueOf(manualLogout)) {
            redirectUrl = pageRequest.getRequest().getHeader("Referer");
        } else {
            Cookie cookie = new Cookie(ReferenceApplicationWebConstants.COOKIE_NAME_LAST_USER, null);
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            pageRequest.getResponse().addCookie(cookie);
        }
        pageRequest.getSession().setAttribute(AppUiConstants.SESSION_ATTRIBUTE_MANUAL_LOGOUT, null);
        return redirectUrl;
    }

    private String getRedirectUrlFromRequest(PageRequest pageRequest) {
        return pageRequest.getRequest().getParameter(REQUEST_PARAMETER_NAME_REDIRECT_URL);
    }

    private String getRedirectUrl(PageRequest pageRequest) {
        String redirectUrl = getRedirectUrlFromRequest(pageRequest);
        if (StringUtils.isBlank(redirectUrl)) {
            redirectUrl = getStringSessionAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, pageRequest.getRequest());
        }
        if (StringUtils.isBlank(redirectUrl)) {
            redirectUrl = getRedirectUrlFromReferer(pageRequest);
        }
        if (StringUtils.isNotBlank(redirectUrl) && isUrlWithinOpenmrs(pageRequest, redirectUrl)) {
            return redirectUrl;
        }
        return "";
    }

    /**
     * Processes requests to authenticate a user
     *
     * @param username
     * @param password
     * @param sessionLocationId
     * @param locationService
     * @param ui {@link UiUtils} object
     * @param pageRequest {@link PageRequest} object
     * @param sessionContext
     * @return
     * @should redirect the user back to the redirectUrl if any
     * @should redirect the user to the home page if the redirectUrl is the login page
     * @should send the user back to the login page if an invalid location is selected
     * @should send the user back to the login page when authentication fails
     */
    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:ParameterAssignment",
            "checkstyle:CyclomaticComplexity", "PMD.ExcessiveParameterList",
            "PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.CyclomaticComplexity",
            "PMD.NPathComplexity", "PMD.AvoidReassigningParameters", "PMD.CollapsibleIfStatements"})
    public String post(@RequestParam(value = "username", required = false) String username,
                       @RequestParam(value = "password", required = false) String password,
                       @RequestParam(value = "sessionLocation", required = false) Integer sessionLocationId,
                       @SpringBean("locationService") LocationService locationService,
                       @SpringBean("adminService") AdministrationService administrationService, UiUtils ui,
                       @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService, PageRequest pageRequest,
                       UiSessionContext sessionContext) {

        String redirectUrl = getRedirectUrl(pageRequest);
        redirectUrl = getRelativeUrl(redirectUrl, pageRequest);
        Location sessionLocation = null;
        if (sessionLocationId != null) {
            try {
                // TODO as above, grant this privilege to Anonymous instead of using a proxy privilege
                Context.addProxyPrivilege(VIEW_LOCATIONS);
                Context.addProxyPrivilege(GET_LOCATIONS);
                sessionLocation = locationService.getLocation(sessionLocationId);
            } finally {
                Context.removeProxyPrivilege(VIEW_LOCATIONS);
                Context.removeProxyPrivilege(GET_LOCATIONS);
            }
        }

        try {
            if (!Context.isAuthenticated()) {
                Context.authenticate(username, password);

                if (isLocationUserPropertyAvailable(administrationService)) {
                    List<Location> accessibleLocations = getUserLocations(administrationService, locationService);
                    if (accessibleLocations.size() == 1) {
                        sessionLocation = accessibleLocations.get(0);
                    } else if (accessibleLocations.size() > 1) {
                        return "redirect:" + ui.pageLink(ReferenceApplicationConstants.MODULE_ID, "login");
                    }

                    //If there is a single login location, default to that
                    if (sessionLocation == null) {
                        List<Location> loginLocations = appFrameworkService.getLoginLocations();
                        if (loginLocations.size() == 1) {
                            sessionLocation = loginLocations.get(0);
                        }
                    }

                    if (sessionLocation != null) {
                        sessionLocationId = sessionLocation.getLocationId();
                    } else {
                        return "redirect:" + ui.pageLink(ReferenceApplicationConstants.MODULE_ID, "login");
                    }
                }
            }

            if (sessionLocation != null && sessionLocation.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN)) {
                // Set a cookie, so next time someone logs in on this machine, we can default to that same location
                Cookie cookie = new Cookie(COOKIE_NAME_LAST_SESSION_LOCATION, sessionLocationId.toString());
                cookie.setHttpOnly(true);
                pageRequest.getResponse().addCookie(cookie);
                if (Context.isAuthenticated()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("User has successfully authenticated");
                    }
                    CurrentUsers.addUser(pageRequest.getRequest().getSession(), Context.getAuthenticatedUser());

                    sessionContext.setSessionLocation(sessionLocation);
                    //we set the username value to check it new or old user is trying to log in
                    cookie = new Cookie(ReferenceApplicationWebConstants.COOKIE_NAME_LAST_USER,
                            String.valueOf(username.hashCode()));
                    cookie.setHttpOnly(true);
                    pageRequest.getResponse().addCookie(cookie);

                    // set the locale based on the user's default locale
                    Locale userLocale = GeneralUtils.getDefaultLocale(Context.getAuthenticatedUser());
                    if (userLocale != null) {
                        Context.getUserContext().setLocale(userLocale);
                        pageRequest.getResponse().setLocale(userLocale);
                        new CookieLocaleResolver().setDefaultLocale(userLocale);
                    }

                    if (StringUtils.isNotBlank(redirectUrl)) {
                        //don't redirect back to the login page on success nor an external url
                        if (isUrlWithinOpenmrs(pageRequest, redirectUrl)) {
                            if (!redirectUrl.contains("login.") && isSameUser(pageRequest, username)) {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("Redirecting user to " + redirectUrl);
                                }
                                return "redirect:" + redirectUrl;
                            } else {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("Redirect contains 'login.', redirecting to home page");
                                }
                            }
                        }
                    }

                    return "redirect:" + ui.pageLink(ReferenceApplicationConstants.MODULE_ID, "home");
                }
            } else if (sessionLocation == null) {
                pageRequest.getSession().setAttribute(ReferenceApplicationWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        ui.message("referenceapplication.login.error.locationRequired"));
            } else {
                // the UI shouldn't allow this, but protect against it just in case
                pageRequest.getSession().setAttribute(ReferenceApplicationWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        ui.message("referenceapplication.login.error.invalidLocation", sessionLocation.getName()));
            }
        } catch (ContextAuthenticationException ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to authenticate user");
            }
            pageRequest.getSession().setAttribute(ReferenceApplicationWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                    ui.message(ReferenceApplicationConstants.MODULE_ID + ".error.login.fail"));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending user back to login page");
        }

        //TODO limit login attempts by IP Address

        pageRequest.getSession().setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, redirectUrl);
        // Since the user is already authenticated without location, need to logout before redirecting
        Context.logout();
        return "redirect:" + ui.pageLink(ReferenceApplicationConstants.MODULE_ID, "login");
    }

    /**
     * Checks if the request should be treated as a submission of a session location selection.
     *
     * @param isUserLocPropEnabled boolean value to specify if location usr property is set or not
     * @return
     */
    private boolean isSelectLocationRequest(boolean isUserLocPropEnabled) {
        return isUserLocPropEnabled && Context.isAuthenticated() && Context.getUserContext().getLocationId() == null;
    }

    private boolean isSameUser(PageRequest pageRequest, String username) {
        String cookieValue = pageRequest.getCookieValue(ReferenceApplicationWebConstants.COOKIE_NAME_LAST_USER);
        int storedUsername = 0;
        if (StringUtils.isNotBlank(cookieValue)) {
            storedUsername = Integer.parseInt(cookieValue);
        }
        return cookieValue == null || storedUsername == username.hashCode();
    }

    private List<Location> getUserLocations(AdministrationService adminService, LocationService locationService) {
        String locationUserPropertyName = adminService
                .getGlobalProperty(ReferenceApplicationConstants.LOCATION_USER_PROPERTY_NAME);
        List<Location> locations = new ArrayList();
        String locationUuids = Context.getAuthenticatedUser().getUserProperty(locationUserPropertyName);
        if (StringUtils.isNotBlank(locationUuids)) {
            for (String uuid : StringUtils.split(locationUuids, ",")) {
                String trimmedUuid = uuid.trim();
                Location loc = locationService.getLocationByUuid(trimmedUuid);
                if (loc == null) {
                    throw new APIException("No location with uuid: " + trimmedUuid);
                }
                locations.add(locationService.getLocationByUuid(trimmedUuid));
            }
        }

        return locations;
    }

    private String getStringSessionAttribute(String attributeName, HttpServletRequest request) {
        Object attributeValue = request.getSession().getAttribute(attributeName);
        request.getSession().removeAttribute(attributeName);
        return attributeValue != null ? attributeValue.toString() : null;
    }

    public String getRelativeUrl(String url, PageRequest pageRequest) {
        String aUrl = url;
        if (aUrl == null) {
            return null;
        }

        if ((!aUrl.isEmpty() && aUrl.charAt(0) == '/') || (!aUrl.startsWith("http://") && !aUrl.startsWith("https://"))) {
            return aUrl;
        }

        //This is an absolute url, discard the protocal, domain name/host and port section
        if (aUrl.startsWith("http://")) {
            aUrl = StringUtils.removeStart(aUrl, "http://");
        } else if (aUrl.startsWith("https://")) {
            aUrl = StringUtils.removeStart(aUrl, "https://");
        }
        int indexOfContextPath = aUrl.indexOf(pageRequest.getRequest().getContextPath());
        if (indexOfContextPath >= 0) {
            aUrl = aUrl.substring(indexOfContextPath);
            LOGGER.debug("Relative redirect:" + aUrl);

            return aUrl;
        }
        return null;
    }
}
