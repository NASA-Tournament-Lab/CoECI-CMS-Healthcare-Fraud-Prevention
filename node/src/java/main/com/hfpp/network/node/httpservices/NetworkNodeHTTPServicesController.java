/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.httpservices;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hfpp.network.node.ConfigurationException;
import com.hfpp.network.node.Helper;
import com.hfpp.network.node.services.AuthenticationException;
import com.hfpp.network.node.services.AuthorizationException;
import com.hfpp.network.node.services.DataExchangeService;
import com.hfpp.network.node.services.DataRequestExpiredException;
import com.hfpp.network.node.services.InsufficientParticipationRatioException;
import com.hfpp.network.node.services.InvalidMessageException;
import com.hfpp.network.node.services.NetworkNodeServiceException;

/**
 * <p>
 * This is the Spring MVC controller that implements the HTTP services exposed by Network Node to Study Management
 * Software.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since its internal state isn't expected to
 * change after Spring IoC initialization, and all dependencies are thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
@Controller
public class NetworkNodeHTTPServicesController {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = NetworkNodeHTTPServicesController.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the DataExchangeService to access Data Exchange Network. It should be non-null. It is required.
     */
    private DataExchangeService dataExchangeService;

    /**
     * Creates an instance of NetworkNodeHTTPServicesController.
     */
    public NetworkNodeHTTPServicesController() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException
     *             if any required field is not initialized properly (dataExchangeService is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        Helper.checkState(dataExchangeService == null, "'dataExchangeService' can't be null.");
    }

    /**
     * This method exposes HTTP Service "Initiate Data Request".
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the acknowledgement XML message responded by the Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is not provided or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML fails XSD schema validation, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing invalid message error.
     * @throws InsufficientParticipationRatioException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing insufficient
     *             participation ratio error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    @RequestMapping(value = "/data_request", method = RequestMethod.POST, consumes = "application/xml",
        produces = "application/xml")
    @ResponseBody
    public String initiateDataRequest(@RequestHeader("x-hfpp-username") String username,
        @RequestHeader("x-hfpp-password") String password, @RequestBody String messageXML)
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".initiateDataRequest(@RequestHeader(\"x-hfpp-username\") String username,"
            + " @RequestHeader(\"x-hfpp-password\") String password, @RequestBody String messageXML)";

        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"username", "password", "messageXML"},
            new Object[] {username, "******", messageXML});

        try {
            String result = dataExchangeService.initiateDataRequest(username, password, messageXML);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] {result});
            return result;
        } catch (NetworkNodeServiceException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature, e);
        }
    }

    /**
     * This method exposes HTTP Service "Initiate General Services".
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the acknowledgement XML message responded by the Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is not provided or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML fails XSD schema validation, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing invalid message error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    @RequestMapping(value = "/general_service", method = RequestMethod.POST, consumes = "application/xml",
        produces = "application/xml")
    @ResponseBody
    public String initiateGeneralServiceRequest(@RequestHeader("x-hfpp-username") String username,
        @RequestHeader("x-hfpp-password") String password, @RequestBody String messageXML)
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".initiateGeneralServiceRequest(@RequestHeader(\"x-hfpp-username\")"
            + " String username, @RequestHeader(\"x-hfpp-password\") String password, @RequestBody String messageXML)";

        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"username", "password", "messageXML"},
            new Object[] {username, "******", messageXML});

        try {
            String result = dataExchangeService.initiateGeneralServiceRequest(username, password, messageXML);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] {result});
            return result;
        } catch (NetworkNodeServiceException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature, e);
        }
    }

    /**
     * This method exposes HTTP Service "Initiate Analysis Result Delivery".
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the acknowledgement XML message responded by the Network Hub
     *
     @throws AuthenticationException
     *             if username/password is not provided or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML fails XSD schema validation, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing invalid message error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    @RequestMapping(value = "/analysis_result", method = RequestMethod.POST, consumes = "application/xml",
        produces = "application/xml")
    @ResponseBody
    public String deliverAnalysisResult(@RequestHeader("x-hfpp-username") String username,
        @RequestHeader("x-hfpp-password") String password, @RequestBody String messageXML)
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".deliverAnalysisResult(@RequestHeader(\"x-hfpp-username\")"
            + " String username, @RequestHeader(\"x-hfpp-password\") String password, @RequestBody String messageXML)";

        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"username", "password", "messageXML"},
            new Object[] {username, "******", messageXML});

        try {
            String result = dataExchangeService.deliverAnalysisResult(username, password, messageXML);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] {result});
            return result;
        } catch (NetworkNodeServiceException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature, e);
        }
    }

    /**
     * This method exposes HTTP Service "Respond to Data Request".
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the acknowledgement XML message responded by the Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is not provided or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML fails XSD schema validation, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing invalid message error.
     * @throws DataRequestExpiredException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing data request
     *             expired error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    @RequestMapping(value = "/data_response", method = RequestMethod.POST, consumes = "application/xml",
        produces = "application/xml")
    @ResponseBody
    public String respondToDataRequest(@RequestHeader("x-hfpp-username") String username,
        @RequestHeader("x-hfpp-password") String password, @RequestBody String messageXML)
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".respondToDataRequest(@RequestHeader(\"x-hfpp-username\")"
            + " String username, @RequestHeader(\"x-hfpp-password\") String password, @RequestBody String messageXML)";

        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"username", "password", "messageXML"},
            new Object[] {username, "******", messageXML});

        try {
            String result = dataExchangeService.respondToDataRequest(username, password, messageXML);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] {result});
            return result;
        } catch (NetworkNodeServiceException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature, e);
        }
    }

    /**
     * This method is the ExceptionHandler for AuthenticationException, which responds with HTTP 401. The message of the
     * exception(if not null) will be returned as the response body.
     *
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleAuthenticationException(AuthenticationException ex) {
        String signature = CLASS_NAME + ".handleAuthenticationException(AuthenticationException ex)";

        return handleException(signature, ex);
    }

    /**
     * This method is the ExceptionHandler for AuthorizationException, which responds with HTTP 403. The message of the
     * exception(if not null) will be returned as the response body.
     *
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String handleAuthorizationException(AuthorizationException ex) {
        String signature = CLASS_NAME + ".handleAuthorizationException(AuthorizationException ex)";

        return handleException(signature, ex);
    }

    /**
     * This method is the ExceptionHandler for InvalidMessageException, which responds with HTTP 400. The message of the
     * exception(if not null) will be returned as the response body.
     *
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    @ExceptionHandler(InvalidMessageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleInvalidMessageException(InvalidMessageException ex) {
        String signature = CLASS_NAME + ".handleInvalidMessageException(InvalidMessageException ex)";

        return handleException(signature, ex);
    }

    /**
     * This method is the ExceptionHandler for DataRequestExpiredException, which responds with HTTP 400. The message of
     * the exception(if not null) will be returned as the response body.
     *
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    @ExceptionHandler(DataRequestExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleDataRequestExpiredException(DataRequestExpiredException ex) {
        String signature = CLASS_NAME + ".handleDataRequestExpiredException(DataRequestExpiredException ex)";

        return handleException(signature, ex);
    }

    /**
     * This method is the ExceptionHandler for InsufficientParticipationRatioException, which responds with HTTP 403.
     * The message of the exception(if not null) will be returned as the response body.
     *
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    @ExceptionHandler(InsufficientParticipationRatioException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String handleInsufficientParticipationRatioException(InsufficientParticipationRatioException ex) {
        String signature = CLASS_NAME
            + ".handleInsufficientParticipationRatioException(InsufficientParticipationRatioException ex)";

        return handleException(signature, ex);
    }

    /**
     * This method is the ExceptionHandler for general NetworkNodeServiceException, which responds with HTTP 500. The
     * message of the exception(if not null) will be returned as the response body.
     *
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    @ExceptionHandler(NetworkNodeServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleNetworkNodeServiceException(NetworkNodeServiceException ex) {
        String signature = CLASS_NAME + ".handleNetworkNodeServiceException(NetworkNodeServiceException ex)";

        return handleException(signature, ex);
    }

    /**
     * Sets the DataExchangeService to access Data Exchange Network.
     *
     * @param dataExchangeService
     *            the DataExchangeService to access Data Exchange Network.
     */
    public void setDataExchangeService(DataExchangeService dataExchangeService) {
        this.dataExchangeService = dataExchangeService;
    }

    /**
     * Handles the exception.
     *
     * @param <T>
     *            the exception type
     * @param signature
     *            the signature
     * @param ex
     *            the exception
     *
     * @return the response body
     */
    private <T extends NetworkNodeServiceException> String handleException(String signature, T ex) {
        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"ex"},
            new Object[] {ex});

        String result = ex.getMessage() == null ? "" : ex.getMessage();

        // Log exit
        Helper.logExit(LOGGER, signature, new Object[] {result});
        return result;
    }
}
