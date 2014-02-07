/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hfpp.network.hub.services.impl.AuthenticationServiceImpl;
import com.hfpp.network.hub.services.impl.AuthorizationServiceImpl;
import com.hfpp.network.hub.services.impl.DataExchangeServiceImpl;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.hub.services.impl.UserServiceImpl;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.Role;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserStatistics;

/**
 * This is base test case for all function tests.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:test-context.xml" })
@Transactional(propagation = Propagation.REQUIRED)
public abstract class BaseTestCase extends TestCase {
    
    /**
     * Represents the document builder factory.
     */
    protected static final DocumentBuilderFactory DOCUMENTBUILDERFACTORY = DocumentBuilderFactory
            .newInstance();

    /**
     * Represents the test user name.
     */
    protected static final String TESTUSERNAME = "user2";

    /**
     * Represents the test user password.
     */
    protected static final String TESTUSERPASSWORD = "pass2";

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    protected EntityManager entityManager;

    /**
     * <p>
     * Represents the <code>EntityManagerFactory </code> for tests.
     * </p>
     */
    @Resource
    private EntityManagerFactory entityManagerFactory;

    /**
     * <p>
     * Represents user service used in tests.
     * </p>
     */
    @Autowired
    protected UserServiceImpl userService;

    /**
     * <p>
     * Represents authentication service used in tests.
     * </p>
     */
    @Autowired
    protected AuthenticationServiceImpl authenticationService;

    /**
     * <p>
     * Represents authorization service used in tests.
     * </p>
     */
    @Autowired
    protected AuthorizationServiceImpl authorizationService;

    /**
     * <p>
     * Represents data exchange service used in tests.
     * </p>
     */
    @Autowired
    protected DataExchangeServiceImpl dataExchangeService;
    
    /**
     * <p>
     * Represents data request message container.
     * </p>
     */
    @Resource
    private DefaultMessageListenerContainer dataRequestMessageContainer;
    
    /**
     * <p>
     * Represents data response message container.
     * </p>
     */
    @Resource
    private DefaultMessageListenerContainer dataResponseMessageContainer;
    
    
    /**
     * <p>
     * Represents analysis result message container.
     * </p>
     */
    @Resource
    private DefaultMessageListenerContainer analysisResultMessageContainer;
    
    
    /**
     * <p>
     * Represents general service message container.
     * </p>
     */
    @Resource
    private DefaultMessageListenerContainer generalServicetMessageContainer;


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
        entityManager = entityManagerFactory.createEntityManager();
        userService.setEntityManager(entityManager);
        authenticationService.setEntityManager(entityManager);
        authorizationService.setEntityManager(entityManager);
        dataExchangeService.setEntityManager(entityManager);
        dataRequestMessageContainer.start();
        dataResponseMessageContainer.start();
        analysisResultMessageContainer.start();
        generalServicetMessageContainer.start();
    }

    /**
     * Get test user.
     * 
     * @return test user.
     */
    public static User getTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        Role role = new Role();
        role.setId(UUID.randomUUID().toString());
        role.setName(UUID.randomUUID().toString());
        user.setRole(role);
        user.setOrganizationName("organizationName");
        user.setUsername(UUID.randomUUID().toString());
        user.setEligibleToInitiateDataRequests(false);
        user.setAutoRetrieveCachedData(false);
        user.setEligibleToReceiveDataRequests(false);
        user.setCreatedDate(new Date());
        user.setUpdatedDate(new Date());
        return user;
    }

    /**
     * <p>
     * Clears the database.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    protected void clearDB() throws Exception {
        entityManager.createQuery("DELETE FROM AuditRecord").executeUpdate();
        entityManager.createQuery("DELETE FROM UserStatistics").executeUpdate();
        entityManager.createQuery("DELETE FROM DataResponse").executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM data_request_requested_user").executeUpdate();
        entityManager.createQuery("DELETE FROM DataRequest").executeUpdate();
        entityManager.createQuery("DELETE FROM User").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM h_user").executeUpdate();
        entityManager.createQuery("DELETE FROM Role").executeUpdate();
        entityManager.flush();
    }

    /**
     * <p>
     * Loads the data into database.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    protected void loadData() throws Exception {
        for (int i = 1; i <= 3; i++) {
            String id = Integer.toString(i);
            User user = getTestUser();
            user.setId(id);
            user.setUsername("user" + i);
            user.getRole().setId(id);
            user.getRole().setName("role" + i);
            user.setOrganizationName("org" + i);
            entityManager.persist(user.getRole());
            entityManager.persist(user);
            entityManager.flush();
            userService.setPassword(id, "pass" + i);
            UserStatistics ust = new UserStatistics();
            ust.setUserId(id);
            entityManager.persist(ust);
            entityManager.flush();
        }
    }

    /**
     * Create document for data request message.
     * 
     * @param request
     *            the data request.
     * @return document for data request message.
     * @throws Exception
     *             throws if any error happen.
     */
    protected Document getDataRequestDocument(DataRequest request)
            throws Exception {
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder()
                .newDocument();
        Element root = doc.createElement("DataRequest");
        doc.appendChild(root);
        Element requestID = doc.createElement("RequestID");
        requestID.setTextContent(request.getId());
        root.appendChild(requestID);
        if (!Helper.isNullOrEmpty(request.getOriginalRequesterId())) {
            Element originalRequesterID = doc
                    .createElement("OriginalRequesterID");
            originalRequesterID
                    .setTextContent(request.getOriginalRequesterId());
            root.appendChild(originalRequesterID);
        }
        Element query = doc.createElement("Query");
        query.setTextContent(request.getQuery());
        root.appendChild(query);
        Element requestedPartners = doc.createElement("RequestedPartners");
        root.appendChild(requestedPartners);
        if (request.getRequestedPartners() != null
                && !request.getRequestedPartners().isEmpty()) {
            for (String requestParterId : request.getRequestedPartners()) {
                Element partnerID = doc.createElement("PartnerID");
                partnerID.setTextContent(requestParterId);
                requestedPartners.appendChild(partnerID);
            }
        }
        Element expirationTime = doc.createElement("ExpirationTime");
        Calendar expirationTimeCal = Calendar.getInstance();
        expirationTimeCal.setTime(request.getExpirationTime());
        expirationTime.setTextContent(DatatypeConverter
                .printDateTime(expirationTimeCal));
        root.appendChild(expirationTime);
        Element cacheSafe = doc.createElement("CacheSafe");
        cacheSafe.setTextContent(DatatypeConverter.printBoolean(request
                .isCacheSafe()));
        root.appendChild(cacheSafe);
        return doc;
    }

    /**
     * Create document for data response message.
     * 
     * @param response
     *            the data response.
     * @param useCache
     *            the user cache flag.
     * @param dataValue
     *            the data value.
     * @return document from data response message.
     * @throws Exception
     *             throws if any error happen.
     */
    protected Document getDataResponseDocument(DataResponse response,
            boolean useCache, String dataValue) throws Exception {
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder()
                .newDocument();
        Element root = doc.createElement("DataResponse");
        doc.appendChild(root);
        Element requestID = doc.createElement("RequestID");
        requestID.setTextContent(response.getRequestId());
        root.appendChild(requestID);
        Element requestDenied = doc.createElement("RequestDenied");
        requestDenied.setTextContent(DatatypeConverter.printBoolean(response
                .isRequestDenied()));
        root.appendChild(requestDenied);
        Element data = doc.createElement("Data");
        data.setAttribute("useCache", DatatypeConverter.printBoolean(useCache));
        data.setTextContent(dataValue);
        root.appendChild(data);
        return doc;
    }

    /**
     * Get document for analysis result message.
     * 
     * @param request
     *            the data request.
     * @return document for analysis result message
     * @throws Exception
     *             throws if any error happen.
     */
    protected Document getAnalysisResultDocument(DataRequest request)
            throws Exception {
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder()
                .newDocument();
        Element root = doc.createElement("AnalysisResult");
        doc.appendChild(root);
        Element requestID = doc.createElement("RequestID");
        requestID.setTextContent(request.getId());
        root.appendChild(requestID);
        Element result = doc.createElement("Result");
        result.setTextContent("result");
        root.appendChild(result);
        return doc;
    }

    /**
     * Create document for partner list request message.
     * 
     * @param user
     *            the user.
     * @return document for partner list request message
     * @throws Exception
     *             throws if any error happen.
     */
    protected Document getPartnerListRequestDocument(User user)
            throws Exception {
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder()
                .newDocument();
        Element root = doc.createElement("PartnerListRequest");
        doc.appendChild(root);
        Element orgName = doc.createElement("OrganizationName");
        orgName.setTextContent(user.getOrganizationName());
        root.appendChild(orgName);
        Element eligibleToReceiveDataRequest = doc
                .createElement("EligibleToReceiveDataRequest");
        eligibleToReceiveDataRequest.setTextContent(DatatypeConverter
                .printBoolean(true));
        root.appendChild(eligibleToReceiveDataRequest);
        Element eligibleToInitiateDataRequest = doc
                .createElement("EligibleToInitiateDataRequest");
        eligibleToInitiateDataRequest.setTextContent(DatatypeConverter
                .printBoolean(true));
        root.appendChild(eligibleToInitiateDataRequest);
        return doc;
    }

    /**
     * Create document for partner statistics request message.
     * 
     * @param user
     *            the user.
     * @return document for partner statistics request message
     * @throws Exception
     *             throws if any error happen.
     */
    protected Document getPartnerStatisticsRequestDocument(User user)
            throws Exception {
        Document doc = DOCUMENTBUILDERFACTORY.newDocumentBuilder()
                .newDocument();
        Element root = doc.createElement("PartnerStatisticsRequest");
        doc.appendChild(root);
        Element id = doc.createElement("PartnerID");
        id.setTextContent(user.getId());
        root.appendChild(id);
        return doc;
    }

    /**
     * Get string value from document.
     * 
     * @param doc
     *            the document
     * @return string value from document
     * @throws Exception
     *             throws if any erro happen.
     */
    protected String getDocumentString(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.getBuffer().toString();
    }

    /**
     * Check audit record exist or not exist in db.
     * 
     * @param testUser
     *            the user to test
     * @param operation
     *            the operation
     * @param denied
     *            the denied flag
     * @param message
     *            the message
     */
    protected void checkAuditRecord(User testUser, String operation,
            boolean denied, String message) {
        Query query = entityManager
                .createNativeQuery("select count(*) from audit_record "
                        + "a where a.user_id=:userId and a.action=:action and "
                        + "a.denied=:denied and a.message=:message");
        query.setParameter("userId", testUser.getId());
        query.setParameter("action", operation);
        query.setParameter("denied", denied);
        query.setParameter("message", message);
        assertEquals(1, ((Number) query.getSingleResult()).intValue());
    }
}
