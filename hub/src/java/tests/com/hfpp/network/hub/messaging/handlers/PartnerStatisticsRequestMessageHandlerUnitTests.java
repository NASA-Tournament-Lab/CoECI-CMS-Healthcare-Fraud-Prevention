/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Document;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.messaging.dto.UserDTO;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link PartnerStatisticsRequestMessageHandler} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class PartnerStatisticsRequestMessageHandlerUnitTests extends
        BaseTestCase {
    /**
     * <p>
     * Represents the <code>PartnerStatisticsRequestMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    @Autowired
    private PartnerStatisticsRequestMessageHandler instance;

    /**
     * Represents the JmsTemplate used to send JMS messages. 
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * <p>
     * Represents initial responded requests value used in tests.
     * </p>
     */
    @Value("#{configProperties['initialRespondedRequestsValue']}")
    private int initialRespondedRequestsValue;
    
    /**
     * <p>
     * Represents the <code>PartnerStatisticsRequestMessageHandler</code>
     * instance used in tests.
     * </p>
     */
    private PartnerStatisticsRequestMessageHandler testHandler;
    
    /**
     * Prepare user and user service to test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testHandler = new PartnerStatisticsRequestMessageHandler();
        testHandler.setUserService(userService);
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
        testHandler.setUserService(null);
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
        User user1 = getTestUser();
        entityManager.persist(user1.getRole());
        user1 = userService.create(user1);
        entityManager.flush();
       
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getPartnerStatisticsRequestDocument(user1);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            entityManager.flush();
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("user"));
            UserDTO userDTO = (UserDTO) templateContext.get("user");
            assertEquals("'handleMessage' should be correct.", user1.getId(),
                    userDTO.getId());
            assertEquals("'handleMessage' should be correct.", 0,
                    userDTO.getDataRequestsInitiated());
            assertEquals("'handleMessage' should be correct.",
                    initialRespondedRequestsValue,
                    userDTO.getDataRequestsResponded());
            assertEquals("'handleMessage' should be correct.", 0,
                    userDTO.getDataRequestsInitiated());
            assertNull("'handleMessage' should be correct.",
                    userDTO.getOrganizationName());
        } finally {
            connection.close();
        }
    }
    
    /**
     * <p>
     * Failure test for the method <code>handleMessage</code>.<br>
     * when document contains invalid value
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected= IllegalArgumentException.class)
    public void test_handleMessageFail1() throws Exception {
        User user1 = getTestUser();
        entityManager.persist(user1.getRole());
        user1 = userService.create(user1);
        entityManager.flush();
       
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().newDocument();
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
     * when not exist user.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected= EntityNotFoundException.class)
    public void test_handleMessageFail2() throws Exception {
        User user1 = getTestUser();
        Connection connection =  jmsTemplate.getConnectionFactory().createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getPartnerStatisticsRequestDocument(user1);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
        } finally {
            connection.close();
        }
    }
}