/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
 * This is a multi-purpose MessageListener implementation that handles multiple
 * types of messages.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong>This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public class MultiPurposeMessageListener extends BaseMessageListener {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = MultiPurposeMessageListener.class
            .getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the mapping from message type (Root XML element tag) to the
     * XML schema file paths.
     */
    private Map<String, String> schemaPaths;

    /**
     * Represents the mapping from message type (Root XML element tag) to the
     * Velocity template file paths.
     */
    private Map<String, String> templatePaths;

    /**
     * Represents the mapping from message type (Root XML element tag) to the
     * corresponding operation name that is requested by the messages of this
     * type.
     */
    private Map<String, String> operationNames;

    /**
     * Represents the mapping from message type (Root XML element tag) to
     * MessageHandler that handles the messages of this type.
     */
    private Map<String, MessageHandler> messageHandlers;

    /**
     * Represents the mapping from message type (Root XML element tag) to the
     * XML Schema object that can be used to validate the XML messages.
     * 
     * It should be non-null non-empty map, keys should be non-null non-empty
     * string, values should be non-null Schema object.
     * 
     * It will be initialized in checkConfiguration() method by instantiating
     * all XSD schemas specified in schemaPaths configuration.
     */
    private Map<String, Schema> schemas;

    /**
     * Empty constructor.
     */
    public MultiPurposeMessageListener() {
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
            // Handle the message
            VelocityContext context = new VelocityContext();
            String templatePath = null;
            String operationName = null;
            User user = null;
            try {
                // Parse XML message to determine the message type
                Document doc = parseXMLMessage(textMessage);
                String messageType = doc.getDocumentElement().getTagName();
                // Retrieve operation name, schema path, template path, handler
                // for the message type
                operationName = operationNames.get(messageType);
                if (operationName != null) {
                    Schema schema = schemas.get(messageType);
                    if (schema != null) {
                        templatePath = templatePaths.get(messageType);
                        if (templatePath != null) {
                            MessageHandler handler = messageHandlers
                                    .get(messageType);
                            if (handler != null) {

                                // Validate XML message
                                validateXMLMessage(textMessage, schema);

                                // Get XPath
                                XPath xpath = createXPath();

                                // Check authentication
                                user = authenticate(message);

                                // Check authorization
                                checkAuthorization(user, operationName);

                                handler.handleMessage(textMessage, user, doc,
                                        xpath, context);

                                // Send response message
                                sendMessage(message.getJMSReplyTo(),
                                        templatePath, context);

                                // Perform auditing
                                performAuditing(user, operationName, false,
                                        textMessage);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                if (templatePath != null) {
                    // handle exception
                    handleException(ex, message, templatePath, context);
                } else {
                    LOGGER.info("templatePath not exist");
                }
                if (operationName != null) {
                    if (user != null) {
                        performAuditing(user, operationName, true, textMessage);
                    } else {
                        LOGGER.info("user for audit not exist");
                    }
                } else {
                    LOGGER.info("operationName not exist");
                }
            }
        }
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly.
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkState(schemaPaths == null, "'schemaPaths' can't be null.");
        Helper.checkState(schemaPaths.isEmpty(),
                "'schemaPaths' can't be empty.");
        for (Entry<String, String> schemaPath : schemaPaths.entrySet()) {
            Helper.checkState(Helper.isNullOrEmpty(schemaPath.getKey()),
                    "'schemaPaths' can't contain null/empty key.");
            Helper.checkState(Helper.isNullOrEmpty(schemaPath.getValue()),
                    "'schemaPaths' can't contain null/empty value.");
        }

        Helper.checkState(templatePaths == null,
                "'templatePaths' can't be null.");
        Helper.checkState(templatePaths.isEmpty(),
                "'templatePaths' can't be empty.");
        for (Entry<String, String> templatePath : templatePaths.entrySet()) {
            Helper.checkState(Helper.isNullOrEmpty(templatePath.getKey()),
                    "'templatePaths' can't contain null/empty key.");
            Helper.checkState(Helper.isNullOrEmpty(templatePath.getValue()),
                    "'templatePaths' can't contain null/empty value.");
        }

        Helper.checkState(operationNames == null,
                "'operationNames' can't be null.");
        Helper.checkState(operationNames.isEmpty(),
                "'operationNames' can't be empty.");
        for (Entry<String, String> operationName : operationNames.entrySet()) {
            Helper.checkState(Helper.isNullOrEmpty(operationName.getKey()),
                    "'operationNames' can't contain null/empty key.");
            Helper.checkState(Helper.isNullOrEmpty(operationName.getValue()),
                    "'operationNames' can't contain null/empty value.");
        }

        Helper.checkState(messageHandlers == null,
                "'messageHandlers' can't be null.");
        Helper.checkState(messageHandlers.isEmpty(),
                "'messageHandlers' can't be empty.");
        for (Entry<String, MessageHandler> messageHandlersEntry : messageHandlers
                .entrySet()) {
            Helper.checkState(
                    Helper.isNullOrEmpty(messageHandlersEntry.getKey()),
                    "'messageHandlers' can't contain null/empty key.");
            Helper.checkState(messageHandlersEntry.getValue() == null,
                    "'messageHandlers' can't contain null value.");
        }

        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemas = new HashMap<String, Schema>();
        try {
            for (Map.Entry<String, String> schemaPath : schemaPaths.entrySet()) {
                Schema schema = schemaFactory.newSchema(new File(schemaPath
                        .getValue()));
                schemas.put(schemaPath.getKey(), schema);
            }
        } catch (SAXException e) {
            throw new ConfigurationException("xml schema is invalid",e);
        }
    }

    /**
     * Getter for the schemaPaths field
     * 
     * @return the schemaPaths
     */
    public Map<String, String> getSchemaPaths() {
        return schemaPaths;
    }

    /**
     * Setter for the schemaPaths field
     * 
     * @param schemaPaths
     *            the schemaPaths to set
     */
    public void setSchemaPaths(Map<String, String> schemaPaths) {
        this.schemaPaths = schemaPaths;
    }

    /**
     * Getter for the templatePaths field
     * 
     * @return the templatePaths
     */
    public Map<String, String> getTemplatePaths() {
        return templatePaths;
    }

    /**
     * Setter for the templatePaths field
     * 
     * @param templatePaths
     *            the templatePaths to set
     */
    public void setTemplatePaths(Map<String, String> templatePaths) {
        this.templatePaths = templatePaths;
    }

    /**
     * Getter for the operationNames field
     * 
     * @return the operationNames
     */
    public Map<String, String> getOperationNames() {
        return operationNames;
    }

    /**
     * Setter for the operationNames field
     * 
     * @param operationNames
     *            the operationNames to set
     */
    public void setOperationNames(Map<String, String> operationNames) {
        this.operationNames = operationNames;
    }

    /**
     * Getter for the messageHandlers field
     * 
     * @return the messageHandlers
     */
    public Map<String, MessageHandler> getMessageHandlers() {
        return messageHandlers;
    }

    /**
     * Setter for the messageHandlers field
     * 
     * @param messageHandlers
     *            the messageHandlers to set
     */
    public void setMessageHandlers(Map<String, MessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

}
