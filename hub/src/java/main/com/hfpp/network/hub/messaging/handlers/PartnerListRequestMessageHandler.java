/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.TextMessage;
import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.w3c.dom.Document;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.messaging.dto.UserDTO;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.SearchResult;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserSearchCriteria;

/**
 * <p>
 * This is the MessageHandler that handles partner list request messages.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public class PartnerListRequestMessageHandler extends BaseMessageHandler {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = PartnerListRequestMessageHandler.class
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
    public PartnerListRequestMessageHandler() {
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
     * This method is supposed to perform the logic to handle the partner list
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
     *             throws if any argument is null
     * @throws XPathExpressionException
     *             throws If expression cannot be compiled
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
        // Build UserSearchCriteria based on the XML message
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setPageNumber(0);
        String organzationName = xpath.compile(
                "/PartnerListRequest/OrganizationName/text()").evaluate(doc);
        if (!Helper.isNullOrEmpty(organzationName)) {
            criteria.setOrganizationName(organzationName);
        }
        String eligibleToReceiveDataRequestValue = xpath.compile(
                "/PartnerListRequest/EligibleToReceiveDataRequest/text()")
                .evaluate(doc);
        if (!Helper.isNullOrEmpty(eligibleToReceiveDataRequestValue)) {
            criteria.setEligibleToReceiveDataRequests(DatatypeConverter
                    .parseBoolean(eligibleToReceiveDataRequestValue));
        }
        String eligibleToInitiateDataRequestValue = xpath.compile(
                "/PartnerListRequest/EligibleToInitiateDataRequest/text()")
                .evaluate(doc);
        if (!Helper.isNullOrEmpty(eligibleToInitiateDataRequestValue)) {
            criteria.setEligibleToInitiateDataRequests(DatatypeConverter
                    .parseBoolean(eligibleToInitiateDataRequestValue));
        }
        // Search users and translate to UserDTO's
        SearchResult<User> result = userService.search(criteria);
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();
        for (User usr : result.getValues()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(usr.getId());
            userDTO.setOrganizationName(usr.getOrganizationName());
            userDTO.setEligibleToReceiveDataRequest(usr
                    .isEligibleToReceiveDataRequests());
            userDTO.setEligibleToInitiateDataRequest(usr
                    .isEligibleToInitiateDataRequests());
            userDTOs.add(userDTO);
        }
        context.put("users", userDTOs);
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
