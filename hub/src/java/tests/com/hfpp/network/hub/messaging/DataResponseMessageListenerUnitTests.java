/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.TypedQuery;

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
import com.hfpp.network.hub.services.DataRequestExpiredException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link DataResponseMessageListener} class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class DataResponseMessageListenerUnitTests extends BaseTestCase {
    /**
     * <p>
     * Represents the <code>DataResponseMessageListener</code> instance used in
     * tests.
     * </p>
     */
    @Autowired
    private DataResponseMessageListener instance;

    /**
     * Represents the JmsTemplate used to send JMS messages.
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Represents the dataResponseQueue.
     */
    @Resource
    private Destination dataResponseQueue;

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
     * Represents the operation name when handle data response message.
     */
    @Value("#{configProperties['dataResponseMessageListener.operationName']}")
    private String operation;

    /**
     * Represents the default error code.
     */
    @Value("#{configProperties['dataResponseMessageListener.defaultErrorCode']}")
    private String defaultErrorCode;

    /**
     * Represents the default error message.
     */
    @Value("#{configProperties['dataResponseMessageListener.defaultErrorMessage']}")
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
     * Represents response used in tests.
     * </p>
     */
    private DataResponse response;

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
        Date now = new Date();
        request.setExpirationTime(new Date(now
                .getTime() + 100000));
        request.setCacheSafe(true);
        
        response = new DataResponse();
        response.setRequestId(request.getId());
        response.setRespondentId("2");
        response.setResponseTimestamp(new Date(now
                .getTime() - 100000));
        response.setData("data1");
        response.setRequestDenied(true);
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
        User user1 = entityManager.find(User.class, "2");
        entityManager.persist(request);
        entityManager.flush();
        TypedQuery<DataResponse> query = entityManager
                .createQuery("select d from DataResponse d where d.requestId=:requestId "
                        + "and d.respondentId=:respondentId", DataResponse.class)
                .setParameter("requestId", response.getRequestId())
                .setParameter("respondentId", response.getRespondentId());
        assertEquals("'onMessage' should be correct.", 0, query.getResultList().size());
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataResponseDocument(response, false, null));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
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
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Accepted", stausNode.getFirstChild().getNodeValue());
        entityManager.flush();
        entityManager.clear();
        DataResponse retrievedResponse = query.getSingleResult();
        assertEquals("'addDataResponse' should be correct.",
                response.getRequestId(), retrievedResponse.getRequestId());
        assertEquals("'addDataResponse' should be correct.",
                response.getRespondentId(),
                retrievedResponse.getRespondentId());
        assertNotNull("'addDataResponse' should be correct.",
                retrievedResponse.getResponseTimestamp());
        assertEquals("'addDataResponse' should be correct.",
                null, retrievedResponse.getData());
        assertEquals("'addDataResponse' should be correct.",
                response.isRequestDenied(),
                retrievedResponse.isRequestDenied());
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
        entityManager.persist(request);
        entityManager.flush();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
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
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
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
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
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
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
    }
    
   
    
    /**
     * <p>
     * Failure test for the method <code>onMessage</code>.<br>
     * when not valid user found in header
     * </p>
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail2() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        entityManager.persist(request);
        entityManager.flush();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataResponseDocument(response, false, null));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setStringProperty(usernameHeader, "wrong user");
                message.setStringProperty(passwordHeader, TESTUSERPASSWORD);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
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
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
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
        entityManager.persist(request);
        entityManager.flush();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataResponseDocument(response, false, null));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
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
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
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
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
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
     * when data request not found  
     * </p>
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessageFail4() throws Exception {
        entityManager.joinTransaction();
        clearDB();
        loadData();
        User user = entityManager.find(User.class, "2");
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataResponseDocument(response, false, null));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
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
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals(response.getRequestId(), requestIDNode.getFirstChild()
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
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
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
     * when data request expired  
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -2);
        request.setExpirationTime(cal.getTime());
        entityManager.persist(request);
        entityManager.flush();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataResponseDocument(response, false, null));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
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
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        assertEquals(response.getRequestId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node stausNode = doc.getElementsByTagName("Status").item(0);
        assertEquals("Not Accepted", stausNode.getFirstChild().getNodeValue());
        Node errorCodeNode = doc.getElementsByTagName("ErrorCode").item(0);
        assertEquals(exceptionErrorCodes.get(DataRequestExpiredException.class.getName()), 
                errorCodeNode.getFirstChild().getNodeValue());
        Node errorMessageNode = doc.getElementsByTagName("ErrorMessage").item(0);
        assertEquals(exceptionErrorMessages.get(DataRequestExpiredException.class.getName()), 
                errorMessageNode.getFirstChild().getNodeValue());
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
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
     * with default error and message  
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
        entityManager.persist(request);
        entityManager.flush();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        String queueName = UUID.randomUUID().toString();
        final Destination replyToQueue = new AMQAnyDestination(String.format(
                "ADDR:{0}; {create: always}", queueName));
        final String xml = getDocumentString(getDataResponseDocument(response, false, null));
        entityManager.flush();
        entityManager.clear();
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
        jmsTemplate.send(dataResponseQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(xml);
                message.setJMSReplyTo(replyToQueue);
                return message;
            }
        });
        Message responseMessage = jmsTemplate.receive(replyToQueue);
        String responseXML = ((TextMessage) responseMessage).getText();
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(responseXML.getBytes("utf-8")));
        assertEquals(1, doc.getElementsByTagName("DataResponseAcknowledgement")
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
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from DataResponse a",
                                Number.class).getSingleResult().intValue());
        assertEquals(
                0,
                entityManager
                        .createQuery("select count(a) from AuditRecord a",
                                Number.class).getSingleResult().intValue());
    }

}