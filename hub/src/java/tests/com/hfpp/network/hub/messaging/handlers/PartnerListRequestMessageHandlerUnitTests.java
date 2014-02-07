/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import java.util.List;

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
import org.w3c.dom.Element;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.messaging.dto.UserDTO;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.Role;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link PartnerListRequestMessageHandler} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class PartnerListRequestMessageHandlerUnitTests extends BaseTestCase {
    /**
     * <p>
     * Represents the <code>PartnerListRequestMessageHandler</code> instance
     * used in tests.
     * </p>
     */
    @Autowired
    private PartnerListRequestMessageHandler instance;

    /**
     * Represents the JmsTemplate used to send JMS messages. 
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * <p>
     * Represents the <code>PartnerListRequestMessageHandler</code> instance
     * used in tests.
     * </p>
     */
    private PartnerListRequestMessageHandler testHandler;

    /**
     * Prepare user and user service to test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testHandler = new PartnerListRequestMessageHandler();
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
    @Test(expected = ConfigurationException.class)
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
    @SuppressWarnings("unchecked")
    @Test
    public void test_handleMessage1() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();

        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getPartnerListRequestDocument(user1);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("users"));
            List<UserDTO> users = (List<UserDTO>) templateContext.get("users");
            assertEquals("'handleMessage' should be correct.", 1, users.size());
            UserDTO userDTO = users.get(0);
            assertEquals("'handleMessage' should be correct.", user1.getId(),
                    userDTO.getId());
            assertEquals("'handleMessage' should be correct.",
                    user1.getOrganizationName(), userDTO.getOrganizationName());
            assertTrue("'handleMessage' should be correct.",
                    userDTO.isEligibleToInitiateDataRequest());
            assertTrue("'handleMessage' should be correct.",
                    userDTO.isEligibleToReceiveDataRequest());
        } finally {
            connection.close();
        }
    }

    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when all child xml elements not exist.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_handleMessage2() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();

        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getDocument(null, null, null);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("users"));
            List<UserDTO> users = (List<UserDTO>) templateContext.get("users");
            assertEquals("'handleMessage' should be correct.", 4, users.size());
            for (UserDTO dto : users) {
                if (dto.getId() == user1.getId()) {
                    assertEquals("'handleMessage' should be correct.",
                            user1.getOrganizationName(),
                            dto.getOrganizationName());
                    assertTrue("'handleMessage' should be correct.",
                            dto.isEligibleToInitiateDataRequest());
                    assertTrue("'handleMessage' should be correct.",
                            dto.isEligibleToReceiveDataRequest());
                } else {
                    assertEquals("'handleMessage' should be correct.", "org"
                            + dto.getId(), dto.getOrganizationName());
                    assertTrue("'handleMessage' should be correct.",
                            dto.isEligibleToInitiateDataRequest());
                    assertTrue("'handleMessage' should be correct.",
                            dto.isEligibleToReceiveDataRequest());
                }
            }
        } finally {
            connection.close();
        }
    }

    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when only exist organizationName.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_handleMessage3() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        String id = "2";
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getDocument("org" + id, null, null);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("users"));
            List<UserDTO> users = (List<UserDTO>) templateContext.get("users");
            assertEquals("'handleMessage' should be correct.", 1, users.size());
            UserDTO userDTO = users.get(0);
            assertEquals("'handleMessage' should be correct.", id,
                    userDTO.getId());
            assertEquals("'handleMessage' should be correct.", "org" + id,
                    userDTO.getOrganizationName());
            assertTrue("'handleMessage' should be correct.",
                    userDTO.isEligibleToInitiateDataRequest());
            assertTrue("'handleMessage' should be correct.",
                    userDTO.isEligibleToReceiveDataRequest());
        } finally {
            connection.close();
        }
    }

    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when only exist
     * EligibleToReceiveDataRequest.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_handleMessage4() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        entityManager.persist(user1.getRole());
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getDocument(null, "false", null);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("users"));
            List<UserDTO> users = (List<UserDTO>) templateContext.get("users");
            assertEquals("'handleMessage' should be correct.", 1, users.size());
            UserDTO userDTO = users.get(0);
            assertEquals("'handleMessage' should be correct.", user1.getId(),
                    userDTO.getId());
            assertEquals("'handleMessage' should be correct.",
                    user1.getOrganizationName(), userDTO.getOrganizationName());
            assertFalse("'handleMessage' should be correct.",
                    userDTO.isEligibleToInitiateDataRequest());
            assertFalse("'handleMessage' should be correct.",
                    userDTO.isEligibleToReceiveDataRequest());
        } finally {
            connection.close();
        }
    }

    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * The result should be correct when only exist
     * EligibleToInitiateDataRequest.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_handleMessage5() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        entityManager.persist(user1.getRole());
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getDocument(null, null, "false");
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("users"));
            List<UserDTO> users = (List<UserDTO>) templateContext.get("users");
            assertEquals("'handleMessage' should be correct.", 1, users.size());
            UserDTO userDTO = users.get(0);
            assertEquals("'handleMessage' should be correct.", user1.getId(),
                    userDTO.getId());
            assertEquals("'handleMessage' should be correct.",
                    user1.getOrganizationName(), userDTO.getOrganizationName());
            assertFalse("'handleMessage' should be correct.",
                    userDTO.isEligibleToInitiateDataRequest());
            assertFalse("'handleMessage' should be correct.",
                    userDTO.isEligibleToReceiveDataRequest());
        } finally {
            connection.close();
        }
    }

    /**
     * <p>
     * Accuracy test for the method <code>handleMessage</code>.<br>
     * when EligibleToReceiveDataRequest not valid boolean string value will
     * parse as false.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void test_handleMessage6() throws Exception {
        clearDB();
        loadData();
        User user1 = getTestUser();
        entityManager.persist(user1.getRole());
        user1 = userService.create(user1);
        entityManager.flush();
        Connection connection = jmsTemplate.getConnectionFactory()
                .createConnection();
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage("");
            Document doc = getDocument(null, "error", null);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            VelocityContext templateContext = new VelocityContext();
            instance.handleMessage(message, user1, doc, xpath, templateContext);
            assertNotNull("'handleMessage' should be correct.",
                    templateContext.get("users"));
            List<UserDTO> users = (List<UserDTO>) templateContext.get("users");
            assertEquals("'handleMessage' should be correct.", 1, users.size());
            UserDTO userDTO = users.get(0);
            assertEquals("'handleMessage' should be correct.", user1.getId(),
                    userDTO.getId());
            assertEquals("'handleMessage' should be correct.",
                    user1.getOrganizationName(), userDTO.getOrganizationName());
            assertFalse("'handleMessage' should be correct.",
                    userDTO.isEligibleToInitiateDataRequest());
            assertFalse("'handleMessage' should be correct.",
                    userDTO.isEligibleToReceiveDataRequest());
        } finally {
            connection.close();
        }
    }

    /**
     * Create partner list request message
     * 
     * @param organizationName
     *            the organization name
     * @param eligibleToReceiveDataRequestValue
     *            the eligibleToReceiveDataRequest value
     * @param eligibleToInitiateDataRequestValue
     *            the eligibleToInitiateDataRequest value
     * @return match xml document
     * @throws Exception
     *             throw if any error happen
     */
    private Document getDocument(String organizationName,
            String eligibleToReceiveDataRequestValue,
            String eligibleToInitiateDataRequestValue) throws Exception {
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder()
                .newDocument();
        Element root = doc.createElement("PartnerListRequest");
        doc.appendChild(root);
        if (!Helper.isNullOrEmpty(organizationName)) {
            Element orgName = doc.createElement("OrganizationName");
            orgName.setTextContent(organizationName);
            root.appendChild(orgName);
        }
        if (!Helper.isNullOrEmpty(eligibleToReceiveDataRequestValue)) {
            Element eligibleToReceiveDataRequest = doc
                    .createElement("EligibleToReceiveDataRequest");
            eligibleToReceiveDataRequest
                    .setTextContent(eligibleToReceiveDataRequestValue);
            root.appendChild(eligibleToReceiveDataRequest);
        }
        if (!Helper.isNullOrEmpty(eligibleToInitiateDataRequestValue)) {
            Element eligibleToReceiveDataRequest = doc
                    .createElement("EligibleToInitiateDataRequest");
            eligibleToReceiveDataRequest
                    .setTextContent(eligibleToInitiateDataRequestValue);
            root.appendChild(eligibleToReceiveDataRequest);
        }
        return doc;
    }
}