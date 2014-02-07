/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import java.util.Date;

import javax.jms.TextMessage;
import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.w3c.dom.Document;

import com.hfpp.network.hub.services.AuthorizationException;
import com.hfpp.network.hub.services.DataRequestExpiredException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the message handler that handles data response messages.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>add check useCache attribute value not exist</li>
 * </ul>
 * </p>
 * @author flying2hk,TCSASSEMBLER
 * @version 1.1
 */
public class DataResponseMessageHandler extends BaseDataExchangeMessageHandler {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = DataResponseMessageHandler.class
            .getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Empty constructor.
     */
    public DataResponseMessageHandler() {
    }

    /**
     * This method is supposed to perform the logic to handle the data response
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
     *             if there is no corresponding request identified requestId
     * @throws DataRequestExpiredException
     *             if the request to respond has expired
     * @throws AuthorizationException
     *             if the respondent isn't on the requested partner list of the
     *             request
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @throws Exception
     *             throws if any error happen
	 * @since 1.1
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
        String requestId = xpath.compile("/DataResponse/RequestID/text()")
                .evaluate(doc);
        context.put("requestId", requestId);

        // Build DataResponse from XML message
        DataResponse response = new DataResponse();
        response.setRequestId(requestId);
        response.setRespondentId(user.getId());
        response.setRequestDenied(DatatypeConverter.parseBoolean(xpath.compile(
                "/DataResponse/RequestDenied/text()").evaluate(doc)));
        response.setResponseTimestamp(new Date(message.getJMSTimestamp()));
        boolean useCache = false;
        String userCacheValue=xpath.compile(
                "string(/DataResponse/Data/@useCache)").evaluate(doc);
        if(!Helper.isNullOrEmpty(userCacheValue)){
            useCache=DatatypeConverter.parseBoolean(userCacheValue);
        }
        if (!useCache) {
            response.setData(xpath.compile("/DataResponse/Data/text()")
                    .evaluate(doc));
        }
        String errorMessage = xpath.compile(
                "string(/DataResponse/ErrorMessage/text())").evaluate(doc);
        if (!Helper.isNullOrEmpty(errorMessage)) {
            response.setErrorMessage(errorMessage);
        }
        // Add data response to the network
        getDataExchangeService().addDataResponse(response);
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }
}
