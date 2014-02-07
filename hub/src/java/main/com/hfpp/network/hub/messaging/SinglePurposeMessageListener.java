/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is a single-purpose MessageListener implementation that handles one type
 * of messages.
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
public class SinglePurposeMessageListener extends BaseMessageListener {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = SinglePurposeMessageListener.class
            .getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the file paths of the XML schema file that is used to validate
     * the received XML message.
     */
    private String schemaPath;

    /**
     * Represents the file path of the Velocity template that is used to
     * generate acknowledgement response XML message routed to the message
     * sender.
     */
    private String templatePath;

    /**
     * Represents the name of the operation that is requested by the messages
     * sent to the queue.
     */
    private String operationName;

    /**
     * Represents the message handler used to handle the message.
     */
    private MessageHandler messageHandler;

    /**
     * Represents the XML Schema object that can be used to validate the XML
     * messages. It will be initialized in checkConfiguration() method by
     * instantiating the XSD schema specified in schemaPath configuration.
     */
    private Schema schema;

    /**
     * Empty constructor.
     */
    public SinglePurposeMessageListener() {
    }

    /**
     * This method is called when a message is received from the queue.
     * 
     * @param message
     *            the received message
     * @throws RecoverableMessageHandlingException
     *             if any recoverable error occurred that prevented the message
     *             being properly handled this time only (Note that currently
     *             this won't happen for this implementation)
     */
    @Transactional(rollbackFor = RecoverableMessageHandlingException.class)
    public void onMessage(Message message) {
        final String signature = CLASS_NAME + ".onMessage(Message message)";
        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "message" },
                new Object[] { message });
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            User user = null;
            VelocityContext context = new VelocityContext();
            try {
                // Validate XML message
                validateXMLMessage(textMessage, schema);

                // Parse XML message
                Document doc = parseXMLMessage(textMessage);
                // Get XPath
                XPath xpath = createXPath();

                // Check authentication
                user = authenticate(message);

                // Check authorization
                checkAuthorization(user, operationName);

                // Handle the message
                messageHandler.handleMessage(textMessage, user, doc, xpath,
                        context);

                // Send response message
                sendMessage(message.getJMSReplyTo(), templatePath, context);

                // Perform auditing
                performAuditing(user, operationName, false, textMessage);
                // message.acknowledge();
            } catch (Exception ex) {
                // handle exception
                handleException(ex, message, templatePath, context);
                // Perform auditing
                if (user != null) {
                    performAuditing(user, operationName, true, textMessage);
                } else {
                    LOGGER.info("user for audit not exist");
                }
            }
        }
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
        Helper.checkState(Helper.isNullOrEmpty(schemaPath),
                "'schemaPath' can't be null or empty.");
        Helper.checkState(Helper.isNullOrEmpty(templatePath),
                "'templatePath' can't be null or empty.");
        Helper.checkState(Helper.isNullOrEmpty(operationName),
                "'operationName' can't be null or empty.");
        Helper.checkState(messageHandler == null,
                "'messageHandler' can't be null.");
        try {
            schema = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                    new File(schemaPath));
        } catch (SAXException e) {
            throw new ConfigurationException(schemaPath
                    + " contains invalid xml schema", e);
        }
    }

    /**
     * Getter for the schemaPath field
     * 
     * @return the schemaPath
     */
    public String getSchemaPath() {
        return schemaPath;
    }

    /**
     * Setter for the schemaPath field
     * 
     * @param schemaPath
     *            the schemaPath to set
     */
    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
    }

    /**
     * Getter for the templatePath field
     * 
     * @return the templatePath
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * Setter for the templatePath field
     * 
     * @param templatePath
     *            the templatePath to set
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * Getter for the operationName field
     * 
     * @return the operationName
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Setter for the operationName field
     * 
     * @param operationName
     *            the operationName to set
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Getter for the messageHandler field
     * 
     * @return the messageHandler
     */
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * Setter for the messageHandler field
     * 
     * @param messageHandler
     *            the messageHandler to set
     */
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Getter for the schema field
     * 
     * @return the schema
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Setter for the schema field
     * 
     * @param schema
     *            the schema to set
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

}
