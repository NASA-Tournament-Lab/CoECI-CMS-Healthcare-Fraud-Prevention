/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.client.AMQAnyDestination;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.services.AuthenticationException;
import com.hfpp.network.hub.services.AuthorizationException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.InsufficientParticipationRatioException;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.Role;
import com.hfpp.network.models.User;


/**
 * <p>
 * Unit tests for {@link DataRequestMessageListener} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class DataRequestMessageListenerUnitTests extends BaseTestCase {
    /**
     * <p>
     * Represents the {@link DataRequestMessageListener} instance used in
     * tests.
     * </p>
     */
    @Autowired
    private DataRequestMessageListener instance;

    /**
     * Represents the JmsTemplate used to send JMS messages.
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Represents the dataRequestQueue.
     */
    @Resource
    private Destination dataRequestQueue;
    
    /**
     * Represents the header name for the user name.
     */
    @Value("#{configProperties['usernameHeader']}")
    private String usernameHeader;

    /**
     * Represents the header name for the password.
     */
    @Value("#{configProperties['passwordHeader']}")
    private String passwordHeader;

    /**
     * Represents the operation name when handle data request message.
     */
    @Value("#{configProperties['dataRequestMessageListener.operationName']}")
    private String operation;
    
    /**
     * Represents the default error code.
     */
    @Value("#{configProperties['dataRequestMessageListener.defaultErrorCode']}")
    private String defaultErrorCode;

    /**
     * Represents the default error message.
     */
    @Value("#{configProperties['dataRequestMessageListener.defaultErrorMessage']}")
    private String defaultErrorMessage;
    
    /**
     * Represents the mapping from exception class to corresponding error code
     * that is written to the XML <&lt;ErrorCode&gt; element.
     */
    @Resource
    private Map<Class<? extends Exception>, String> exceptionErrorCodes;

    /**
     * Represents the mapping from exception class to corresponding error
     * message that is written to the XML &lt;ErrorMessage&gt; element.
     */
    @Resource
    private Map<Class<? extends Exception>, String> exceptionErrorMessages;

    /**
     * <p>
     * Represents request used in tests.
     * </p>
     */
    private DataRequest request;

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
        instance.setAuthorizationService(authorizationService);
    }
    
    /**
     * <p>
     * Accuracy test for the method <code>onMessage</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessage() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        final String username = UUID.randomUUID().toString();
        final String password = "pass2";
        User user1 = getTestUser();
        user1.setUsername(username);
        user1.setRole(entityManager.find(Role.class, "1"));
        user1 = userService.create(user1);
        entityManager.flush();
        userService.setPassword(user1.getId(), password);
        entityManager.flush();
        String id = request.getId();
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataRequestDocument(request));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, username);
                message.setStringProperty(passwordHeader, password);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Accepted", stausNode.getFirstChild().getNodeValue());
        entityManager.flush();
        entityManager.clear();
        retrievedRequest = entityManager
                .createQuery("select r from DataRequest r where r.id=:id",
                        DataRequest.class).setParameter("id", id)
                .getSingleResult();
        assertNotNull("'handleMessage' should be correct.", retrievedRequest);
        assertEquals("'handleMessage' should be correct.",
                request.getOriginalRequesterId(),
                retrievedRequest.getOriginalRequesterId());
        assertEquals("'handleMessage' should be correct.", user1.getId(),
                retrievedRequest.getRequesterId());
        assertEquals("'handleMessage' should be correct.", request.getQuery(),
                retrievedRequest.getQuery());
        assertEquals("'handleMessage' should be correct.", request
                .getRequestedPartners().size(), retrievedRequest
                .getRequestedPartners().size());
        assertEquals("'handleMessage' should be correct.",
                request.isCacheSafe(), retrievedRequest.isCacheSafe());
        assertNotNull("'handleMessage' should be correct.",
                retrievedRequest.getExpirationTime());
        assertEquals(
                1,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        checkAuditRecord(user1, operation, false, xml);
    }
    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * when xml not valid.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail1() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        String id = request.getId();
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = "error xml";
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, TESTUSERNAME);
                message.setStringProperty(passwordHeader, TESTUSERPASSWORD);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals("$requestId", requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(exceptionErrorCodes.get(SAXParseException.class.getName()), 
                errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(exceptionErrorMessages.get(SAXParseException.class.getName()), 
                errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertNull("'handleMessage' should be correct.",  entityManager.find(DataRequest.class,id));
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
    }

    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * when not get valid user from message header
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail2() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        String id = request.getId();
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataRequestDocument(request));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, "error");
                message.setStringProperty(passwordHeader, "error");
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals("$requestId", requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(exceptionErrorCodes.get(AuthenticationException.class.getName()), 
                errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(exceptionErrorMessages.get(AuthenticationException.class.getName()), 
                errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertNull("'handleMessage' should be correct.",  entityManager.find(DataRequest.class,id));
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
    }
    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * when user not authorization to perform operation
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail3() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        entityManager.flush();
        final String username = user.getUsername();
        final String password = "pass";
        userService.setPassword(user.getId(), password);
        entityManager.flush();
        String id = request.getId();
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataRequestDocument(request));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, username);
                message.setStringProperty(passwordHeader, password);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals("$requestId", requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(exceptionErrorCodes.get(AuthorizationException.class.getName()),
                errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(exceptionErrorMessages.get(AuthorizationException.class.getName()),
                errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertNull("'handleMessage' should be correct.",  entityManager.find(DataRequest.class,id));
        assertEquals(
                1,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        checkAuditRecord(user,operation,  true, xml);
    }
    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * with error happen when handle message with InsufficientParticipationRatioException
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail4() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        User user = entityManager.find(User.class, "2");
        entityManager.createQuery("update UserStatistics u set u.dataRequestsInitiated=1")
        .executeUpdate();
        String id = request.getId();
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataRequestDocument(request));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, TESTUSERNAME);
                message.setStringProperty(passwordHeader, TESTUSERPASSWORD);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(exceptionErrorCodes.get(InsufficientParticipationRatioException.class.getName()),
                errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(exceptionErrorMessages.get(InsufficientParticipationRatioException.class.getName()), 
                errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertNull("'handleMessage' should be correct.",  entityManager.find(DataRequest.class,id));
        assertEquals(
                1,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        checkAuditRecord(user, operation, true, xml);
    }
    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * with error happen when handle message with EntityNotFoundException
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail5() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        User user = entityManager.find(User.class, "2");
        String id = request.getId();
        request.setRequestedPartners(Arrays.asList("not exist Id"));
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataRequestDocument(request));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, TESTUSERNAME);
                message.setStringProperty(passwordHeader, TESTUSERPASSWORD);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(exceptionErrorCodes.get(EntityNotFoundException.class.getName()), 
                errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(exceptionErrorMessages.get(EntityNotFoundException.class.getName()), 
                errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertNull("'handleMessage' should be correct.",  entityManager.find(DataRequest.class,id));
        assertEquals(
                1,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        checkAuditRecord(user, operation, true, xml);
    }
    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * with default error code and message
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail6() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        String id = request.getId();
        request.setRequestedPartners(Arrays.asList("not exist Id"));
        DataRequest retrievedRequest = entityManager
                .find(DataRequest.class, id);
        assertNull("'onMessage' should be correct.", retrievedRequest);
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataRequestDocument(request));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataRequestQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                //not exist user name and password in header
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataRequestAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals("$requestId", requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(defaultErrorCode, errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(defaultErrorMessage, errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertNull("'handleMessage' should be correct.",  entityManager.find(DataRequest.class,id));
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
    }
}