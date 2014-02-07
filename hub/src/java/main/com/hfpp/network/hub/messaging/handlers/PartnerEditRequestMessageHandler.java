/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import javax.annotation.PostConstruct;
import javax.jms.TextMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.w3c.dom.Document;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.LookupService;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.Role;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the MessageHandler that handles partner edit request messages.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 * @since Healthcare Fraud Prevention Release Assembly v1.0
 */
public class PartnerEditRequestMessageHandler extends BaseMessageHandler {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = PartnerEditRequestMessageHandler.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the user service used to access user data.
     */
    private UserService userService;

    /**
     * Represents the lookup service to retrieve lookup entities.
     */
    private LookupService lookupService;

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (userService or lookupService is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkState(userService == null, "'userService' can't be null.");
        Helper.checkState(lookupService == null, "'lookupService' can't be null.");
    }

    /**
     * This method is supposed to perform the logic to handle the partner edit
     * request message and fill the passed VelocityContext with additional
     * variables to generate the response XML.
     * 
     * @param message
     *            the received TextMessage
     * @param user
     *            the authenticated user
     * @param doc
     *            the parsed XML document
     * @param xpath
     *            the XPath used to extract XML document elements
     * @param context
     *            the VelocityContext
     * @throws IllegalArgumentException
     *             throws if any argument is null, or field in XML in null or
     *             empty, or roleId is invalid
     * @throws XPathExpressionException
     *             throws If expression cannot be compiled
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @throws EntityNotFoundException
     *             if partner user doesn't exist
     * @throws Exception
     *             throws if any error happen
     */
    @Override
    public void handleMessage(TextMessage message, User user, Document doc, XPath xpath, VelocityContext context)
            throws Exception {
        final String signature =
                CLASS_NAME + ".handleMessage"
                        + "(TextMessage message, User user, Document doc,XPath xpath, VelocityContext context)";
        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "message", "user", "doc", "xpath", "context" },
                new Object[] { message, user, doc, xpath, context });
        Helper.checkNull(LOGGER, signature, message, "message");
        Helper.checkNull(LOGGER, signature, user, "user");
        Helper.checkNull(LOGGER, signature, doc, "doc");
        Helper.checkNull(LOGGER, signature, xpath, "xpath");
        Helper.checkNull(LOGGER, signature, context, "context");

        String partnerID = xpath.compile("/PartnerEditRequest/PartnerID/text()").evaluate(doc);
        Helper.checkNullOrEmpty(partnerID, "partnerID");
        String username = xpath.compile("/PartnerEditRequest/Username/text()").evaluate(doc);
        Helper.checkNullOrEmpty(username, "username");
        String password = xpath.compile("/PartnerEditRequest/Password/text()").evaluate(doc);
        String roleId = xpath.compile("/PartnerEditRequest/RoleID/text()").evaluate(doc);
        Helper.checkNullOrEmpty(roleId, "roleId");
        Role role = Helper.findRole(roleId, lookupService.getAllRoles());
        String organizationName = xpath.compile("/PartnerEditRequest/OrganizationName/text()").evaluate(doc);
        Helper.checkNullOrEmpty(organizationName, "organizationName");
        String autoRetrieveCachedData =
                xpath.compile("/PartnerEditRequest/AutoRetrieveCachedData/text()").evaluate(doc);
        Helper.checkNullOrEmpty(autoRetrieveCachedData, "autoRetrieveCachedData");

        User partnerUser = userService.get(partnerID);
        if (partnerUser == null) {
            throw new EntityNotFoundException("Partner user not exist with id:" + partnerID);
        }
        partnerUser.setAutoRetrieveCachedData("true".equals(autoRetrieveCachedData.toLowerCase()));
        partnerUser.setUsername(username);
        partnerUser.setRole(role);
        partnerUser.setOrganizationName(organizationName);
        partnerUser.setUpdatedBy(user.getId());
        userService.update(partnerUser);
        if (password != null && !password.trim().isEmpty()) {
            userService.setPassword(partnerUser.getId(), password);
        }
        context.put("user", partnerUser);
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }

    /**
     * Getter for the userService field
     * 
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Setter for the userService field
     * 
     * @param userService
     *            the userService to set
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Getter for the lookupService field
     * 
     * @return the lookupService
     */
    public LookupService getLookupService() {
        return lookupService;
    }

    /**
     * Setter for the lookupService field
     * 
     * @param lookupService
     *            the lookupService to set
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }
}
