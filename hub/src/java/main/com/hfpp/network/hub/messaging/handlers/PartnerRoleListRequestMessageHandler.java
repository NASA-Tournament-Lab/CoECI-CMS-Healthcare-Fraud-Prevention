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
import com.hfpp.network.hub.services.LookupService;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the MessageHandler that handles partner role list request messages.
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
public class PartnerRoleListRequestMessageHandler extends BaseMessageHandler {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = PartnerRoleListRequestMessageHandler.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the lookup service to retrieve lookup entities.
     */
    private LookupService lookupService;

    /**
     * Empty constructor.
     */
    public PartnerRoleListRequestMessageHandler() {
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (lookupService is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkState(lookupService == null, "'lookupService' can't be null.");
    }

    /**
     * This method is supposed to perform the logic to handle the partner role
     * list request message and fill the passed VelocityContext with additional
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

        context.put("roles", lookupService.getAllRoles());
        // Log exit
        Helper.logExit(LOGGER, signature, null);
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
