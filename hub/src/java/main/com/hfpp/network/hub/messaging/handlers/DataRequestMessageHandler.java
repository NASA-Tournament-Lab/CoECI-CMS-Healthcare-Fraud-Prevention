/*
 * Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import java.util.ArrayList;

import javax.jms.TextMessage;
import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.InsufficientParticipationRatioException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the message handler that handles data request messages.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
 *      - updated handleMessage for added StudyID in data request
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.1
 */
public class DataRequestMessageHandler extends BaseDataExchangeMessageHandler {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = DataRequestMessageHandler.class
            .getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Empty constructor.
     */
    public DataRequestMessageHandler() {
    }

    /**
     * This method is supposed to perform the logic to handle the data request
     * message and fill the passed VelocityContext with additional variables to
     * generate the response XML.
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
     *             if the requester/original requester's partner contains not
     *             exist user Id or not found user statistics
     * @throws InsufficientParticipationRatioException
     *             if the requester/original requester's participation ratio is
     *             low
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
        // Extract request ID
        String requestId = xpath.compile("/DataRequest/RequestID/text()")
                .evaluate(doc);
        context.put("requestId", requestId);
        // Extract study ID
        String studyId = xpath.compile("/DataRequest/StudyID/text()")
                .evaluate(doc);
        context.put("studyId", studyId);
        // Build DataRequest from XML message
        DataRequest request = new DataRequest();
        request.setId(requestId);
        request.setStudyId(studyId);
        request.setRequesterId(user.getId());
        String originalRequesterID = xpath.compile(
                "/DataRequest/OriginalRequesterID/text()").evaluate(doc);
        if (!Helper.isNullOrEmpty(originalRequesterID)) {
            request.setOriginalRequesterId(originalRequesterID);
        }

        String cacheSafeValue = xpath.compile("/DataRequest/CacheSafe/text()")
                .evaluate(doc);
        if (!Helper.isNullOrEmpty(cacheSafeValue)) {
            request.setCacheSafe(DatatypeConverter.parseBoolean(cacheSafeValue));
        }
        request.setQuery(xpath.compile("/DataRequest/Query/text()").evaluate(
                doc));
        request.setExpirationTime(DatatypeConverter.parseDateTime(
                xpath.compile("/DataRequest/ExpirationTime/text()").evaluate(
                        doc)).getTime());
        request.setRequestedPartners(new ArrayList<String>());
        NodeList nodes = (NodeList) xpath.compile(
                "/DataRequest/RequestedPartners/PartnerID").evaluate(doc,
                XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            String userId = nodes.item(i).getTextContent();
            request.getRequestedPartners().add(userId);
        }
        // Add data request to the network
        getDataExchangeService().addDataRequest(request);
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }
}
