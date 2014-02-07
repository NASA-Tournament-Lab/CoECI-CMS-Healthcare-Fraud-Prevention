/*
 * Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.
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
import com.hfpp.network.hub.messaging.dto.UserDTO;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserStatistics;

/**
 * <p>
 * This is the MessageHandler that handles partner statistics request messages.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
 *      - updated handleMessage for the added dataRequestsDeclined to UserDTO
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.1
 */
public class PartnerStatisticsRequestMessageHandler extends BaseMessageHandler {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = PartnerStatisticsRequestMessageHandler.class
            .getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the user service used to access user data.
     */
    private UserService userService;

    /**
     * Empty constructor.
     */
    public PartnerStatisticsRequestMessageHandler() {
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (userService is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkState(userService == null, "'userService' can't be null.");
    }

    /**
     * This method is supposed to perform the logic to handle the partner
     * statistics request message and fill the passed VelocityContext with
     * additional variables to generate the response XML.
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
     *             throws if any argument is null
     * @throws XPathExpressionException
     *             throws If expression cannot be compiled
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @throws Exception
     *             throws if any error happen
     */
    public void handleMessage(TextMessage message, User user, Document doc,
            XPath xpath, VelocityContext context) throws Exception {
        final String signature = CLASS_NAME
                + ".handleMessage(TextMessage message, User user, Document doc,XPath xpath, VelocityContext context)";
        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "message", "user",
                "doc", "xpath", "context" }, new Object[] { message, user, doc,
                xpath, context });
        Helper.checkNull(LOGGER, signature, message, "message");
        Helper.checkNull(LOGGER, signature, user, "user");
        Helper.checkNull(LOGGER, signature, doc, "doc");
        Helper.checkNull(LOGGER, signature, xpath, "xpath");
        Helper.checkNull(LOGGER, signature, context, "context");
        // Extract requested user ID
        String userId = xpath.compile(
                "/PartnerStatisticsRequest/PartnerID/text()").evaluate(doc);
        // Get user statistics
        UserStatistics stat = userService.getUserStatistics(userId);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        // check null
        if (stat != null) {
            userDTO.setDataRequestsReceived(stat.getDataRequestsReceived());
            userDTO.setDataRequestsResponded(stat.getDataRequestsResponded());
            userDTO.setDataRequestsInitiated(stat.getDataRequestsInitiated());
            userDTO.setDataRequestsDeclined(stat.getDataRequestsDeclined());
        }
        context.put("user", userDTO);
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

}
