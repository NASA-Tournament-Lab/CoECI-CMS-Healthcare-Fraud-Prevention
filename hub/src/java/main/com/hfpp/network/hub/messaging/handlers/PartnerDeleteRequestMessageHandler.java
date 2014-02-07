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
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Document;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the MessageHandler that handles partner delete request messages.
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
public class PartnerDeleteRequestMessageHandler extends BaseMessageHandler {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = PartnerDeleteRequestMessageHandler.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the user service used to access user data.
     */
    private UserService userService;

    /**
     * Represents the JmsTemplate used to send JMS messages. It should be
     * non-null. It is required.
     */
    private JmsTemplate jmsTemplate;
    
    /**
     * Represents the name prefix of the AMQP queue from which Network Nodes
     * receive analysis result. It is optional. It should be non-null, non-empty
     * string. It will be initialized to "hfpp.queue.analysis_results." by
     * default. Usually there is no need to set this explicitly, it is
     * configurable just for possible future queue name changes.
     */
    private String analysisResultQueuePrefix = "hfpp.queue.analysis_results.";

    /**
     * Represents the name prefix of the AMQP queue from which Network Nodes
     * receive data responses. It is optional. It should be non-null, non-empty
     * string. It will be initialized to "hfpp.queue.data_responses." by
     * default. Usually there is no need to set this explicitly, it is
     * configurable just for possible future queue name changes.
     */
    private String dataResponseQueuePrefix = "hfpp.queue.data_responses.";

    /**
     * Represents the name prefix of the AMQP queue from which Network Nodes
     * receive data requests. It is optional. It should be non-null, non-empty
     * string. It will be initialized to "hfpp.queue.data_requests." by default.
     * Usually there is no need to set this explicitly, it is configurable just
     * for possible future queue name changes.
     */
    private String dataRequestQueuePrefix = "hfpp.queue.data_requests.";

    /**
     * Empty constructor.
     */
    public PartnerDeleteRequestMessageHandler() {
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (userService or jmsTemplate is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkState(userService == null, "'userService' can't be null.");
        Helper.checkState(jmsTemplate == null, "'jmsTemplate' can't be null.");
        Helper.checkState(Helper.isNullOrEmpty(analysisResultQueuePrefix),
                "'analysisResultQueuePrefix' can't be null/empty.");
        Helper.checkState(Helper.isNullOrEmpty(dataResponseQueuePrefix),
                "'dataResponseQueuePrefix' can't be null/empty.");
        Helper.checkState(Helper.isNullOrEmpty(dataRequestQueuePrefix),
                "'dataRequestQueuePrefix' can't be null/empty.");
    }

    /**
     * This method is supposed to perform the logic to handle the partner delete
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
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
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
        // Extract requested user ID
        String partnerID = xpath.compile("/PartnerDeleteRequest/PartnerID/text()").evaluate(doc);
        // Delete partner user
        userService.delete(partnerID);
        
        context.put("partnerID", partnerID);
        
        deleteQueue(dataRequestQueuePrefix + partnerID);
        deleteQueue(dataResponseQueuePrefix + partnerID);
        deleteQueue(analysisResultQueuePrefix + partnerID);

        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }
    
    /**
     * Delete queue
     * @param queue the queue name
     */
    private void deleteQueue(String queue) {
        String destinationName = queue
                + "; {delete: always}";
        jmsTemplate.convertAndSend(destinationName, "delete " + queue);
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
     * Sets the JmsTemplate used to send JMS messages.
     * 
     * @param jmsTemplate
     *            the JmsTemplate used to send JMS messages.
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    
    /**
     * Sets the name prefix of the AMQP queue from which Network Nodes receive
     * analysis result.
     * 
     * @param analysisResultQueuePrefix
     *            the name prefix of the AMQP queue from which Network Nodes
     *            receive analysis result.
     */
    public void setAnalysisResultQueuePrefix(String analysisResultQueuePrefix) {
        this.analysisResultQueuePrefix = analysisResultQueuePrefix;
    }

    /**
     * Sets the name prefix of the AMQP queue from which Network Nodes receive
     * data responses.
     * 
     * @param dataResponseQueuePrefix
     *            the name prefix of the AMQP queue from which Network Nodes
     *            receive data responses.
     */
    public void setDataResponseQueuePrefix(String dataResponseQueuePrefix) {
        this.dataResponseQueuePrefix = dataResponseQueuePrefix;
    }

    /**
     * Sets the name prefix of the AMQP queue from which Network Nodes receive
     * data requests.
     * 
     * @param dataRequestQueuePrefix
     *            the name prefix of the AMQP queue from which Network Nodes
     *            receive data requests.
     */
    public void setDataRequestQueuePrefix(String dataRequestQueuePrefix) {
        this.dataRequestQueuePrefix = dataRequestQueuePrefix;
    }
}
