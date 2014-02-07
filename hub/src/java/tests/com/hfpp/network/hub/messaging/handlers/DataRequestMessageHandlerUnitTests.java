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
import com.hfpp.network.models.Role;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link DataRequestMessageHandler} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class DataRequestMessageHandlerUnitTests extends
        BaseTestCase {
    /**
     * <p>
     * Represents the <code>DataRequestMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    @Autowired
    private DataRequestMessageHandler instance;
    
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
     * Represents the <code>DataRequestMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    private DataRequestMessageHandler testHandler;
    
    
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
        request.setRequestedPartners(Arrays.asList("2"));
        request.setExpirationTime(new Date());
        request.setCacheSafe(true);
        
        testHandler = new DataRequestMessageHandler();
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
     * The result should be correct when OriginalRequesterID element not exist.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_handleMessage1() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc =  getDataRequestDocument(request);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();

            String id = request.getId();
            entityManager.clear();
            DataRequest retrievedRequest = entityManager.find(DataRequest.class, id);
            assertNull("'handleMessage' should be correct.",
                    retrievedRequest);
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            entityManager.clear();
            assertEquals("'handleMessage' should be correct.",request.getId(),
                    (String)templateContext.get("requestId"));
            retrievedRequest = entityManager.find(DataRequest.class, id);
            assertNotNull("'handleMessage' should be correct.",
                    retrievedRequest);
            assertEquals("'handleMessage' should be correct.",
                request.getOriginalRequesterId(), retrievedRequest.getOriginalRequesterId());
            assertEquals("'handleMessage' should be correct.",
                user1.getId(), retrievedRequest.getRequesterId());
            assertEquals("'handleMessage' should be correct.",
                request.getQuery(), retrievedRequest.getQuery());
            assertEquals("'handleMessage' should be correct.",
                request.getRequestedPartners().size(), retrievedRequest.getRequestedPartners().size());
            assertEquals("'handleMessage' should be correct.",
                request.isCacheSafe(), retrievedRequest.isCacheSafe());
            assertNotNull("'handleMessage' should be correct.",
                retrievedRequest.getExpirationTime());
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when OriginalRequesterID element exist.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_handleMessage2() throws Exception {
        clearDB();
        loadData();
        User user = getTestUser();
        user.setRole(entityManager.find(Role.class, "1"));
        user = userService.create(user);
        entityManager.flush();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            request.setOriginalRequesterId(user.getId());
            Document doc =  getDataRequestDocument(request);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            String id = request.getId();
            entityManager.clear();
            DataRequest retrievedRequest = entityManager.find(DataRequest.class, id);
            assertNull("'handleMessage' should be correct.",
                    retrievedRequest);
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            entityManager.clear();
            assertEquals("'handleMessage' should be correct.",request.getId(),
                    (String)templateContext.get("requestId"));
            retrievedRequest = entityManager.find(DataRequest.class, id);
            assertNotNull("'handleMessage' should be correct.",
                    retrievedRequest);
            assertEquals("'handleMessage' should be correct.",
                request.getOriginalRequesterId(), retrievedRequest.getOriginalRequesterId());
            assertEquals("'handleMessage' should be correct.",
                user1.getId(), retrievedRequest.getRequesterId());
            assertEquals("'handleMessage' should be correct.",
                request.getQuery(), retrievedRequest.getQuery());
            assertEquals("'handleMessage' should be correct.",
                request.getRequestedPartners().size(), retrievedRequest.getRequestedPartners().size());
            assertEquals("'handleMessage' should be correct.",
                request.isCacheSafe(), retrievedRequest.isCacheSafe());
            assertNotNull("'handleMessage' should be correct.",
                retrievedRequest.getExpirationTime());
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when CacheSafe element exist.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_handleMessage3() throws Exception {
        clearDB();
        loadData();
        User user = getTestUser();
        user.setRole(entityManager.find(Role.class, "1"));
        user = userService.create(user);
        entityManager.flush();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc =  getDataRequestDocument(request);
            //remove cache safe element
            doc.getDocumentElement().removeChild(doc.getElementsByTagName("CacheSafe").item(0));
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            String id = request.getId();
            entityManager.clear();
            DataRequest retrievedRequest = entityManager.find(DataRequest.class, id);
            assertNull("'handleMessage' should be correct.",
                    retrievedRequest);
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            entityManager.clear();
            assertEquals("'handleMessage' should be correct.",request.getId(),
                    (String)templateContext.get("requestId"));
            retrievedRequest = entityManager.find(DataRequest.class, id);
            assertNotNull("'handleMessage' should be correct.",
                    retrievedRequest);
            assertEquals("'handleMessage' should be correct.",
                request.getOriginalRequesterId(), retrievedRequest.getOriginalRequesterId());
            assertEquals("'handleMessage' should be correct.",
                user1.getId(), retrievedRequest.getRequesterId());
            assertEquals("'handleMessage' should be correct.",
                request.getQuery(), retrievedRequest.getQuery());
            assertEquals("'handleMessage' should be correct.",
                request.getRequestedPartners().size(), retrievedRequest.getRequestedPartners().size());
            //isCacheSafe become false 
            assertTrue("'handleMessage' should be correct.",
                request.isCacheSafe());
            assertFalse("'handleMessage' should be correct.", retrievedRequest.isCacheSafe());
            assertNotNull("'handleMessage' should be correct.",
                retrievedRequest.getExpirationTime());
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Failure test for the method <code>handleMessage</code>.<br>
     * when request partner id not exist
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected= EntityNotFoundException.class)
    public void test_handleMessageFail1() throws Exception {
        clearDB();
        loadData();
        User user = getTestUser();
        user.setRole(entityManager.find(Role.class, "1"));
        user = userService.create(user);
        entityManager.flush();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            request.setRequestedPartners(Arrays.asList("not exist Id"));
            Document doc =  getDataRequestDocument(request);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Failure test for the method <code>handleMessage</code>.<br>
     * when ExpirationTime not valid date time value
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected= IllegalArgumentException.class)
    public void test_handleMessageFail2() throws Exception {
        clearDB();
        loadData();
        User user = getTestUser();
        user.setRole(entityManager.find(Role.class, "1"));
        user = userService.create(user);
        entityManager.flush();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc =  getDataRequestDocument(request);
            doc.getElementsByTagName("ExpirationTime").item(0).setTextContent("invalida date");
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
        } finally {
            connection.close();
        }
    }

}