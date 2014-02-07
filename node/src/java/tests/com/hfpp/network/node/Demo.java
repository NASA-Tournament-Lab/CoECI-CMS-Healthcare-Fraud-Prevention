/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

/**
 * <p>
 * Shows usage for the assembly.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class Demo {
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
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(Demo.class);
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
        Properties properties = new Properties();
        InputStream inStream = new FileInputStream(TestsHelper.TEST_FILES + "qpid.properties");
        try {
            properties.load(inStream);
        } finally {
            inStream.close();
        }
        context = new InitialContext(properties);
        connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionfactory");
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
     * Accuracy test for <code>HTTP Service "Initiate Data Request"</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_InitiateDataRequest() throws Exception {
        final String messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "data_request.xml");
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
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(TestsHelper.URL_BASE + "/services/data_request");
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setDoOutput(true);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setRequestProperty("x-hfpp-username", "user1");
                            urlConnection.setRequestProperty("x-hfpp-password", "pass1");
                            urlConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                            // Use default chunk length
                            urlConnection.setChunkedStreamingMode(0);

                            OutputStream outputStream = urlConnection.getOutputStream();
                            try {
                                outputStream.write(messageXML.getBytes("utf-8"));
                                outputStream.flush();
                            } finally {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            // Ignore
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("The result should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);
    }

    /**
     * <p>
     * Accuracy test for <code>HTTP Service "Initiate General Services".</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_InitiateGeneralServiceRequest() throws Exception {
        final String messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "partner_list_request.xml");
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
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(TestsHelper.URL_BASE + "/services/general_service");
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setDoOutput(true);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setRequestProperty("x-hfpp-username", "user1");
                            urlConnection.setRequestProperty("x-hfpp-password", "pass1");
                            urlConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                            // Use default chunk length
                            urlConnection.setChunkedStreamingMode(0);

                            OutputStream outputStream = urlConnection.getOutputStream();
                            try {
                                outputStream.write(messageXML.getBytes("utf-8"));
                                outputStream.flush();
                            } finally {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            // Ignore
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("The result should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);
    }

    /**
     * <p>
     * Accuracy test for <code>HTTP Service "Initiate Analysis Result Delivery"</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_DeliverAnalysisResult() throws Exception {
        final String messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "analysis_result.xml");
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
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(TestsHelper.URL_BASE + "/services/analysis_result");
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setDoOutput(true);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setRequestProperty("x-hfpp-username", "user1");
                            urlConnection.setRequestProperty("x-hfpp-password", "pass1");
                            urlConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                            // Use default chunk length
                            urlConnection.setChunkedStreamingMode(0);

                            OutputStream outputStream = urlConnection.getOutputStream();
                            try {
                                outputStream.write(messageXML.getBytes("utf-8"));
                                outputStream.flush();
                            } finally {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            // Ignore
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("The result should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);
    }

    /**
     * <p>
     * Accuracy test for <code>HTTP Service "Respond to Data Request"</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_RespondToDataRequest() throws Exception {
        final String messageXML = TestsHelper.readFile(TestsHelper.XML_MESSAGE_FILES + "data_response_non_cached.xml");
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
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(TestsHelper.URL_BASE + "/services/data_response");
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setDoOutput(true);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setRequestProperty("x-hfpp-username", "user1");
                            urlConnection.setRequestProperty("x-hfpp-password", "pass1");
                            urlConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                            // Use default chunk length
                            urlConnection.setChunkedStreamingMode(0);

                            OutputStream outputStream = urlConnection.getOutputStream();
                            try {
                                outputStream.write(messageXML.getBytes("utf-8"));
                                outputStream.flush();
                            } finally {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            // Ignore
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                });
            thread.start();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("The result should be correct.",
                messageXML, message.getText().trim().replace("\r\n", "\n"));

            MessageProducer messageProducer = session.createProducer(message.getJMSReplyTo());

            message = session.createTextMessage(responseXML);
            messageProducer.send(message);

        } finally {
            connection.close();
        }

        Thread.sleep(5000);
    }
}