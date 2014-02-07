/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import junit.framework.JUnit4TestAdapter;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.velocity.app.VelocityEngine;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import com.hfpp.network.BaseUnitTests;
import com.hfpp.network.hub.services.DataExchangeService;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;

/**
 * <p>
 * Unit tests for {@link DataExchangeServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get data exchange service by spring context from base class in
 * test_Spring</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class DataExchangeServiceImplUnitTests extends BaseUnitTests {
    /**
     * <p>
     * Represents the <code>DataExchangeServiceImpl</code> instance used in
     * tests.
     * </p>
     */
    private DataExchangeServiceImpl instance;

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Represents jms template used in tests.
     * </p>
     */
    private JmsTemplate jmsTemplate;

    /**
     * <p>
     * Represents cache service used in tests.
     * </p>
     */
    private CacheServiceImpl cacheService;

    /**
     * <p>
     * Represents user service used in tests.
     * </p>
     */
    private UserServiceImpl userService;

    /**
     * <p>
     * Represents velocity engine used in tests.
     * </p>
     */
    private VelocityEngine velocityEngine;

    /**
     * <p>
     * Represents analysis result queue prefix used in tests.
     * </p>
     */
    private String analysisResultQueuePrefix = "hfpp.queue.analysis_results.";

    /**
     * <p>
     * Represents data response queue prefix used in tests.
     * </p>
     */
    private String dataResponseQueuePrefix = "hfpp.queue.data_responses.";

    /**
     * <p>
     * Represents data request queue prefix used in tests.
     * </p>
     */
    private String dataRequestQueuePrefix = "hfpp.queue.data_requests.";

    /**
     * <p>
     * Represents data response template path used in tests.
     * </p>
     */
    private String dataResponseTemplatePath = VELOCIY_TEMPLATES
            + "data_request_routed_to_partner.xml.vm";

    /**
     * <p>
     * Represents data request template path used in tests.
     * </p>
     */
    private String dataRequestTemplatePath = VELOCIY_TEMPLATES
            + "data_response_routed_to_requester.xml.vm";

    /**
     * <p>
     * Represents ehcache used in tests.
     * </p>
     */
    private Ehcache ehcache;

    /**
     * <p>
     * Represents initial responded requests value used in tests.
     * </p>
     */
    private int initialRespondedRequestsValue = 1;

    /**
     * <p>
     * Represents participation ratio threshold used in tests.
     * </p>
     */
    private double participationRatioThreshold = 3;

    /**
     * <p>
     * Represents password encryptor used in tests.
     * </p>
     */
    private PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    /**
     * <p>
     * Represents roles eligible to receive data requests used in tests.
     * </p>
     */
    private List<String> rolesEligibleToReceiveDataRequests = new ArrayList<String>(
            Arrays.asList("role1", "role2", "role3"));

    /**
     * <p>
     * Represents roles eligible to initiate data requests used in tests.
     * </p>
     */
    private List<String> rolesEligibleToInitiateDataRequests = new ArrayList<String>(
            Arrays.asList("role1", "role2", "role3"));

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
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        instance = new DataExchangeServiceImpl();

        entityManager = getEntityManager();

        Properties properties = new Properties();
        InputStream inStream = new FileInputStream(TEST_FILES
                + "qpid.properties");
        try {
            properties.load(inStream);
        } finally {
            inStream.close();
        }
        context = new InitialContext(properties);
        connectionFactory = (ConnectionFactory) context
                .lookup("qpidConnectionfactory");
        jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);

        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.addCacheIfAbsent("cache1");
        ehcache = cacheManager.getEhcache("cache1");
        ehcache.removeAll();
        cacheService = new CacheServiceImpl();
        cacheService.setEhcache(ehcache);
        cacheService.checkConfiguration();

        userService = new UserServiceImpl();
        userService.setEntityManager(entityManager);
        userService
                .setInitialRespondedRequestsValue(initialRespondedRequestsValue);
        userService.setParticipationRatioThreshold(participationRatioThreshold);
        userService.setPasswordEncryptor(passwordEncryptor);
        userService
                .setRolesEligibleToReceiveDataRequests(rolesEligibleToReceiveDataRequests);
        userService
                .setRolesEligibleToInitiateDataRequests(rolesEligibleToInitiateDataRequests);
        userService.checkConfiguration();

        velocityEngine = new VelocityEngine();

        instance.setEntityManager(entityManager);
        instance.setJmsTemplate(jmsTemplate);
        instance.setCacheService(cacheService);
        instance.setUserService(userService);
        instance.setVelocityEngine(velocityEngine);
        instance.setAnalysisResultQueuePrefix(analysisResultQueuePrefix);
        instance.setDataResponseQueuePrefix(dataResponseQueuePrefix);
        instance.setDataRequestQueuePrefix(dataRequestQueuePrefix);
        instance.setDataResponseTemplatePath(dataResponseTemplatePath);
        instance.setDataRequestTemplatePath(dataRequestTemplatePath);

        instance.checkConfiguration();

        request = new DataRequest();
        request.setId(UUID.randomUUID().toString());
        request.setRequesterId("1");
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
        CacheManager.getInstance().removeCache("cache1");

        context.close();
    }

    /**
     * <p>
     * Accuracy test with Spring.<br>
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     * @since 1.1
     */
    @SuppressWarnings("unused")
    @Test
    public void test_Spring() throws Exception {
        // Get service
        DataExchangeService service = (DataExchangeService) APP_CONTEXT
                .getBean("dataExchangeService");

        service.addDataRequest(request);

        DataRequest dataRequest = service.getDataRequest(request.getId());

        service.addDataResponse(response);

        service.deliverAnalysisResult(request.getRequesterId(),
                response.getRequestId(), "The result.");
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>DataExchangeServiceImpl()</code>.
     * <br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new DataExchangeServiceImpl();

        assertNull("'entityManager' should be correct.",
                BaseUnitTests.getField(instance, "entityManager"));
        assertNull("'jmsTemplate' should be correct.",
                BaseUnitTests.getField(instance, "jmsTemplate"));
        assertNull("'cacheService' should be correct.",
                BaseUnitTests.getField(instance, "cacheService"));
        assertNull("'userService' should be correct.",
                BaseUnitTests.getField(instance, "userService"));
        assertNull("'velocityEngine' should be correct.",
                BaseUnitTests.getField(instance, "velocityEngine"));
        assertNotNull("'analysisResultQueuePrefix' should be correct.",
                BaseUnitTests.getField(instance, "analysisResultQueuePrefix"));
        assertNotNull("'dataResponseQueuePrefix' should be correct.",
                BaseUnitTests.getField(instance, "dataResponseQueuePrefix"));
        assertNotNull("'dataRequestQueuePrefix' should be correct.",
                BaseUnitTests.getField(instance, "dataRequestQueuePrefix"));
        assertNull("'dataResponseTemplatePath' should be correct.",
                BaseUnitTests.getField(instance, "dataResponseTemplatePath"));
        assertNull("'dataRequestTemplatePath' should be correct.",
                BaseUnitTests.getField(instance, "dataRequestTemplatePath"));
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>addDataRequest(DataRequest request)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_addDataRequest() throws Exception {
        entityManager.getTransaction().begin();
        instance.addDataRequest(request);
        entityManager.getTransaction().commit();

        String id = request.getId();

        DataRequest retrievedRequest = getEntityManager().find(
                DataRequest.class, id);
        assertEquals("'addDataRequest' should be correct.",
                request.getOriginalRequesterId(),
                retrievedRequest.getOriginalRequesterId());
        assertEquals("'addDataRequest' should be correct.",
                request.getRequesterId(), retrievedRequest.getRequesterId());
        assertEquals("'addDataRequest' should be correct.", request.getQuery(),
                retrievedRequest.getQuery());
        assertEquals("'addDataRequest' should be correct.", request
                .getRequestedPartners().size(), retrievedRequest
                .getRequestedPartners().size());
        assertEquals("'addDataRequest' should be correct.",
                request.isCacheSafe(), retrievedRequest.isCacheSafe());
        assertNotNull("'addDataRequest' should be correct.",
                retrievedRequest.getExpirationTime());
    }

    /**
     * <p>
     * Accuracy test for the method <code>getDataRequest(String id)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getDataRequest() throws Exception {
        entityManager.getTransaction().begin();
        instance.addDataRequest(request);
        entityManager.getTransaction().commit();

        String id = request.getId();

        DataRequest res = instance.getDataRequest(id);
        assertEquals("'getDataRequest' should be correct.",
                request.getOriginalRequesterId(), res.getOriginalRequesterId());
        assertEquals("'getDataRequest' should be correct.",
                request.getRequesterId(), res.getRequesterId());
        assertEquals("'getDataRequest' should be correct.", request.getQuery(),
                res.getQuery());
        assertEquals("'getDataRequest' should be correct.", request
                .getRequestedPartners().size(), res.getRequestedPartners()
                .size());
        assertEquals("'getDataRequest' should be correct.",
                request.isCacheSafe(), res.isCacheSafe());
        assertNotNull("'getDataRequest' should be correct.",
                res.getExpirationTime());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>addDataResponse(DataResponse response)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_addDataResponse() throws Exception {
        entityManager.getTransaction().begin();
        instance.addDataRequest(request);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        instance.addDataResponse(response);
        entityManager.getTransaction().commit();

        DataResponse retrievedResponse = getEntityManager().find(
                DataResponse.class, response);
        assertEquals("'addDataResponse' should be correct.",
                response.getRequestId(), retrievedResponse.getRequestId());
        assertEquals("'addDataResponse' should be correct.",
                response.getRespondentId(), retrievedResponse.getRespondentId());
        assertNotNull("'addDataResponse' should be correct.",
                retrievedResponse.getResponseTimestamp());
        assertEquals("'addDataResponse' should be correct.",
                response.getData(), retrievedResponse.getData());
        assertEquals("'addDataResponse' should be correct.",
                response.isRequestDenied(), retrievedResponse.isRequestDenied());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>deliverAnalysisResult(String userId, String requestId,
     * String result)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_deliverAnalysisResult() throws Exception {
        entityManager.getTransaction().begin();
        instance.addDataRequest(request);
        entityManager.getTransaction().commit();

        response.setRequestDenied(false);
        entityManager.getTransaction().begin();
        instance.addDataResponse(response);
        entityManager.getTransaction().commit();

        Connection connection = connectionFactory.createConnection();
        try {
            Destination destination = new AMQAnyDestination("ADDR:"
                    + analysisResultQueuePrefix + response.getRespondentId()
                    + "; {create: never, delete: never}");
            MessageConsumer messageConsumer = getMessageConsumer(connection,
                    destination);

            entityManager.getTransaction().begin();
            instance.deliverAnalysisResult(request.getRequesterId(),
                    response.getRequestId(), "The result.");
            entityManager.getTransaction().commit();

            TextMessage message = (TextMessage) messageConsumer.receive();
            assertEquals("'deliverAnalysisResult' should be correct.",
                    "The result.", message.getText());
        } finally {
            connection.close();
        }
    }

    /**
     * Creates the message consumer.
     * 
     * @param connection
     *            the connection
     * @param destination
     *            the destination
     * 
     * @return the message consumer.
     * 
     * @throws Exception
     *             to Junit.
     */
    private static MessageConsumer getMessageConsumer(Connection connection,
            Destination destination) throws Exception {
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        return session.createConsumer(destination);
    }
}