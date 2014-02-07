/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.TypedQuery;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Document;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.CacheService;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link DataResponseMessageHandler} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class DataResponseMessageHandlerUnitTests extends BaseTestCase {
    /**
     * <p>
     * Represents the <code>DataResponseMessageHandler</code> instance used in
     * tests.
     * </p>
     */
    @Autowired
    private DataResponseMessageHandler instance;

    /**
     * Represents the JmsTemplate used to send JMS messages. 
     */
    @Autowired
    private JmsTemplate jmsTemplate;
    
    /**
     * Represents the cache service.
     */
    @Autowired
    private CacheService cacheService;
    
    /**
     * Represents data response queue prefix
     */
    @Value("#{configProperties['dataResponseQueuePrefix']}")
    private String dataResponseQueuePrefix;

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
     * Represents the <code>DataResponseMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    private DataResponseMessageHandler testHandler;
    

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
        request.setRequesterId("1");
        request.setQuery("query1");
        request.setRequestedPartners(new ArrayList<String>());
        request.getRequestedPartners().add("2");
        request.setExpirationTime(new Date());
        request.setCacheSafe(true);

        response = new DataResponse();
        response.setRequestId(request.getId());
        response.setRespondentId("2");
        response.setResponseTimestamp(new Date(request.getExpirationTime()
                .getTime() - 100000));
        response.setData("data1");
        response.setRequestDenied(true);
        
        testHandler = new DataResponseMessageHandler();
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
     * The result should be correct when cache safe true.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_handleMessage1() throws Exception {
        clearDB();
        loadData();
        User user1 = entityManager.find(User.class, "2");
        dataExchangeService.addDataRequest(request);
        entityManager.flush();
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            message.setJMSTimestamp(response.getResponseTimestamp().getTime());
            Document doc = getDataResponseDocument(response,true, null);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            entityManager.clear();
            TypedQuery<DataResponse>query=entityManager.
                    createQuery("select r from DataResponse r where r.requestId=:requestId "
                            +"and r.respondentId=:respondentId", DataResponse.class)
                    .setParameter("requestId", request.getId())
                    .setParameter("respondentId", user1.getId());
            List<DataResponse> responses = query.getResultList();
                    
            assertEquals("'handleMessage' should be correct.", 0,responses.size());
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            entityManager.clear();
            Destination destination =  new AMQAnyDestination("ADDR:"
                    + dataResponseQueuePrefix + request.getRequesterId()
                    + "; {create: never, delete: never}");
            Message receivedMessage = jmsTemplate.receive(destination);
            assertTrue("'handleMessage' should be correct.",receivedMessage instanceof TextMessage);
            assertNotNull("'handleMessage' should be correct.",((TextMessage)receivedMessage).getText());
            entityManager.flush();
            entityManager.clear();
            responses = query.getResultList();
            assertEquals("'handleMessage' should be correct.", 1,responses.size());
            assertEquals("'handleMessage' should be correct.", request.getId(),
                    (String) templateContext.get("requestId"));
            DataResponse retrievedResponse = responses.get(0);
            assertNotNull("'handleMessage' should be correct.",
                    retrievedResponse);
            assertEquals("'handleMessage' should be correct.",
                    response.getRequestId(), retrievedResponse.getRequestId());
            assertEquals("'handleMessage' should be correct.",
                    response.getRespondentId(),
                    retrievedResponse.getRespondentId());
            assertNotNull("'handleMessage' should be correct.",
                    retrievedResponse.getResponseTimestamp());
            assertNull("'handleMessage' should be correct.", retrievedResponse.getData());
            assertEquals("'handleMessage' should be correct.",
                    response.isRequestDenied(),
                    retrievedResponse.isRequestDenied());
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when use cache false.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_handleMessage2() throws Exception {
        clearDB();
        loadData();
        cacheService.clear();
        String cacheKey = response.getRespondentId()+":"+request.getQuery();
        User user1 = entityManager.find(User.class, "2");
        dataExchangeService.addDataRequest(request);
        entityManager.flush();
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        String dataValue="data value";
        assertTrue(request.isCacheSafe());
        response.setRequestDenied(false);
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            message.setJMSTimestamp(response.getResponseTimestamp().getTime());
            Document doc = getDataResponseDocument(response,false,dataValue);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            entityManager.clear();
            TypedQuery<DataResponse>query=entityManager.
                    createQuery("select r from DataResponse r where r.requestId=:requestId "
                            +"and r.respondentId=:respondentId", DataResponse.class)
                    .setParameter("requestId", request.getId())
                    .setParameter("respondentId", user1.getId());
            List<DataResponse> responses = query.getResultList();
                    
            assertEquals("'handleMessage' should be correct.", 0,responses.size());
            assertNull("'handleMessage' should be correct.",cacheService.get(cacheKey));
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            entityManager.clear();
            Destination destination =  new AMQAnyDestination("ADDR:"
                    + dataResponseQueuePrefix + request.getRequesterId()
                    + "; {create: never, delete: never}");
            Message receivedMessage = jmsTemplate.receive(destination);
            assertTrue("'handleMessage' should be correct.",receivedMessage instanceof TextMessage);
            assertNotNull("'handleMessage' should be correct.",((TextMessage)receivedMessage).getText());
            entityManager.flush();
            entityManager.clear();
            responses = query.getResultList();
            assertEquals("'handleMessage' should be correct.", 1,responses.size());
            assertEquals("'handleMessage' should be correct.", request.getId(),
                    (String) templateContext.get("requestId"));
            DataResponse retrievedResponse = responses.get(0);
            assertNotNull("'handleMessage' should be correct.",
                    retrievedResponse);
            assertEquals("'handleMessage' should be correct.",
                    response.getRequestId(), retrievedResponse.getRequestId());
            assertEquals("'handleMessage' should be correct.",
                    response.getRespondentId(),
                    retrievedResponse.getRespondentId());
            assertNotNull("'handleMessage' should be correct.",
                    retrievedResponse.getResponseTimestamp());
            assertEquals("'handleMessage' should be correct.",
                    dataValue, (String)cacheService.get(cacheKey).getCachedObject());
            assertNull("'handleMessage' should be correct.",retrievedResponse.getData());
            assertEquals("'handleMessage' should be correct.",
                    response.isRequestDenied(),
                    retrievedResponse.isRequestDenied());
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Failure test for the method <code>handleMessage</code>.<br>
     * when match request not exist.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected= EntityNotFoundException.class)
    public void test_handleMessageFail() throws Exception {
        clearDB();
        loadData();
        cacheService.clear();
        User user1 = entityManager.find(User.class, "2");
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            message.setJMSTimestamp(response.getResponseTimestamp().getTime());
            Document doc = getDataResponseDocument(response,false,null);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
        } finally {
            connection.close();
        }
    }
}