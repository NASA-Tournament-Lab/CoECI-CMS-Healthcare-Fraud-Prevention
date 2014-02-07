/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.qpid.client.AMQAnyDestination;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
 * This is the implementation of DataExchangeService.
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
public class DataExchangeServiceImpl implements DataExchangeService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = DataExchangeServiceImpl.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the string 'DataRequest'.
     */
    private static final String DATA_REQUEST = "DataRequest";

    /**
     * Represents the string 'PartnerListRequest'.
     */
    private static final String PARTNER_LIST_REQUEST = "PartnerListRequest";

    /**
     * Represents the string 'PartnerStatisticsRequest'.
     */
    private static final String PARTNER_STATISTICS_REQUEST = "PartnerStatisticsRequest";

    /**
     * Represents the string 'PartnerAddRequest'
     */
    private static final String PARTNER_ADD_REQUEST = "PartnerAddRequest";

    /**
     * Represents the string 'PartnerEditRequest'
     */
    private static final String PARTNER_EDIT_REQUEST = "PartnerEditRequest";

    /**
     * Represents the string 'PartnerDeleteRequest'
     */
    private static final String PARTNER_DELETE_REQUEST = "PartnerDeleteRequest";

    /**
     * Represents the string 'PartnerGetRequest'
     */
    private static final String PARTNER_GET_REQUEST = "PartnerGetRequest";

    /**
     * Represents the string 'PartnerRoleListRequest'
     */
    private static final String PARTNER_ROLE_LIST_REQUEST = "PartnerRoleListRequest";
    
    /**
     * Represents the string 'AnalysisResult'.
     */
    private static final String ANALYSIS_RESULT = "AnalysisResult";

    /**
     * Represents the string 'DataResponse'.
     */
    private static final String DATA_RESPONSE = "DataResponse";

    /**
     * Represents the string '000'.
     */
    private static final String CODE_000 = "000";

    /**
     * Represents the string '001'.
     */
    private static final String CODE_001 = "001";

    /**
     * Represents the string '002'.
     */
    private static final String CODE_002 = "002";

    /**
     * Represents the string '003'.
     */
    private static final String CODE_003 = "003";

    /**
     * Represents the string '100'.
     */
    private static final String CODE_100 = "100";

    /**
     * Represents the JmsTemplate used to send and receive JMS messages. It should be non-null. It is required.
     */
    private JmsTemplate jmsTemplate;

    /**
     * Represents the mapping from message type (Root XML element tag) to the AMQP queue names. It should be non-null
     * non-empty map, keys/values should be non-null non-empty strings. It is optional. It will be initialized to a map
     * containing the default entries in constructor (see constructor for details). Usually there is no need to set this
     * explicitly, it is configurable just for possible future queue name changes.
     */
    private Map<String, String> queueNames;

    /**
     * Represents the mapping from message type (Root XML element tag) to the XML schema file paths. It should be
     * non-null non-empty map, keys/values should be non-null non-empty strings. It is required
     */
    private Map<String, String> schemaPaths;

    /**
     * Represents the mapping from message type (Root XML element tag) to the XPath expression that can be used to
     * extract the "ErrorCode" value from response message. It should be non-null non-empty map, keys/values should be
     * non-null non-empty strings. It is optional. It will be initialized to a map containing the default entries in
     * constructor (see constructor for details). Usually there is no need to set this explicitly, it is configurable
     * just for possible future changes.
     */
    private Map<String, String> errorCodeXPathExpressions;

    /**
     * Represents the error code to corresponding exception class mappings. It should be non-null non-empty map. Key
     * should be non-null non-empty string representing the message type. Value should be non-null non-empty map
     * representing the error code to exception class mapping for the message type (key of the map should be non-null
     * non-empty string representing the error code, value of the map should be non-null exception class). It is
     * optional. It will be initialized to a map containing the default entries in constructor (see constructor for
     * details). Usually there is no need to set this explicitly, it is configurable just for possible future changes.
     */
    private Map<String, Map<String, Class<? extends NetworkNodeServiceException>>> errorCodeToExceptionMappings;

    /**
     * Represents the message types supported by general services. It should be non-null non-empty list, values should
     * be non-null non-empty strings. It is optional. It will be initialized to a list containing the default entries in
     * constructor (see constructor for details). Usually there is no need to set this explicitly, it is configurable
     * just for possible future changes.
     */
    private List<String> generalServicesMessageTypes;

    /**
     * Represents the DocumentBuilderFactory used to parse XML documents. It should be non-null. It will be initialized
     * in place and won't change afterwards.
     */
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /**
     * Represents the XPathFactory used to create XPath instances that can be used to extract data from XML. It should
     * be non-null. It will be initialized in place and won't change afterwards.
     */
    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    /**
     * Represents the mapping from message type (Root XML element tag) to the XML Schema object that can be used to
     * validate the XML messages. It should be non-null non-empty map, keys should be non-null non-empty string, values
     * should be non-null Schema object. It will be initialized in checkConfiguration() method by instantiating all XSD
     * schemas specified in schemaPaths configuration.
     */
    private Map<String, Schema> schemas;

    /**
     * Creates an instance of DataExchangeServiceImpl.
     */
    public DataExchangeServiceImpl() {
        // Initialize the queue names
        queueNames = new HashMap<String, String>();
        queueNames.put(ANALYSIS_RESULT, "hfpp.queue.analysis_results");
        queueNames.put(DATA_RESPONSE, "hfpp.queue.data_responses");
        queueNames.put(DATA_REQUEST, "hfpp.queue.data_requests");
        queueNames.put(PARTNER_LIST_REQUEST, "hfpp.queue.general_services");
        queueNames.put(PARTNER_STATISTICS_REQUEST, "hfpp.queue.general_services");
        queueNames.put(PARTNER_ADD_REQUEST, "hfpp.queue.general_services");
        queueNames.put(PARTNER_EDIT_REQUEST, "hfpp.queue.general_services");
        queueNames.put(PARTNER_GET_REQUEST, "hfpp.queue.general_services");
        queueNames.put(PARTNER_DELETE_REQUEST, "hfpp.queue.general_services");
        queueNames.put(PARTNER_ROLE_LIST_REQUEST, "hfpp.queue.general_services");

        // Initialize errorCodeXPathExpressions
        errorCodeXPathExpressions = new HashMap<String, String>();
        errorCodeXPathExpressions.put(ANALYSIS_RESULT, "/AnalysisResultAcknowlegement/ErrorCode/text()");
        errorCodeXPathExpressions.put(DATA_RESPONSE, "/DataResponseAcknowlegement/ErrorCode/text()");
        errorCodeXPathExpressions.put(DATA_REQUEST, "/DataRequestAcknowlegement/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_LIST_REQUEST, "/PartnerList/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_STATISTICS_REQUEST, "/PartnerStatistics/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_ADD_REQUEST, "/PartnerAdd/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_EDIT_REQUEST, "/PartnerEdit/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_GET_REQUEST, "/PartnerGet/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_DELETE_REQUEST, "/PartnerDelete/ErrorCode/text()");
        errorCodeXPathExpressions.put(PARTNER_ROLE_LIST_REQUEST, "/PartnerRoleList/ErrorCode/text()");

        // Initialize errorCodeToExceptionMappings
        errorCodeToExceptionMappings = new HashMap<String, Map<String, Class<? extends NetworkNodeServiceException>>>();

        Map<String, Class<? extends NetworkNodeServiceException>> mapping = createMap(
            new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
            new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                AuthorizationException.class, InvalidMessageException.class});
        errorCodeToExceptionMappings.put(ANALYSIS_RESULT, mapping);

        mapping = createMap(
            new String[] {CODE_000, CODE_001, CODE_002, CODE_100, CODE_003},
            new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                AuthorizationException.class, InvalidMessageException.class,
                DataRequestExpiredException.class});
        errorCodeToExceptionMappings.put(DATA_RESPONSE, mapping);

        mapping = createMap(
            new String[] {CODE_000, CODE_001, CODE_002, CODE_100, CODE_003},
            new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                AuthorizationException.class, InvalidMessageException.class,
                InsufficientParticipationRatioException.class});
        errorCodeToExceptionMappings.put(DATA_REQUEST, mapping);

        mapping = createMap(
            new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
            new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                AuthorizationException.class, InvalidMessageException.class});
        errorCodeToExceptionMappings.put(PARTNER_LIST_REQUEST, mapping);

        mapping = createMap(
            new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
            new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                AuthorizationException.class, InvalidMessageException.class});
        errorCodeToExceptionMappings.put(PARTNER_STATISTICS_REQUEST, mapping);

        mapping = createMap(
                new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
                new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                    AuthorizationException.class, InvalidMessageException.class});
            errorCodeToExceptionMappings.put(PARTNER_ADD_REQUEST, mapping);

        mapping = createMap(
                new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
                new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                    AuthorizationException.class, InvalidMessageException.class});
            errorCodeToExceptionMappings.put(PARTNER_EDIT_REQUEST, mapping);

        mapping = createMap(
                new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
                new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                    AuthorizationException.class, InvalidMessageException.class});
            errorCodeToExceptionMappings.put(PARTNER_GET_REQUEST, mapping);

        mapping = createMap(
                new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
                new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                    AuthorizationException.class, InvalidMessageException.class});
            errorCodeToExceptionMappings.put(PARTNER_DELETE_REQUEST, mapping);

        mapping = createMap(
                new String[] {CODE_000, CODE_001, CODE_002, CODE_100},
                new Class<?>[] {NetworkNodeServiceException.class, AuthenticationException.class,
                    AuthorizationException.class, InvalidMessageException.class});
            errorCodeToExceptionMappings.put(PARTNER_ROLE_LIST_REQUEST, mapping);

        // Initialize generalServiceMessageTypes
        generalServicesMessageTypes = new ArrayList<String>();
        generalServicesMessageTypes.add(PARTNER_LIST_REQUEST);
        generalServicesMessageTypes.add(PARTNER_STATISTICS_REQUEST);
        generalServicesMessageTypes.add(PARTNER_ADD_REQUEST);
        generalServicesMessageTypes.add(PARTNER_EDIT_REQUEST);
        generalServicesMessageTypes.add(PARTNER_GET_REQUEST);
        generalServicesMessageTypes.add(PARTNER_DELETE_REQUEST);
        generalServicesMessageTypes.add(PARTNER_ROLE_LIST_REQUEST);
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException
     *             if any required field is not initialized properly (jmsTemplate is null; queueNames,
     *             schemaPaths or errorCodeXPathExpressions is null/empty or contains null/empty key or value;
     *             generalServicesMessageTypes is null/empty or contains null/empty element; any error occurs).
     */
    @PostConstruct
    public void checkConfiguration() {
        Helper.checkState(jmsTemplate == null, "'jmsTemplate' can't be null.");

        checkMap(queueNames, "queueNames");
        checkMap(schemaPaths, "schemaPaths");
        checkMap(errorCodeXPathExpressions, "errorCodeXPathExpressions");

        Helper.checkState(errorCodeToExceptionMappings == null, "'errorCodeToExceptionMappings' can't be null.");
        Helper.checkState(errorCodeToExceptionMappings.isEmpty(), "'errorCodeToExceptionMappings' can't be empty.");
        for (Entry<String, Map<String, Class<? extends NetworkNodeServiceException>>> entry
            : errorCodeToExceptionMappings.entrySet()) {
            Helper.checkState(Helper.isNullOrEmpty(entry.getKey()),
                "'errorCodeToExceptionMappings' can't contain null/empty key.");

            Map<String, Class<? extends NetworkNodeServiceException>> value = entry.getValue();
            Helper.checkState(value == null, "'errorCodeToExceptionMappings' can't contain null value.");
            Helper.checkState(value.isEmpty(), "'errorCodeToExceptionMappings' can't contain empty value.");

            for (Entry<String, Class<? extends NetworkNodeServiceException>> valueEntry : value.entrySet()) {
                Helper.checkState(Helper.isNullOrEmpty(valueEntry.getKey()),
                    "Value of 'errorCodeToExceptionMappings' can't contain null/empty key.");
                Helper.checkState(valueEntry.getValue() == null,
                    "Value of 'errorCodeToExceptionMappings' can't contain null value.");
            }
        }

        Helper.checkState(generalServicesMessageTypes == null, "'generalServicesMessageTypes' can't be null.");
        Helper.checkState(generalServicesMessageTypes.isEmpty(), "'generalServicesMessageTypes' can't be empty.");
        for (String generalServicesMessageType : generalServicesMessageTypes) {
            Helper.checkState(Helper.isNullOrEmpty(generalServicesMessageType),
                "'generalServicesMessageTypes' can't contain null/empty element.");
        }

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        schemas = new HashMap<String, Schema>();
        for (Entry<String, String> entry : schemaPaths.entrySet()) {
            try {
                schemas.put(entry.getKey(), factory.newSchema(new File(entry.getValue())));
            } catch (SAXException e) {
                throw new ConfigurationException("The schema is invalid.", e);
            }
        }
    }

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
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".initiateDataRequest(String username, String password, String messageXML)";

        return sendAndReceiveResponse(signature, Arrays.asList(DATA_REQUEST), username, password, messageXML);
    }

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
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".initiateDataRequest(String username, String password, String messageXML)";

        return sendAndReceiveResponse(signature, Arrays.asList(PARTNER_LIST_REQUEST, PARTNER_STATISTICS_REQUEST,
                PARTNER_ADD_REQUEST, PARTNER_EDIT_REQUEST, PARTNER_DELETE_REQUEST, PARTNER_GET_REQUEST,
                PARTNER_ROLE_LIST_REQUEST), username, password, messageXML);
    }

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
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".initiateDataRequest(String username, String password, String messageXML)";

        return sendAndReceiveResponse(signature, Arrays.asList(ANALYSIS_RESULT), username, password, messageXML);
    }

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
        throws NetworkNodeServiceException {
        String signature = CLASS_NAME + ".initiateDataRequest(String username, String password, String messageXML)";

        return sendAndReceiveResponse(signature, Arrays.asList(DATA_RESPONSE), username, password, messageXML);
    }

    /**
     * Sets the JmsTemplate used to send and receive JMS messages.
     *
     * @param jmsTemplate
     *            the JmsTemplate used to send and receive JMS messages.
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Sets the mapping from message type (Root XML element tag) to the AMQP queue names.
     *
     * @param queueNames
     *            the mapping from message type (Root XML element tag) to the AMQP queue names.
     */
    public void setQueueNames(Map<String, String> queueNames) {
        this.queueNames = queueNames;
    }

    /**
     * Sets the mapping from message type (Root XML element tag) to the XML schema file paths.
     *
     * @param schemaPaths
     *            the mapping from message type (Root XML element tag) to the XML schema file paths.
     */
    public void setSchemaPaths(Map<String, String> schemaPaths) {
        this.schemaPaths = schemaPaths;
    }

    /**
     * Sets the mapping from message type (Root XML element tag) to the XPath expression that can be used to extract the
     * "ErrorCode" value from response message.
     *
     * @param errorCodeXPathExpressions
     *            the mapping from message type (Root XML element tag) to the XPath expression that can be used to
     *            extract the "ErrorCode" value from response message.
     */
    public void setErrorCodeXPathExpressions(Map<String, String> errorCodeXPathExpressions) {
        this.errorCodeXPathExpressions = errorCodeXPathExpressions;
    }

    /**
     * Sets the error code to corresponding exception class mappings.
     *
     * @param errorCodeToExceptionMappings
     *            the error code to corresponding exception class mappings.
     */
    public void setErrorCodeToExceptionMappings(
        Map<String, Map<String, Class<? extends NetworkNodeServiceException>>> errorCodeToExceptionMappings) {
        this.errorCodeToExceptionMappings = errorCodeToExceptionMappings;
    }

    /**
     * Sets the message types supported by general services.
     *
     * @param generalServicesMessageTypes
     *            the message types supported by general services.
     */
    public void setGeneralServicesMessageTypes(List<String> generalServicesMessageTypes) {
        this.generalServicesMessageTypes = generalServicesMessageTypes;
    }

    /**
     * <p>
     * Checks state of map.
     * </p>
     *
     * @param map
     *            the map.
     * @param name
     *            the name.
     *
     * @throws ConfigurationException
     *             if map is null/empty or contains null/empty key or value
     */
    private static void checkMap(Map<String, String> map, String name) {
        Helper.checkState(map == null, "'" + name + "' can't be null.");
        Helper.checkState(map.isEmpty(), "'" + name + "' can't be empty.");
        for (Entry<String, String> entry : map.entrySet()) {
            Helper.checkState(Helper.isNullOrEmpty(entry.getKey()), "'" + name + "' can't contain null/empty key.");
            Helper.checkState(Helper.isNullOrEmpty(entry.getValue()), "'" + name + "' can't contain null/empty value.");
        }
    }

    /**
     * Creates the mapping.
     *
     * @param codes
     *            the error codes
     * @param exceptions
     *            the exceptions
     *
     * @return the mapping.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Class<? extends NetworkNodeServiceException>> createMap(String[] codes,
        Class<?>[] exceptions) {
        Map<String, Class<? extends NetworkNodeServiceException>> mapping =
            new HashMap<String, Class<? extends NetworkNodeServiceException>>();

        for (int i = 0; i < codes.length; i++) {
            mapping.put(codes[i], (Class<? extends NetworkNodeServiceException>) exceptions[i]);
        }

        return mapping;
    }

    /**
     * This is a private method that implements the general logic for sending message to Network Hub and receiving
     * response. Before being sent to Network Hub, the message will be validated against XSD schema. The received
     * response will be parsed to DOM Document object, and errors will be checked.
     *
     * @param signature
     *            the signature
     * @param supportedMessageTypes
     *            the supported message types for the message
     * @param username
     *            the username
     * @param password
     *            the password
     * @param messageXML
     *            the message XML
     *
     * @return the response XML received from Network Hub
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
     * @throws DataRequestExpiredException
     *             if the Network Hub responded with an XML message with &lt;ErrorCode&gt; representing data request
     *             expired error.
     * @throws NetworkNodeServiceException
     *             if any other unexpected error occurred
     */
    private String sendAndReceiveResponse(String signature, List<String> supportedMessageTypes, final String username,
        final String password, final String messageXML) throws NetworkNodeServiceException {
        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"username", "password", "messageXML"},
            new Object[] {username, "******", messageXML});

        // Check if user credential is provided
        if (Helper.isNullOrEmpty(username) || Helper.isNullOrEmpty(password)) {
            // Log exception
            throw Helper.logException(LOGGER, signature, new AuthenticationException(
                "'username' or 'password' is null/empty."));
        }
        if (messageXML == null) {
            // Log exception
            throw Helper.logException(LOGGER, signature, new InvalidMessageException("'messageXML' is null."));
        }

        try {
            byte[] messageXMLBytes = messageXML.getBytes("utf-8");

            // Determine the message type of the XML document
            String messageType = null;
            if (supportedMessageTypes.size() == 1) {
                messageType = supportedMessageTypes.get(0);
            } else {
                // Parse XML message to determine the message type
                Document doc = documentBuilderFactory.newDocumentBuilder().parse(
                    new ByteArrayInputStream(messageXMLBytes));
                messageType = doc.getDocumentElement().getTagName();
                if (!supportedMessageTypes.contains(messageType)) {
                    // Log exception
                    throw Helper.logException(LOGGER, signature, new InvalidMessageException(
                        "Not supported message type '" + messageType + "'."));
                }
            }

            // Validate XML message against XSD schema
            Schema schema = schemas.get(messageType);
            Validator validator = schema.newValidator();
            try {
                validator.validate(new StreamSource(new ByteArrayInputStream(messageXMLBytes)));
            } catch (SAXException ex) {
                // Log exception
                throw Helper.logException(LOGGER, signature, new InvalidMessageException(
                    "'messageXML' fails XSD schema validation."));
            }

            // Send message to AMQP queue
            Destination queue = new AMQAnyDestination("ADDR:" + queueNames.get(messageType)
                + "; {create: never, delete: never}");
            final Destination replyToQueue = new AMQAnyDestination("ADDR:" + UUID.randomUUID().toString()
                + "; {create: always, delete: always, durable: false,"
                + " x-declare: { auto-delete: true,  exclusive: true}}");

            jmsTemplate.send(queue,
                /**
                 * <p>
                 * The message creator.
                 * </p>
                 *
                 * <p>
                 * <strong>Thread Safety: </strong> This class is immutable and thread safe.
                 * </p>
                 *
                 * @author flying2hk, sparemax
                 * @version 1.0
                 */
                new MessageCreator() {
                    /**
                     * Creates the message.
                     *
                     * @param session
                     *            the session
                     *
                     * @throws JMSException
                     *             if any error occurs
                     */
                    public Message createMessage(Session session) throws JMSException {
                        Message message = session.createTextMessage(messageXML);
                        message.setStringProperty("x-hfpp-username", username);
                        message.setStringProperty("x-hfpp-password", password);
                        message.setJMSReplyTo(replyToQueue);
                        return message;
                    }
                });

            // Receive response
            Message responseMessage = jmsTemplate.receive(replyToQueue);
            responseMessage.acknowledge();

            String responseXML = ((TextMessage) responseMessage).getText();

            // Parse response XML
            Document doc = documentBuilderFactory.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));

            // Check for common errors
            String errorCodeXPathExpression = errorCodeXPathExpressions.get(messageType);
            if (errorCodeXPathExpression != null) {
                XPath xpath = xpathFactory.newXPath();
                String errorCode = xpath.compile(errorCodeXPathExpression).evaluate(doc);
                if ((errorCode != null) && (errorCode.trim().length() != 0)) {
                    Class<? extends NetworkNodeServiceException> exceptionClass = errorCodeToExceptionMappings.get(
                        messageType).get(errorCode);
                    if (exceptionClass == null) {
                        // Log exception
                        throw Helper.logException(LOGGER, signature, new NetworkNodeServiceException(responseXML));
                    } else {
                        throw Helper.logException(LOGGER, signature, exceptionClass.getConstructor(String.class)
                            .newInstance(responseXML));
                    }
                }
            }
            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] {responseXML});
            return responseXML;
        } catch (IOException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("An I/O error has occurred.", e));
        } catch (SAXException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("Failed to parse the XML.", e));
        } catch (ParserConfigurationException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("Failed to create a DocumentBuilder instance.", e));
        } catch (URISyntaxException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("The URI is invalid.", e));
        } catch (JmsException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("A JMS error has occurred.", e));
        } catch (JMSException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("A JMS error has occurred.", e));
        } catch (XPathExpressionException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("An XPath expression error has occurred.", e));
        } catch (SecurityException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("A security error has occurred.", e));
        } catch (InstantiationException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("Failed to create the exception.", e));
        } catch (IllegalAccessException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("Failed to create the exception.", e));
        } catch (InvocationTargetException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("Failed to create the exception.", e));
        } catch (NoSuchMethodException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                new NetworkNodeServiceException("Failed to create the exception.", e));
        }
    }
}
