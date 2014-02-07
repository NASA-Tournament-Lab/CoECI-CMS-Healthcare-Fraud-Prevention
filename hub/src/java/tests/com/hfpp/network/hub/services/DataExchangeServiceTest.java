/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.DataExchangeServiceImpl;
import com.hfpp.network.hub.services.impl.UserServiceImpl;
import com.hfpp.network.models.CachedObjectWrapper;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserStatistics;

/**
 * Functional tests for {@link DataExchangeService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class DataExchangeServiceTest extends BaseTestCase {

    /**
     * Represents the data exchange service
     */
    @Autowired
    private DataExchangeService dataExchangeService;

    /**
     * Represents the implementation of data exchange service
     */
    @Autowired
    private DataExchangeServiceImpl dataExchangeServiceImpl;

    /**
     * Represents the cache service.
     */
    @Autowired
    private CacheService cacheService;

    /**
     * Represents the user service
     */
    @Autowired
    private UserServiceImpl userService;

    /**
     * Represents the jms template
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Represents the velocity engine.
     */
    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * Represents analysis result queue prefix
     */
    @Value("#{configProperties['analysisResultQueuePrefix']}")
    private String analysisResultQueuePrefix;

    /**
     * Represents data response queue prefix
     */
    @Value("#{configProperties['dataResponseQueuePrefix']}")
    private String dataResponseQueuePrefix;

    /**
     * Represents data request queue prefix
     */
    @Value("#{configProperties['dataRequestQueuePrefix']}")
    private String dataRequestQueuePrefix;

    /**
     * Represents data response template path
     */
    @Value("#{configProperties['dataResponseTemplatePath']}")
    private String dataResponseTemplatePath;

    /**
     * Represents data request template path
     */
    @Value("#{configProperties['dataRequestTemplatePath']}")
    private String dataRequestTemplatePath;

    /**
     * Represents the initial number-of-responded-requests value for new users.
     */
    @Value("#{configProperties['initialRespondedRequestsValue']}")
    private int initialRespondedRequestsValue;

    /**
     * Represents the implementation of data exchange service
     */
    private DataExchangeServiceImpl testDataExchangeServiceImpl;

    /**
     * Prepare audit service for test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        userService.setEntityManager(entityManager);
        testDataExchangeServiceImpl = new DataExchangeServiceImpl();
        testDataExchangeServiceImpl.setEntityManager(entityManager);
        testDataExchangeServiceImpl.setJmsTemplate(jmsTemplate);
        testDataExchangeServiceImpl.setCacheService(cacheService);
        testDataExchangeServiceImpl.setUserService(userService);
        testDataExchangeServiceImpl.setVelocityEngine(velocityEngine);
        testDataExchangeServiceImpl
                .setAnalysisResultQueuePrefix(analysisResultQueuePrefix);
        testDataExchangeServiceImpl
                .setDataResponseQueuePrefix(dataResponseQueuePrefix);
        testDataExchangeServiceImpl
                .setDataRequestQueuePrefix(dataRequestQueuePrefix);
        testDataExchangeServiceImpl
                .setDataRequestTemplatePath(dataRequestTemplatePath);
        testDataExchangeServiceImpl
                .setDataResponseTemplatePath(dataResponseTemplatePath);
    }

    /**
     * Accuracy test for DataExchangeServiceImpl#checkConfiguration
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when entity
     * manager is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail1() throws Exception {
        testDataExchangeServiceImpl.setEntityManager(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when jms
     * template is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail2() throws Exception {
        testDataExchangeServiceImpl.setJmsTemplate(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when cache
     * service is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail3() throws Exception {
        testDataExchangeServiceImpl.setCacheService(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when user
     * service is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail4() throws Exception {
        testDataExchangeServiceImpl.setUserService(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * VelocityEngine is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail5() throws Exception {
        testDataExchangeServiceImpl.setVelocityEngine(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * analysisResultQueuePrefix is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail6() throws Exception {
        testDataExchangeServiceImpl.setAnalysisResultQueuePrefix(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * analysisResultQueuePrefix is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail7() throws Exception {
        testDataExchangeServiceImpl.setAnalysisResultQueuePrefix("");
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataResponseQueuePrefix is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail8() throws Exception {
        testDataExchangeServiceImpl.setDataResponseQueuePrefix(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataResponseQueuePrefix is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail9() throws Exception {
        testDataExchangeServiceImpl.setDataResponseQueuePrefix("");
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataRequestQueuePrefix is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail10() throws Exception {
        testDataExchangeServiceImpl.setDataRequestQueuePrefix(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataRequestQueuePrefix is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail11() throws Exception {
        testDataExchangeServiceImpl.setDataRequestQueuePrefix("");
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataResponseTemplatePath is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail12() throws Exception {
        testDataExchangeServiceImpl.setDataResponseTemplatePath(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataResponseTemplatePath is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail13() throws Exception {
        testDataExchangeServiceImpl.setDataResponseTemplatePath("");
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataRequestTemplatePath is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail14() throws Exception {
        testDataExchangeServiceImpl.setDataRequestTemplatePath(null);
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for DataExchangeServiceImpl#checkConfiguration when
     * dataRequestTemplatePath is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail15() throws Exception {
        testDataExchangeServiceImpl.setDataRequestTemplatePath("");
        testDataExchangeServiceImpl.checkConfiguration();
    }

    /**
     * Accuracy test for DataExchangeService#addDataRequest when request is not
     * cache safe originalRequesterId is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataRequest1() throws Exception {
        clearDB();
        entityManager.flush();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        entityManager.clear();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = getDataRequest();
        request.setOriginalRequesterId(null);
        request.setRequesterId(user.getId());
        Assert.assertFalse(request.isCacheSafe());
        String queueName = dataRequestQueuePrefix + user.getId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        Assert.assertEquals(0, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        dataExchangeService.addDataRequest(request);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(1, ust.getDataRequestsInitiated());
        Assert.assertEquals(1, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Template template = velocityEngine.getTemplate(dataRequestTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        Calendar expirationTimeCal = Calendar.getInstance();
        expirationTimeCal.setTime(request.getExpirationTime());
        String expireTimeValue = javax.xml.bind.DatatypeConverter
                .printDateTime(expirationTimeCal);
        context.put("expirationTime", expireTimeValue);
        context.put("cacheAvailable", false);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataRequest")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node queryNode = doc.getElementsByTagName("Query").item(0);
        Assert.assertTrue(queryNode.getFirstChild().getNodeValue()
                .contains(request.getQuery()));
        Node expireNode = doc.getElementsByTagName("ExpirationTime").item(0);
        Assert.assertEquals(expireTimeValue, expireNode.getFirstChild()
                .getNodeValue());
        Node cacheNode = doc.getElementsByTagName("CacheAvailable").item(0);
        Assert.assertEquals("false", cacheNode.getFirstChild().getNodeValue());
    }

    /**
     * Accuracy test for DataExchangeService#addDataRequest when request is
     * cache safe and not exist cache
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataRequest2() throws Exception {
        clearDB();
        cacheService.clear();
        entityManager.flush();
        User user = getTestUser();
        user.setAutoRetrieveCachedData(true);
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = getDataRequest();
        request.setOriginalRequesterId(null);
        request.setRequesterId(user.getId());
        request.setCacheSafe(true);
        String cacheKey = user.getId() + ":" + request.getQuery();
        String cacheValue = "cache value";
        cacheService.put(cacheKey, cacheValue);
        String queueName = dataResponseQueuePrefix + user.getId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        Assert.assertEquals(0, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        dataExchangeService.addDataRequest(request);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(1, ust.getDataRequestsInitiated());
        Assert.assertEquals(1, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue + 1,
                ust.getDataRequestsResponded());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Template template = velocityEngine
                .getTemplate(dataResponseTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        DataResponse response = new DataResponse();
        response.setData(cacheValue);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        response.setRequestDenied(false);
        context.put("response", response);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataResponse")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node respondentNode = doc.getElementsByTagName("RespondentID").item(0);
        Assert.assertEquals(user.getId(), respondentNode.getFirstChild()
                .getNodeValue());
        Node dataNode = doc.getElementsByTagName("Data").item(0);
        Assert.assertTrue(dataNode.getTextContent().contains(cacheValue));
    }

    /**
     * Accuracy test for DataExchangeService#addDataRequest when request is
     * cache safe and exist cache
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataRequest3() throws Exception {
        clearDB();
        entityManager.flush();
        cacheService.clear();
        User user = getTestUser();
        user.setAutoRetrieveCachedData(false);
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = getDataRequest();
        request.setOriginalRequesterId(null);
        request.setRequesterId(user.getId());
        request.setCacheSafe(true);
        String cacheKey = user.getId() + ":" + request.getQuery();
        String cacheValue = "cache value";
        cacheService.put(cacheKey, cacheValue);
        String queueName = dataRequestQueuePrefix + user.getId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        Assert.assertEquals(0, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        dataExchangeService.addDataRequest(request);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(1, ust.getDataRequestsInitiated());
        Assert.assertEquals(1, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Template template = velocityEngine.getTemplate(dataRequestTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("cacheAvailable", true);
        Calendar expirationTimeCal = Calendar.getInstance();
        expirationTimeCal.setTime(request.getExpirationTime());
        String expireTimeValue = javax.xml.bind.DatatypeConverter
                .printDateTime(expirationTimeCal);
        context.put("expirationTime", expireTimeValue);
        Calendar cacheTimestampCal = Calendar.getInstance();
        CachedObjectWrapper wrapper = cacheService.get(cacheKey);
        cacheTimestampCal.setTime(wrapper.getTimestamp());
        String cacheTimestampValue = javax.xml.bind.DatatypeConverter
                .printDateTime(cacheTimestampCal);
        context.put("cacheTimestamp", cacheTimestampValue);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataRequest")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node queryNode = doc.getElementsByTagName("Query").item(0);
        Assert.assertTrue(queryNode.getFirstChild().getNodeValue()
                .contains(request.getQuery()));
        Node expireNode = doc.getElementsByTagName("ExpirationTime").item(0);
        Assert.assertEquals(expireTimeValue, expireNode.getFirstChild()
                .getNodeValue());
        Node cacheTimeNode = doc.getElementsByTagName("CacheTimestamp").item(0);
        Assert.assertEquals(cacheTimestampValue, cacheTimeNode.getFirstChild()
                .getNodeValue());
        Node cacheNode = doc.getElementsByTagName("CacheAvailable").item(0);
        Assert.assertEquals("true", cacheNode.getFirstChild().getNodeValue());
    }

    /**
     * Accuracy test for DataExchangeService#addDataRequest when request is not
     * cache safe and originalRequesterId not null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataRequest4() throws Exception {
        clearDB();
        entityManager.flush();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = getDataRequest();
        request.setOriginalRequesterId(user.getId());
        request.setRequesterId(user.getId());
        Assert.assertFalse(request.isCacheSafe());
        String queueName = dataRequestQueuePrefix + user.getId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        Assert.assertEquals(0, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        dataExchangeService.addDataRequest(request);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(1, ust.getDataRequestsInitiated());
        Assert.assertEquals(1, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Template template = velocityEngine.getTemplate(dataRequestTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        Calendar expirationTimeCal = Calendar.getInstance();
        expirationTimeCal.setTime(request.getExpirationTime());
        String expireTimeValue = javax.xml.bind.DatatypeConverter
                .printDateTime(expirationTimeCal);
        context.put("expirationTime", expireTimeValue);
        context.put("cacheAvailable", false);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataRequest")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node queryNode = doc.getElementsByTagName("Query").item(0);
        Assert.assertTrue(queryNode.getFirstChild().getNodeValue()
                .contains(request.getQuery()));
        Node expireNode = doc.getElementsByTagName("ExpirationTime").item(0);
        Assert.assertEquals(expireTimeValue, expireNode.getFirstChild()
                .getNodeValue());
        Node cacheNode = doc.getElementsByTagName("CacheAvailable").item(0);
        Assert.assertEquals("false", cacheNode.getFirstChild().getNodeValue());
    }

    /**
     * Accuracy test for DataExchangeService#addDataRequest when
     * requestedPartners is empty cache safe originalRequesterId is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataRequest5() throws Exception {
        clearDB();
        entityManager.flush();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        entityManager.clear();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = getDataRequest();
        request.setOriginalRequesterId(null);
        request.setRequesterId(user.getId());
        request.setRequestedPartners(new ArrayList<String>());
        Assert.assertFalse(request.isCacheSafe());
        String queueName = dataRequestQueuePrefix + user.getId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        Assert.assertEquals(0, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        dataExchangeService.addDataRequest(request);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(1, ust.getDataRequestsInitiated());
        Assert.assertEquals(1, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Template template = velocityEngine.getTemplate(dataRequestTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        Calendar expirationTimeCal = Calendar.getInstance();
        expirationTimeCal.setTime(request.getExpirationTime());
        String expireTimeValue = javax.xml.bind.DatatypeConverter
                .printDateTime(expirationTimeCal);
        context.put("expirationTime", expireTimeValue);
        context.put("cacheAvailable", false);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataRequest")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node queryNode = doc.getElementsByTagName("Query").item(0);
        Assert.assertTrue(queryNode.getFirstChild().getNodeValue()
                .contains(request.getQuery()));
        Node expireNode = doc.getElementsByTagName("ExpirationTime").item(0);
        Assert.assertEquals(expireTimeValue, expireNode.getFirstChild()
                .getNodeValue());
        Node cacheNode = doc.getElementsByTagName("CacheAvailable").item(0);
        Assert.assertEquals("false", cacheNode.getFirstChild().getNodeValue());
    }

    /**
     * Accuracy test for DataExchangeService#addDataRequest when
     * requestedPartners is not empty cache safe originalRequesterId is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataRequest6() throws Exception {
        clearDB();
        entityManager.flush();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        Assert.assertFalse(userService.get(user.getId())
                .isEligibleToReceiveDataRequests());
        User user2 = getTestUser();
        user2.getRole().setName("role1");
        entityManager.persist(user2.getRole());
        entityManager.flush();
        user2 = userService.create(user2);
        entityManager.flush();
        Assert.assertTrue(userService.get(user2.getId())
                .isEligibleToReceiveDataRequests());
        entityManager.flush();
        entityManager.clear();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = getDataRequest();
        request.setOriginalRequesterId(null);
        request.setRequesterId(user.getId());
        List<String> parterners = new ArrayList<String>();
        parterners.add(user.getId());
        parterners.add(user2.getId());
        request.setRequestedPartners(parterners);
        Assert.assertFalse(request.isCacheSafe());
        String queueName = dataRequestQueuePrefix + user2.getId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        Assert.assertEquals(0, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        UserStatistics ust2 = userService.getUserStatistics(user2.getId());
        Assert.assertEquals(0, ust2.getDataRequestsInitiated());
        Assert.assertEquals(0, ust2.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust2.getDataRequestsResponded());
        dataExchangeService.addDataRequest(request);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(1, ust.getDataRequestsInitiated());
        Assert.assertEquals(0, ust.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        ust2 = userService.getUserStatistics(user2.getId());
        Assert.assertEquals(0, ust2.getDataRequestsInitiated());
        Assert.assertEquals(1, ust2.getDataRequestsReceived());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust2.getDataRequestsResponded());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Template template = velocityEngine.getTemplate(dataRequestTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        Calendar expirationTimeCal = Calendar.getInstance();
        expirationTimeCal.setTime(request.getExpirationTime());
        String expireTimeValue = javax.xml.bind.DatatypeConverter
                .printDateTime(expirationTimeCal);
        context.put("expirationTime", expireTimeValue);
        context.put("cacheAvailable", false);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataRequest")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node queryNode = doc.getElementsByTagName("Query").item(0);
        Assert.assertTrue(queryNode.getFirstChild().getNodeValue()
                .contains(request.getQuery()));
        Node expireNode = doc.getElementsByTagName("ExpirationTime").item(0);
        Assert.assertEquals(expireTimeValue, expireNode.getFirstChild()
                .getNodeValue());
        Node cacheNode = doc.getElementsByTagName("CacheAvailable").item(0);
        Assert.assertEquals("false", cacheNode.getFirstChild().getNodeValue());
    }

    /**
     * Failure test for DataExchangeService#addDataRequest when request is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataRequestFail1() throws Exception {
        dataExchangeService.addDataRequest(null);
    }

    /**
     * Failure test for DataExchangeService#addDataRequest when expiration time
     * is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataRequestFail2() throws Exception {
        DataRequest request = new DataRequest();
        request.setExpirationTime(null);
        dataExchangeService.addDataRequest(request);
    }

    /**
     * Failure test for DataExchangeService#addDataRequest when requesterId and
     * originalRequesterId is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataRequestFail3() throws Exception {
        DataRequest request = new DataRequest();
        request.setRequesterId(null);
        request.setOriginalRequesterId(null);
        request.setExpirationTime(new Date());
        dataExchangeService.addDataRequest(request);
    }

    /**
     * Failure test for DataExchangeService#addDataRequest when requesterId is
     * emtpy and originalRequesterId is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataRequestFail4() throws Exception {
        DataRequest request = new DataRequest();
        request.setExpirationTime(new Date());
        request.setRequesterId("");
        dataExchangeService.addDataRequest(request);
    }

    /**
     * Failure test for DataExchangeService#addDataRequest when requesterId is
     * not valid
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testAddDataRequestFail5() throws Exception {
        DataRequest request = new DataRequest();
        request.setRequesterId("not exist user Id");
        request.setExpirationTime(new Date());
        dataExchangeService.addDataRequest(request);
    }

    /**
     * Failure test for DataExchangeService#addDataRequest when user with
     * requesterId is participationRatioLow
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = InsufficientParticipationRatioException.class)
    public void testAddDataRequestFail6() throws Exception {
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        Assert.assertFalse(userService.isParticipationRatioLow(user.getId()));
        entityManager
                .createQuery(
                        "update UserStatistics u set u.dataRequestsInitiated=-1,"
                                + "u.dataRequestsResponded =2 where u.userId=:userId")
                .setParameter("userId", user.getId()).executeUpdate();
        entityManager.flush();
        entityManager.clear();
        Assert.assertTrue(userService.isParticipationRatioLow(user.getId()));
        DataRequest request = new DataRequest();
        request.setExpirationTime(new Date());
        request.setRequesterId(user.getId());
        dataExchangeService.addDataRequest(request);
    }

    /**
     * Accuracy test for DataExchangeService#addDataResponse
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataResponse1() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        DataResponse response = getDataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        String queueName = dataResponseQueuePrefix + request.getRequesterId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        dataExchangeService.addDataResponse(response);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(initialRespondedRequestsValue + 1,
                ust.getDataRequestsResponded());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Assert.assertEquals(0, message.getJMSExpiration());
        Template template = velocityEngine
                .getTemplate(dataResponseTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("response", response);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataResponse")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node respondentNode = doc.getElementsByTagName("RespondentID").item(0);
        Assert.assertEquals(user.getId(), respondentNode.getFirstChild()
                .getNodeValue());
        Node dataNode = doc.getElementsByTagName("Data").item(0);
        Assert.assertTrue(dataNode.getTextContent()
                .contains(response.getData()));
    }

    /**
     * Accuracy test for DataExchangeService#addDataResponse with RequestDenied
     * true
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataResponse2() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        DataResponse response = getDataResponse();
        response.setRequestDenied(true);
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertTrue(response.isRequestDenied());
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        String queueName = dataResponseQueuePrefix + request.getRequesterId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        dataExchangeService.addDataResponse(response);
        entityManager.flush();
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Assert.assertEquals(0, message.getJMSExpiration());
        Template template = velocityEngine
                .getTemplate(dataResponseTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("response", response);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataResponse")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node respondentNode = doc.getElementsByTagName("RespondentID").item(0);
        Assert.assertEquals(user.getId(), respondentNode.getFirstChild()
                .getNodeValue());
        Assert.assertEquals(0, doc.getElementsByTagName("Data").getLength());
    }

    /**
     * Accuracy test for DataExchangeService#addDataResponse with data null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAddDataResponse3() throws Exception {
        cacheService.clear();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        DataResponse response = getDataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        response.setData(null);
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        UserStatistics ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(initialRespondedRequestsValue,
                ust.getDataRequestsResponded());
        String queueName = dataResponseQueuePrefix + request.getRequesterId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        String cacheKey = response.getRespondentId() + ":" + request.getQuery();
        String cacheValue = "cache value";
        cacheService.put(cacheKey, cacheValue);
        dataExchangeService.addDataResponse(response);
        ust = userService.getUserStatistics(user.getId());
        Assert.assertEquals(initialRespondedRequestsValue + 1,
                ust.getDataRequestsResponded());
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Assert.assertEquals(0, message.getJMSExpiration());
        Template template = velocityEngine
                .getTemplate(dataResponseTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("response", response);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        String jmsMessage = sw.toString();
        String receivedMessage = ((TextMessage) message).getText();
        Assert.assertEquals(jmsMessage, receivedMessage);
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder().parse(
                new ByteArrayInputStream(receivedMessage.getBytes("utf-8")));
        Assert.assertEquals(1, doc.getElementsByTagName("DataResponse")
                .getLength());
        Node requestIDNode = doc.getElementsByTagName("RequestID").item(0);
        Assert.assertEquals(request.getId(), requestIDNode.getFirstChild()
                .getNodeValue());
        Node respondentNode = doc.getElementsByTagName("RespondentID").item(0);
        Assert.assertEquals(user.getId(), respondentNode.getFirstChild()
                .getNodeValue());
        Node dataNode = doc.getElementsByTagName("Data").item(0);
        Assert.assertTrue(dataNode.getTextContent().contains(cacheValue));
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when response is
     * null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataResponseFail1() throws Exception {
        dataExchangeService.addDataResponse(null);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when response time
     * is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataResponseFail2() throws Exception {
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(null);
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when requestId is
     * null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataResponseFail3() throws Exception {
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(new Date());
        response.setRequestId(null);
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when requestId is
     * empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDataResponseFail4() throws Exception {
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(new Date());
        response.setRequestId("");
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when requestId not
     * valid
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testAddDataResponseFail5() throws Exception {
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(new Date());
        response.setRequestId("not found Id");
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when request expired
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = DataRequestExpiredException.class)
    public void testAddDataResponseFail6() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, -10);
        Date exireDate = calendar.getTime();
        Assert.assertTrue(now.after(exireDate));
        DataRequest request = getDataRequest();
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when user not in
     * requested partners
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthorizationException.class)
    public void testAddDataResponseFail7() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when no cache found
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testAddDataResponseFail8() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        cacheService.clear();
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when no cache with
     * String type found
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testAddDataResponseFail9() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        DataResponse response = getDataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        response.setData(null);
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        cacheService.clear();
        String cacheKey = response.getRespondentId() + ":" + request.getQuery();
        cacheService.put(cacheKey, true);
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when no
     * UserStatistics found
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testAddDataResponseFail10() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        DataResponse response = getDataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse when
     * requestedPartners not contains valid value
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthorizationException.class)
    public void testAddDataResponseFail11() throws Exception {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        DataRequest request = getDataRequest();
        request.setExpirationTime(exireDate);
        request.setRequestedPartners(Arrays.asList(user.getId()));
        entityManager.persist(request);
        entityManager.flush();
        DataResponse response = new DataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId("error Id");
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse with data null and
     * no cache
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testAddDataResponseFail12() throws Exception {
        cacheService.clear();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        DataResponse response = getDataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        response.setData(null);
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Failure test for DataExchangeService#addDataResponse with data null and
     * not valid cache
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testAddDataResponseFail13() throws Exception {
        cacheService.clear();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 10);
        Date exireDate = calendar.getTime();
        Assert.assertFalse(now.after(exireDate));
        DataRequest request = getDataRequest();
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        DataResponse response = getDataResponse();
        response.setResponseTimestamp(now);
        response.setRequestId(request.getId());
        response.setRespondentId(user.getId());
        response.setData(null);
        request.setRequestedPartners(Arrays.asList(response.getRespondentId()));
        request.setExpirationTime(exireDate);
        entityManager.persist(request);
        entityManager.flush();
        Assert.assertFalse(request.isCacheSafe());
        Assert.assertFalse(response.isRequestDenied());
        String cacheKey = response.getRespondentId() + ":" + request.getQuery();
        cacheService.put(cacheKey, true);
        dataExchangeService.addDataResponse(response);
    }

    /**
     * Accuracy test for DataExchangeService#getDataRequest
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGetDataRequest() throws Exception {
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        String id = user.getId();
        DataRequest request = getDataRequest();
        request.setRequestedPartners(Arrays.asList(id));
        Assert.assertNull(dataExchangeService.getDataRequest(request.getId()));
        entityManager.persist(request);
        entityManager.flush();
        DataRequest getRequest = dataExchangeService.getDataRequest(request
                .getId());
        Assert.assertEquals(request.getId(), getRequest.getId());
        Assert.assertEquals(request.getOriginalRequesterId(),
                getRequest.getOriginalRequesterId());
        Assert.assertEquals(request.getQuery(), getRequest.getQuery());
        Assert.assertEquals(request.getRequesterId(),
                getRequest.getRequesterId());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Assert.assertEquals(format.format(request.getExpirationTime()),
                format.format(getRequest.getExpirationTime()));
        Assert.assertEquals(1, getRequest.getRequestedPartners().size());
        Assert.assertEquals(1, getRequest.getRequestedPartners().size());
        Assert.assertEquals(id, getRequest.getRequestedPartners().get(0));
    }

    /**
     * Failure test for DataExchangeService#getDataRequest when id is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetDataRequestFail1() throws Exception {
        dataExchangeService.getDataRequest(null);
    }

    /**
     * Failure test for DataExchangeService#getDataRequest when id is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetDataRequestFail2() throws Exception {
        dataExchangeService.getDataRequest("");
    }

    /**
     * Accuracy test for DataExchangeService#deliverAnalysisResult when no
     * response
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testDeliverAnalysisResult1() throws Exception {
        DataRequest request = getDataRequest();
        entityManager.persist(request);
        entityManager.flush();
        String userId = request.getRequesterId();
        String requestId = request.getId();
        String result = "result";
        dataExchangeService.deliverAnalysisResult(userId, requestId, result);
    }

    /**
     * Accuracy test for DataExchangeService#deliverAnalysisResult when exist
     * response
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testDeliverAnalysisResult2() throws Exception {
        DataRequest request = getDataRequest();
        entityManager.persist(request);
        DataResponse response = getDataResponse();
        User user = getTestUser();
        user.setId(response.getRespondentId());
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        response.setRequestId(request.getId());
        entityManager.persist(response);
        entityManager.flush();
        String userId = request.getRequesterId();
        String requestId = request.getId();
        String result = "result";
        String queueName = analysisResultQueuePrefix
                + response.getRespondentId();
        String destinationName = queueName
                + "; {create: always, node:{type:queue,durable:True}}";
        jmsTemplate.convertAndSend(destinationName, "create queue");
        Destination destination = new AMQAnyDestination("ADDR:" + queueName
                + "; {create: never, delete: never}");
        dataExchangeService.deliverAnalysisResult(userId, requestId, result);
        Message message = jmsTemplate.receive(destination);
        Assert.assertEquals("create queue", ((TextMessage) message).getText());
        message = jmsTemplate.receive(destination);
        Assert.assertTrue(message instanceof TextMessage);
        Assert.assertEquals(0, message.getJMSExpiration());
        Assert.assertEquals(result, ((TextMessage) message).getText());
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when userId is
     * null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeliverAnalysisResultFail1() throws Exception {
        String requestId = "requestId";
        String result = "result";
        dataExchangeService.deliverAnalysisResult(null, requestId, result);
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when userId is
     * empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeliverAnalysisResultFail2() throws Exception {
        String requestId = "requestId";
        String result = "result";
        dataExchangeService.deliverAnalysisResult("", requestId, result);
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when requestId
     * is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeliverAnalysisResultFail3() throws Exception {
        String userId = "userId";
        String result = "result";
        dataExchangeService.deliverAnalysisResult(userId, null, result);
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when requestId
     * is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeliverAnalysisResultFail4() throws Exception {
        String userId = "userId";
        String result = "result";
        dataExchangeService.deliverAnalysisResult(userId, "", result);
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when result is
     * null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeliverAnalysisResultFail5() throws Exception {
        String userId = "userId";
        String requestId = "requestId";
        dataExchangeService.deliverAnalysisResult(userId, requestId, null);
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when result is
     * empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeliverAnalysisResultFail6() throws Exception {
        String userId = "userId";
        String requestId = "requestId";
        dataExchangeService.deliverAnalysisResult(userId, requestId, "");
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when request
     * with given id not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDeliverAnalysisResultFail7() throws Exception {
        String userId = "userId";
        String requestId = "not exist request";
        String result = "result";
        dataExchangeService.deliverAnalysisResult(userId, requestId, result);
    }

    /**
     * Failure test for DataExchangeService#deliverAnalysisResult when
     * requesterId not same as userId
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthorizationException.class)
    public void testDeliverAnalysisResultFail8() throws Exception {
        DataRequest request = getDataRequest();
        entityManager.persist(request);
        entityManager.flush();
        String userId = "userId";
        String requestId = request.getId();
        String result = "result";
        dataExchangeService.deliverAnalysisResult(userId, requestId, result);
    }

    /**
     * Get data request.
     * 
     * @return the data request.
     */
    private DataRequest getDataRequest() {
        DataRequest request = new DataRequest();
        request.setCacheSafe(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 2);
        request.setExpirationTime(calendar.getTime());
        request.setId(UUID.randomUUID().toString());
        request.setOriginalRequesterId(UUID.randomUUID().toString());
        request.setQuery("query");
        request.setRequesterId(UUID.randomUUID().toString());
        return request;

    }

    /**
     * Get test data response
     * 
     * @return the data response.
     */
    private DataResponse getDataResponse() {
        DataResponse response = new DataResponse();
        response.setData("data");
        response.setRequestDenied(false);
        response.setRequestId(UUID.randomUUID().toString());
        response.setResponseTimestamp(new Date());
        response.setRespondentId(UUID.randomUUID().toString());
        return response;

    }

}
