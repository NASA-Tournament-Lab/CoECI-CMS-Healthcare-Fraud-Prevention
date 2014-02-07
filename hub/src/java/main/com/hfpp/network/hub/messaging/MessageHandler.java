/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import javax.jms.TextMessage;
import javax.xml.xpath.XPath;

import org.apache.velocity.VelocityContext;
import org.w3c.dom.Document;

import com.hfpp.network.models.User;

/**
 * <p>
 * MessageHandler interface abstracts the logic for handling parsed XML
 * messages. XML message validation, authentication and general authorization
 * (role-checking) is done outside of the MessageHandler.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively
 * thread safe.
 * </p>
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public interface MessageHandler {
    /**
     * This method is supposed to perform the logic to handle the message and
     * fill the passed VelocityContext with additional variables to generate the
     * response XML.
     * 
     * @param message
     *            the received TextMessage
     * @param user
     *            the authenticated user
     * @param doc
     *            the parsed XML document
     * 
     * @param xpath
     *            the XPath used to extract XML document elements
     * @param context
     *            the VelocityContext
     * @throws Exception
     *             throws if any error happen
     */
    public void handleMessage(TextMessage message, User user, Document doc,
            XPath xpath, VelocityContext context) throws Exception;
}
