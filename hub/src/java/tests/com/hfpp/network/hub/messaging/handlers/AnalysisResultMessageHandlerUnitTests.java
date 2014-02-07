/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Document;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link AnalysisResultMessageHandler} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AnalysisResultMessageHandlerUnitTests extends
        BaseTestCase {
    /**
     * <p>
     * Represents the <code>AnalysisResultMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    @Autowired
    private AnalysisResultMessageHandler instance;
    
    /**
     * Represents the JmsTemplate used to send JMS messages.
     */
    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * <p>
     * Represents request used in tests.
     * </p>
     */
    private DataRequest request;
    
    /**
     * <p>
     * Represents response used in tests.
     * </p>
     */
    private DataResponse response;
    
    /**
     * <p>
     * Represents the <code>AnalysisResultMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    private AnalysisResultMessageHandler testHandler;
    
    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        request = new DataRequest();
        request.setId(UUID.randomUUID().toString());
        request.setRequesterId("2");
        request.setQuery("query1");
        request.setRequestedPartners(Arrays.asList("2"));
        request.setExpirationTime(new Date());
        request.setCacheSafe(true);
        
        response = new DataResponse();
        response.setRequestId(request.getId());
        response.setRespondentId("2");
        response.setResponseTimestamp(new Date(request.getExpirationTime()
                .getTime() - 100000));
        response.setData("data1");
        response.setRequestDenied(true);
        
        testHandler = new AnalysisResultMessageHandler();
        testHandler.setDataExchangeService(dataExchangeService);
    }

    
    /**
     * Accuracy test for <code>checkConfiguration</code> method
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testHandler.checkConfiguration();
    }
    
    /**
     * Failure test for <code>checkConfiguration</code> method
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected= ConfigurationException.class)
    public void testCheckConfigurationFail() throws Exception {
        testHandler.setDataExchangeService(null);
        testHandler.checkConfiguration();
    }
    
    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_handleMessage() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        User user1 = entityManager.find(User.class, "2");
        dataExchangeService.addDataRequest(request);
        dataExchangeService.addDataResponse(response);
        entityManager.flush();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            String messageContent="messageContent";
            TextMessage message = session.createTextMessage(messageContent);
            Document doc =  getAnalysisResultDocument(request);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            entityManager.clear();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            entityManager.clear();
            assertEquals("'handleMessage' should be correct.",request.getId(),
                    (String)templateContext.get("requestId"));
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Failure test for the method <code>handleMessage</code>.<br>
     * when data request not exist
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected = EntityNotFoundException.class)
    public void test_handleMessageFail() throws Exception {
        clearDB();
        loadData();
        User user1 = entityManager.find(User.class, "2");
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            String messageContent="messageContent";
            TextMessage message = session.createTextMessage(messageContent);
            Document doc =  getAnalysisResultDocument(request);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            entityManager.clear();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
        } finally {
            connection.close();
        }
    }
}