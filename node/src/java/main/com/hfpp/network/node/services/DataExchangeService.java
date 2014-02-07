/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.services;

/**
 * <p>
 * The DataExchangeService is used to
 * <ul>
 * <li>initiate a data request</li>
 * <li>initiate general service request</li>
 * <li>deliver analysis result</li>
 * <li>respond to data request</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface DataExchangeService {
    /**
     * This method is used to initiate data request. This method will send "Data Request" message to
     * "hfpp.queue.data_requests" AMQP queue, wait for and return the "Data Request Acknowledgement" message.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the "Data Request Acknowledgement" message from Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is null or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML is null or fails XSD schema validation, or the Network Hub responded with an XML
     *             message with &lt;ErrorCode&gt; representing invalid message error.
     * @throws InsufficientParticipationRatioException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing insufficient
     *             participation ratio error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    public String initiateDataRequest(String username, String password, String messageXML)
        throws NetworkNodeServiceException;

    /**
     * This method is used to initiate general service request. This method will send "Partner Statistics Request" /
     * "Partner List Request" message to "hfpp.queue.general_services" AMQP queue, wait for and return the
     * "Partner Statistics" / "Partner List" message.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the response message from Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is null or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML is null or fails XSD schema validation, or the Network Hub responded with an XML
     *             message with &lt;ErrorCode&gt; representing invalid message error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    public String initiateGeneralServiceRequest(String username, String password, String messageXML)
        throws NetworkNodeServiceException;

    /**
     * This method is used to deliver analysis result. This method will send "Analysis Result" message to
     * "hfpp.queue.analysis_results" AMQP queue, wait for and return the "Analysis Result Acknowledgement" message.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the "Analysis Result Acknowledgement" message from Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is null or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML is null or fails XSD schema validation, or the Network Hub responded with an XML
     *             message with &lt;ErrorCode&gt; representing invalid message error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    public String deliverAnalysisResult(String username, String password, String messageXML)
        throws NetworkNodeServiceException;

    /**
     * This method is used to respond to data request. This method will send "Data Response" message to
     * "hfpp.queue.data_responses" AMQP queue, wait for and return the "Data Response Acknowledgement" message.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the "Data Response Acknowledgement" message from Network Hub
     *
     * @throws AuthenticationException
     *             if username/password is null or empty, or the Network Hub responded with an XML message with
     *             &lt;ErrorCode&gt; representing authentication error.
     * @throws AuthorizationException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing authorization
     *             error.
     * @throws InvalidMessageException
     *             if the messageXML is null or fails XSD schema validation, or the Network Hub responded with an XML
     *             message with &lt;ErrorCode&gt; representing invalid message error.
     * @throws DataRequestExpiredException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing data request
     *             expired error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    public String respondToDataRequest(String username, String password, String messageXML)
        throws NetworkNodeServiceException;
}
