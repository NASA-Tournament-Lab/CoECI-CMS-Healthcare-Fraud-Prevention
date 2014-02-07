/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.JUnit4TestAdapter;

import org.apache.qpid.client.AMQAnyDestination;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import com.hfpp.network.node.TestsHelper;
import com.hfpp.network.node.services.NetworkNodeServiceException;

/**
 * <p>
 * Unit tests for {@link DataExchangeServiceImpl} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class DataExchangeServiceImplUnitTests {
    /**
     * <p>
     * Represents the <code>DataExchangeServiceImpl</code> instance used in tests.
     * </p>
     */
    private DataExchangeServiceImpl instance;

    /**
     * <p>
     * Represents jms template used in tests.
     * </p>
     */
    private JmsTemplate jmsTemplate;

    /**
     * <p>
     * Represents schema paths used in tests.
     * </p>
     */
    private Map<String, String> schemaPaths;

    /**
     * <p>
     * Represents username used in tests.
     * </p>
     */
    private String username = "user1";

    /**
     * <p>
     * Represents password used in tests.
     * </p>
     */
    private String password = "pass1";

    /**
     * <p>
     * Represents message XML used in tests.
     * </p>
     */
    private String messageXML = "<Message>The message.</Message>";

    /**
     * <p>
     * Represents context used in tests.
     * </p>
     */
    private Context context;

    /**
     * <p>
     * Represents connection factory used in tests.
     * </p>
     */
    private ConnectionFactory connectionFactory;

    /**
     * <p>
     * Represents result used in tests.
     * </p>
     */
    private String result;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DataExchangeServiceImplUnitTests.class);
    }

    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Before
    public void setUp() throws Exception {
        instance = new DataExchangeServiceImpl();

        Properties properties = new Properties();
        InputStream inStream = new FileInputStream(TestsHelper.TEST_FILES + "qpid.properties");
        try {
            properties.load(inStream);
        } finally {
            inStream.close();
        }
        context = new InitialContext(properties);
        connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionfactory");
        jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);

        schemaPaths = new HashMap<String, String>();
        schemaPaths.put("DataRequest", TestsHelper.SCHEMA_FILES + "data_request.xsd");
        schemaPaths.put("PartnerListRequest", TestsHelper.SCHEMA_FILES + "partner_list_request.xsd");
        schemaPaths.put("PartnerStatisticsRequest", TestsHelper.SCHEMA_FILES + "partner_statistics_request.xsd");
        schemaPaths.put("PartnerAddRequest", TestsHelper.SCHEMA_FILES + "partner_add_request.xsd");
        schemaPaths.put("PartnerGetRequest", TestsHelper.SCHEMA_FILES + "partner_get_request.xsd");
        schemaPaths.put("PartnerDeleteRequest", TestsHelper.SCHEMA_FILES + "partner_delete_request.xsd");
        schemaPaths.put("PartnerEditRequest", TestsHelper.SCHEMA_FILES + "partner_edit_request.xsd");
        schemaPaths.put("PartnerRoleListRequest", TestsHelper.SCHEMA_FILES + "partner_role_list_request.xsd");
        schemaPaths.put("AnalysisResult", TestsHelper.SCHEMA_FILES + "analysis_result.xsd");
        schemaPaths.put("DataResponse", TestsHelper.SCHEMA_FILES + "data_response.xsd");

        instance.setJmsTemplate(jmsTemplate);
        instance.setSchemaPaths(schemaPaths);

        instance.checkConfiguration();
    }

    /**
     * <p>
     * Cleans up the unit tests.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @After
    public void tearDown() throws Exception {
        context.close();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>DataExchangeServiceImpl()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new DataExchangeServiceImpl();

        assertNull("'jmsTemplate' should be correct.", TestsHelper.getField(instance, "jmsTemplate"));
        assertNotNull("'queueNames' should be correct.", TestsHelper.getField(instance, "queueNames"));
        assertNull("'schemaPaths' should be correct.", TestsHelper.getField(instance, "schemaPaths"));
        assertNotNull("'errorCodeXPathExpressions' should be correct.",
            TestsHelper.getField(instance, "errorCodeXPathExpressions"));
        assertNotNull("'errorCodeToExceptionMappings' should be correct.",
            TestsHelper.getField(instance, "errorCodeToExceptionMappings"));
        assertNotNull("'generalServicesMessageTypes' should be correct.",
            TestsHelper.getField(instance, "generalServicesMessageTypes"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>initiateDataRequest(String username, String password,
     * String messageXML)</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_initiateDataRequest() throws Exception {
        messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "data_request.xml");
        String responseXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "data_request_ack_accepted.xml");

        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = new AMQAnyDestination("ADDR:" + "hfpp.queue.data_requests"
                + "; {create: never, delete: never}");
            MessageConsumer messageConsumer = session.createConsumer(destination);

            Thread thread = new Thread(
                /**
                 * The runnable implementation.
                 *
                 * @author sparemax
                 * @version 1.0
                 */
                new Runnable() {
                    /**
                     * Runs the method.
                     */
                    public void run() {
                        try {
                            result = instance.initiateDataRequest(username, password, messageXML);
                        } catch (NetworkNodeServiceException e) {
                            // Ignore
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("'initiateDataRequest' should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);

        assertEquals("'initiateDataRequest' should be correct.",
            responseXML, result.trim().replace("\r\n", "\n"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>initiateGeneralServiceRequest(String username, String password,
     * String messageXML)</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_initiateGeneralServiceRequest() throws Exception {
        messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "partner_list_request.xml");
        String responseXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "partner_list.xml");

        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = new AMQAnyDestination("ADDR:" + "hfpp.queue.general_services"
                + "; {create: never, delete: never}");
            MessageConsumer messageConsumer = session.createConsumer(destination);

            Thread thread = new Thread(
                /**
                 * The runnable implementation.
                 *
                 * @author sparemax
                 * @version 1.0
                 */
                new Runnable() {
                    /**
                     * Runs the method.
                     */
                    public void run() {
                        try {
                            result = instance.initiateGeneralServiceRequest(username, password, messageXML);
                        } catch (NetworkNodeServiceException e) {
                            // Ignore
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("'initiateGeneralServiceRequest' should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);

        assertEquals("'initiateGeneralServiceRequest' should be correct.",
            responseXML, result.trim().replace("\r\n", "\n"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>deliverAnalysisResult(String username, String password,
     * String messageXML)</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_deliverAnalysisResult() throws Exception {
        messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "analysis_result.xml");
        String responseXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "analysis_result_ack_accepted.xml");

        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = new AMQAnyDestination("ADDR:" + "hfpp.queue.analysis_results"
                + "; {create: never, delete: never}");
            MessageConsumer messageConsumer = session.createConsumer(destination);

            Thread thread = new Thread(
                /**
                 * The runnable implementation.
                 *
                 * @author sparemax
                 * @version 1.0
                 */
                new Runnable() {
                    /**
                     * Runs the method.
                     */
                    public void run() {
                        try {
                            result = instance.deliverAnalysisResult(username, password, messageXML);
                        } catch (NetworkNodeServiceException e) {
                            // Ignore
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("'deliverAnalysisResult' should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);

        assertEquals("'deliverAnalysisResult' should be correct.",
            responseXML, result.trim().replace("\r\n", "\n"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>respondToDataRequest(String username, String password,
     * String messageXML)</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_respondToDataRequest() throws Exception {
        messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "data_response_non_cached.xml");
        String responseXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "data_response_ack_accepted.xml");

        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = new AMQAnyDestination("ADDR:" + "hfpp.queue.data_responses"
                + "; {create: never, delete: never}");
            MessageConsumer messageConsumer = session.createConsumer(destination);

            Thread thread = new Thread(
                /**
                 * The runnable implementation.
                 *
                 * @author sparemax
                 * @version 1.0
                 */
                new Runnable() {
                    /**
                     * Runs the method.
                     */
                    public void run() {
                        try {
                            result = instance.respondToDataRequest(username, password, messageXML);
                        } catch (NetworkNodeServiceException e) {
                            // Ignore
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("'respondToDataRequest' should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);

        assertEquals("'respondToDataRequest' should be correct.",
            responseXML, result.trim().replace("\r\n", "\n"));
    }
}