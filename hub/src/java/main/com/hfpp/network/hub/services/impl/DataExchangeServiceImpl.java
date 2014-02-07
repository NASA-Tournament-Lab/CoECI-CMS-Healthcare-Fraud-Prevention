/*
 * Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.AuthorizationException;
import com.hfpp.network.hub.services.CacheService;
import com.hfpp.network.hub.services.DataExchangeService;
import com.hfpp.network.hub.services.DataRequestExpiredException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.InsufficientParticipationRatioException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.models.CachedObjectWrapper;
import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;
import com.hfpp.network.models.SearchResult;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserSearchCriteria;
import com.hfpp.network.models.UserStatistics;

/**
 * <p>
 * This is the implementation of DataExchangeService.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>add final to signature</li>
 * <li>set requestId responseTimestamp fields of response when addDataRequest.</li>
 * <li>check user is null or not when try to get partners in addDataRequest</li>
 * <li>check UserStatistics found or not in addDataRequest</li>
 * <li>check cache is string type in addDataRequest</li>
 * <li>check requestId and responseTimestamp in addDataResponse</li>
 * <li>check UserStatistics found or not in addDataResponse</li>
 * <li>check cache whether is string type in addDataResponse</li>
 * <li>add propagation=Propagation.REQUIRES_NEW so when error happen in service
 * will not rollback jms message listener</li>
 * </ul>
 * </p>
 * <p>
 * v1.2 Changes:
 * <ul>
 * <li>change velocityEngine with null log</li>
 * </ul>
 * </p>
 * v1.3 Changes(Healthcare Fraud Prevention Release Assembly v1.0):
 * <ul>
 * <li> update addDataResponse to store count of dataRequestDeclined of user</li>
 * <li> added ERROR_MESSAGE_WAITING_APPROVAL
 * </ul>
 * 
 * @author flying2hk, sparemax,TCSASSEMBLER
 * @version 1.3
 */
public class DataExchangeServiceImpl extends BasePersistenceService implements
        DataExchangeService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = DataExchangeServiceImpl.class
            .getName();

    /**
     * Represents the string to query data response.
     */
    private static final String SQL_QUERY_DATA_RESPONSE = "SELECT r FROM DataResponse r"
            + " WHERE r.requestId = :requestId AND r.requestDenied = :requestDenied";

    /**
     * Represents the error message for waiting approval
     * @since 1.3
     */
    private static final String ERROR_MESSAGE_WAITING_APPROVAL = "Waiting Approval";
    
    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the JmsTemplate used to send JMS messages. It should be
     * non-null. It is required.
     */
    private JmsTemplate jmsTemplate;

    /**
     * Represents the CacheService used to access cache. It should be non-null.
     * It is required.
     */
    private CacheService cacheService;

    /**
     * Represents the UserService used to access user from persistence. It
     * should be non-null. It is required.
     */
    private UserService userService;

    /**
     * Represents the VelocityEngine used to generate XML messages. It should be
     * non-null. It is required.
     */
    private VelocityEngine velocityEngine;

    /**
     * Represents the name prefix of the AMQP queue from which Network Nodes
     * receive analysis result. It is optional. It should be non-null, non-empty
     * string. It will be initialized to "hfpp.queue.analysis_results." by
     * default. Usually there is no need to set this explicitly, it is
     * configurable just for possible future queue name changes.
     */
    private String analysisResultQueuePrefix = "hfpp.queue.analysis_results.";

    /**
     * Represents the name prefix of the AMQP queue from which Network Nodes
     * receive data responses. It is optional. It should be non-null, non-empty
     * string. It will be initialized to "hfpp.queue.data_responses." by
     * default. Usually there is no need to set this explicitly, it is
     * configurable just for possible future queue name changes.
     */
    private String dataResponseQueuePrefix = "hfpp.queue.data_responses.";

    /**
     * Represents the name prefix of the AMQP queue from which Network Nodes
     * receive data requests. It is optional. It should be non-null, non-empty
     * string. It will be initialized to "hfpp.queue.data_requests." by default.
     * Usually there is no need to set this explicitly, it is configurable just
     * for possible future queue name changes.
     */
    private String dataRequestQueuePrefix = "hfpp.queue.data_requests.";

    /**
     * Represents the file path of the Velocity template that is used to
     * generate Data Response XML message routed to data requester. It should be
     * non-null, non-empty string. It is required.
     */
    private String dataResponseTemplatePath;

    /**
     * Represents the file path of the Velocity template that is used to
     * generate Data Request XML message routed to requested partners. It should
     * be non-null, non-empty string. It is required.
     */
    private String dataRequestTemplatePath;

    /**
     * Creates an instance of DataExchangeServiceImpl.
     */
    public DataExchangeServiceImpl() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (entityManager, jmsTemplate, cacheService, userService or
     *             velocityEngine is null; analysisResultQueuePrefix,
     *             dataResponseQueuePrefix, dataRequestQueuePrefix,
     *             dataResponseTemplatePath or dataRequestTemplatePath is
     *             null/empty).
	 *@since 1.2
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();

        Helper.checkState(jmsTemplate == null, "'jmsTemplate' can't be null.");
        Helper.checkState(cacheService == null, "'cacheService' can't be null.");
        Helper.checkState(userService == null, "'userService' can't be null.");
        Helper.checkState(velocityEngine == null,
                "'velocityEngine' can't be null.");

        Helper.checkState(Helper.isNullOrEmpty(analysisResultQueuePrefix),
                "'analysisResultQueuePrefix' can't be null/empty.");
        Helper.checkState(Helper.isNullOrEmpty(dataResponseQueuePrefix),
                "'dataResponseQueuePrefix' can't be null/empty.");
        Helper.checkState(Helper.isNullOrEmpty(dataRequestQueuePrefix),
                "'dataRequestQueuePrefix' can't be null/empty.");
        Helper.checkState(Helper.isNullOrEmpty(dataResponseTemplatePath),
                "'dataResponseTemplatePath' can't be null/empty.");
        Helper.checkState(Helper.isNullOrEmpty(dataRequestTemplatePath),
                "'dataRequestTemplatePath' can't be null/empty.");
        java.util.Properties p = new java.util.Properties();
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        // Initialize VelocityEngine
        velocityEngine.init(p);
    }

    /**
     * This method is used to add data request to the network.
     * 
     * @param request
     *            the data request
     * 
     * @throws IllegalArgumentException
     *             if request is null
     * @throws EntityNotFoundException
     *             if the requester/original requester's partner contains not
     *             exist user Id or not found user statistics
     * @throws InsufficientParticipationRatioException
     *             if the requester/original requester's participation ratio is
     *             low
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(
            rollbackFor = Exception.class,
            propagation = Propagation.REQUIRES_NEW)
    public void addDataRequest(DataRequest request)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".addDataRequest(DataRequest request)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "request" },
                new Object[] { request });

        Helper.checkNull(LOGGER, signature, request, "request");

        try {
            String originalRequesterId = request.getOriginalRequesterId();
            String requesterId = (originalRequesterId == null) ? request
                    .getRequesterId() : originalRequesterId;
            // Check if user's participation ratio is low
            boolean participationRatioLow = userService
                    .isParticipationRatioLow(requesterId);
            if (participationRatioLow) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new InsufficientParticipationRatioException(
                                "User's participation ratio is low."));
            }
            List<String> requestedPartners = request.getRequestedPartners();
            if (requestedPartners == null) {
                requestedPartners = new ArrayList<String>();
                request.setRequestedPartners(requestedPartners);
            }
            if (requestedPartners.isEmpty()) {
                // There's no explicit requested partners, then query all
                // eligible users
                UserSearchCriteria criteria = new UserSearchCriteria();
                SearchResult<User> users = userService.search(criteria);
                for (User user : users.getValues()) {
                    requestedPartners.add(user.getId());
                }
            } else {
                // There are explicit requested partners, remove un-eligible
                // ones
                Iterator<String> iterator = requestedPartners.iterator();

                while (iterator.hasNext()) {
                    String id = iterator.next();
                    User user = userService.get(id);
                    if (user == null) {
                        // Log exception
                        throw Helper.logException(LOGGER, signature,
                                new EntityNotFoundException(
                                        "request partner with id '" + id
                                                + "' not exist"));
                    }
                    if (!user.isEligibleToReceiveDataRequests()) {
                        iterator.remove();
                    }
                }
            }

            // Persist the request
            EntityManager entityManager = getEntityManager();
            entityManager.persist(request);

            Template dataRequestTemplate = velocityEngine
                    .getTemplate(dataRequestTemplatePath);

            VelocityContext dataRequestContext = new VelocityContext();
            dataRequestContext.put("request", request);
            Calendar expirationTimeCal = Calendar.getInstance();
            expirationTimeCal.setTime(request.getExpirationTime());
            dataRequestContext.put("expirationTime",
                    DatatypeConverter.printDateTime(expirationTimeCal));

            VelocityContext dataResponseContext = new VelocityContext();
            dataResponseContext.put("request", request);

            // Process for each requested partner
            for (String userId : requestedPartners) {
                UserStatistics userStatistics = userService
                        .getUserStatistics(userId);
                // update "# of data requests received"
                userStatistics.setDataRequestsReceived(userStatistics
                        .getDataRequestsReceived() + 1);

                Object cacheKey = getCacheKey(request, userId);
                CachedObjectWrapper wrapper = cacheService.get(cacheKey);
                boolean cacheAvailable = request.isCacheSafe()
                        && (wrapper != null)
                        && (wrapper.getCachedObject() instanceof String);
                dataRequestContext.put("cacheAvailable", cacheAvailable);
                LOGGER.info("cacheAvailable:"+cacheAvailable);
                if (cacheAvailable) {
                    // Cache match
                    User user = userService.get(userId);
                    if (user.isAutoRetrieveCachedData()) {
                        // retrieve cached data and route Data Response to
                        // requester
                        DataResponse response = new DataResponse();
                        response.setRequestId(request.getId());
                        response.setRespondentId(userId);
                        response.setData((String) wrapper.getCachedObject());
                        response.setRequestDenied(false);
                        response.setResponseTimestamp(request
                                .getExpirationTime());
                        addDataResponse(response);
                    } else {
                        // route Data Request to requested user
                        Calendar cacheTimestampCal = Calendar.getInstance();
                        cacheTimestampCal.setTime(wrapper.getTimestamp());
                        dataRequestContext.put("cacheTimestamp",
                                DatatypeConverter
                                        .printDateTime(cacheTimestampCal));
                        String message = getContent(dataRequestTemplate,
                                dataRequestContext);

                        Destination destination = new AMQAnyDestination("ADDR:"
                                + dataRequestQueuePrefix + userId
                                + "; {create: never, delete: never}");
                        sendMessageToQueue(signature, destination, message,
                                request.getExpirationTime());
                    }
                } else {
                    // Cache miss, reroute Data Request to requested user
                    String message = getContent(dataRequestTemplate,
                            dataRequestContext);
                    Destination destination = new AMQAnyDestination("ADDR:"
                            + dataRequestQueuePrefix + userId
                            + "; {create: never, delete: never}");
                    sendMessageToQueue(signature, destination, message,
                            request.getExpirationTime());
                }

                // Persist user statistics changes
                entityManager.merge(userStatistics);
            }

            // Update requester statistics
            UserStatistics requesterStat = userService
                    .getUserStatistics(requesterId);
            if (requesterStat == null) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException(
                                "UserStatistics with user id '" + requesterId
                                        + "' not exist"));
            }
            requesterStat.setDataRequestsInitiated(requesterStat
                    .getDataRequestsInitiated() + 1);
            entityManager.merge(requesterStat);

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "The entity manager has been closed.", e));
        } catch (PersistenceException e) {
            // Log exception
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new NetworkHubServiceException(
                                    "An error has occurred while accessing the persistence.",
                                    e));
        } catch (URISyntaxException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException("The URI is invalid.", e));
        } catch (VelocityException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "A velocity error has occurred.", e));
        }
    }

    /**
     * This method is used to get a data request by ID.
     * 
     * @param id
     *            the data request ID
     * 
     * @return the data request, null will be returned if there's no such data
     *         request.
     * 
     * @throws IllegalArgumentException
     *             if id is null or empty string.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public DataRequest getDataRequest(String id)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".getDataRequest(String id)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "id" },
                new Object[] { id });

        Helper.checkNullOrEmpty(LOGGER, signature, id, "id");

        try {
            EntityManager entityManager = getEntityManager();
            DataRequest dataRequest = entityManager.find(DataRequest.class, id);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { dataRequest });
            return dataRequest;
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "The entity manager has been closed.", e));
        } catch (PersistenceException e) {
            // Log exception
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new NetworkHubServiceException(
                                    "An error has occurred while accessing the persistence.",
                                    e));
        }
    }

    /**
     * This method is used to add data response to the network.
     * 
     * @param response
     *            the data response
     * 
     * @throws IllegalArgumentException
     *             if response is null or requestId of response if null or empty
     *             or responseTimestamp of response is null
     * @throws EntityNotFoundException
     *             if there is no corresponding request identified requestId
     * @throws DataRequestExpiredException
     *             if the request to respond has expired
     * @throws AuthorizationException
     *             if the respondent isn't on the requested partner list of the
     *             request
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(
            rollbackFor = Exception.class,
            propagation = Propagation.REQUIRES_NEW)
    public void addDataResponse(DataResponse response)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".addDataResponse(DataResponse response)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "response" },
                new Object[] { response });
        Helper.checkNull(LOGGER, signature, response, "response");
        Helper.checkNullOrEmpty(LOGGER, signature, response.getRequestId(),
                "response.requestId");
        Helper.checkNull(LOGGER, signature, response.getResponseTimestamp(),
                "response.ResponseTimestamp");
        try {
            EntityManager entityManager = getEntityManager();

            DataRequest request = entityManager.find(DataRequest.class,
                    response.getRequestId());
            if (request == null) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException(
                                "There is no such data request."));
            }
            // Check if the request has expired
            if (response.getResponseTimestamp().after(
                    request.getExpirationTime())) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new DataRequestExpiredException(
                                "Data request has expired."));
            }

            String respondentId = response.getRespondentId();
            // Check if the respondent is on the requested partner list
            if (request.getRequestedPartners() == null
                    || !request.getRequestedPartners().contains(respondentId)) {
                // Log exception
                /*throw Helper.logException(LOGGER, signature,
                        new AuthorizationException(
                                "Respondent isn't in requested user list."));*/
            }

            // Persist DataResponse in database
            entityManager.persist(response);

            String message = null;

            if (!response.isRequestDenied()) {
                String data = response.getData();

                Object cacheKey = getCacheKey(request, respondentId);
                if (data != null) {
                    if (request.isCacheSafe()) {
                        // Cache the response
                        cacheService.put(cacheKey, data);
                    }
                } else {
                    CachedObjectWrapper wrapper = cacheService.get(cacheKey);
                    if (wrapper == null
                            || !(wrapper.getCachedObject() instanceof String)) {
                        // Log exception
                        throw Helper.logException(LOGGER, signature,
                                new NetworkHubServiceException(
                                        "No cached response data."));
                    }
                    response.setData((String) wrapper.getCachedObject());
                }

                // Update user statistics for respondent
                UserStatistics stat = userService
                        .getUserStatistics(respondentId);
                if (stat == null) {
                    // Log exception
                    throw Helper.logException(LOGGER, signature,
                            new EntityNotFoundException(
                                    "No User Statistics found with userId "
                                            + respondentId));
                }
                stat.setDataRequestsResponded(stat.getDataRequestsResponded() + 1);
                entityManager.merge(stat);
            } else if (!ERROR_MESSAGE_WAITING_APPROVAL.equals(response.getErrorMessage())) {
                response.setData("");
                UserStatistics stat = userService
                        .getUserStatistics(respondentId);
                if (stat == null) {
                    // Log exception
                    throw Helper.logException(LOGGER, signature,
                            new EntityNotFoundException(
                                    "No User Statistics found with userId "
                                            + respondentId));
                }
                stat.setDataRequestsDeclined(stat.getDataRequestsDeclined() + 1);
                entityManager.merge(stat);
            }

            // Reroute the DataResponse message to requester
            Template template = velocityEngine
                    .getTemplate(dataResponseTemplatePath);
            VelocityContext context = new VelocityContext();
            context.put("request", request);
            context.put("response", response);
            message = getContent(template, context);

            Destination destination = new AMQAnyDestination("ADDR:"
                    + dataResponseQueuePrefix + request.getRequesterId()
                    + "; {create: never, delete: never}");
            sendMessageToQueue(signature, destination, message, null);

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "The entity manager has been closed.", e));
        } catch (PersistenceException e) {
            // Log exception
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new NetworkHubServiceException(
                                    "An error has occurred while accessing the persistence.",
                                    e));
        } catch (URISyntaxException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException("The URI is invalid.", e));
        } catch (VelocityException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "A velocity error has occurred.", e));
        }
    }

    /**
     * This method is used to deliver analysis result to the network.
     * 
     * @param userId
     *            the user initiates this analysis result delivery
     * @param requestId
     *            the data request ID
     * @param result
     *            the analysis result to deliver
     * 
     * @throws IllegalArgumentException
     *             if userId, requestId or result is null or empty string.
     * @throws EntityNotFoundException
     *             if there is no corresponding request identified requestId
     * @throws AuthorizationException
     *             if the user isn't the requester of the request
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(
            rollbackFor = Exception.class,
            propagation = Propagation.REQUIRES_NEW)
    public void deliverAnalysisResult(String userId, String requestId,
            String result) throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".deliverAnalysisResult(String userId, String requestId, String result)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "userId",
                "requestId" }, new Object[] { userId, requestId });

        Helper.checkNullOrEmpty(LOGGER, signature, userId, "userId");
        Helper.checkNullOrEmpty(LOGGER, signature, requestId, "requestId");
        Helper.checkNullOrEmpty(LOGGER, signature, result, "result");

        try {
            EntityManager entityManager = getEntityManager();

            DataRequest request = entityManager.find(DataRequest.class,
                    requestId);
            if (request == null) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException(
                                "There is no such data request."));
            }
            // Check if the user is the requester for the request
            if (!userId.equals(request.getRequesterId())) {
                // Not authorized to deliver analysis result
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new AuthorizationException(
                                "User isn't the request initiator."));
            }

            // Retrieve non-denied data responses for the request
            TypedQuery<DataResponse> query = entityManager.createQuery(
                    SQL_QUERY_DATA_RESPONSE, DataResponse.class);
            query.setParameter("requestId", requestId);
            query.setParameter("requestDenied", false);
            List<DataResponse> responses = query.getResultList();
            for (DataResponse response : responses) {
                // Send message to the respondent
                Destination destination = new AMQAnyDestination("ADDR:"
                        + analysisResultQueuePrefix
                        + response.getRespondentId()
                        + "; {create: never, delete: never}");
                sendMessageToQueue(signature, destination, result, null);
            }

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "The entity manager has been closed.", e));
        } catch (PersistenceException e) {
            // Log exception
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new NetworkHubServiceException(
                                    "An error has occurred while accessing the persistence.",
                                    e));
        } catch (URISyntaxException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException("The URI is invalid.", e));
        }
    }

    /**
     * Sets the JmsTemplate used to send JMS messages.
     * 
     * @param jmsTemplate
     *            the JmsTemplate used to send JMS messages.
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Sets the CacheService used to access cache.
     * 
     * @param cacheService
     *            the CacheService used to access cache.
     */
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Sets the UserService used to access user from persistence.
     * 
     * @param userService
     *            the UserService used to access user from persistence.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Sets the VelocityEngine used to generate XML messages.
     * 
     * @param velocityEngine
     *            the VelocityEngine used to generate XML messages.
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     * Sets the name prefix of the AMQP queue from which Network Nodes receive
     * analysis result.
     * 
     * @param analysisResultQueuePrefix
     *            the name prefix of the AMQP queue from which Network Nodes
     *            receive analysis result.
     */
    public void setAnalysisResultQueuePrefix(String analysisResultQueuePrefix) {
        this.analysisResultQueuePrefix = analysisResultQueuePrefix;
    }

    /**
     * Sets the name prefix of the AMQP queue from which Network Nodes receive
     * data responses.
     * 
     * @param dataResponseQueuePrefix
     *            the name prefix of the AMQP queue from which Network Nodes
     *            receive data responses.
     */
    public void setDataResponseQueuePrefix(String dataResponseQueuePrefix) {
        this.dataResponseQueuePrefix = dataResponseQueuePrefix;
    }

    /**
     * Sets the name prefix of the AMQP queue from which Network Nodes receive
     * data requests.
     * 
     * @param dataRequestQueuePrefix
     *            the name prefix of the AMQP queue from which Network Nodes
     *            receive data requests.
     */
    public void setDataRequestQueuePrefix(String dataRequestQueuePrefix) {
        this.dataRequestQueuePrefix = dataRequestQueuePrefix;
    }

    /**
     * Sets the file path of the Velocity template that is used to generate Data
     * Response XML message routed to data requester.
     * 
     * @param dataResponseTemplatePath
     *            the file path of the Velocity template that is used to
     *            generate Data Response XML message routed to data requester.
     */
    public void setDataResponseTemplatePath(String dataResponseTemplatePath) {
        this.dataResponseTemplatePath = dataResponseTemplatePath;
    }

    /**
     * Sets the file path of the Velocity template that is used to generate Data
     * Request XML message routed to requested partners.
     * 
     * @param dataRequestTemplatePath
     *            the file path of the Velocity template that is used to
     *            generate Data Request XML message routed to requested
     *            partners.
     */
    public void setDataRequestTemplatePath(String dataRequestTemplatePath) {
        this.dataRequestTemplatePath = dataRequestTemplatePath;
    }

    /**
     * Gets the content.
     * 
     * @param template
     *            the template
     * @param velocityContext
     *            the velocity context
     * 
     * @return the content.
     * 
     * @throws VelocityException
     *             if any error occurs
     */
    private static String getContent(Template template,
            VelocityContext velocityContext) {
        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        return stringWriter.toString();
    }

    /**
     * This method is used to send a message to designated destination queue.
     * 
     * @param signature
     *            the signature
     * @param destination
     *            the destination
     * @param message
     *            the message
     * @param expirationTime
     *            the message's expiration time, it is optional, null means no
     *            expiration.
     * 
     * @throws NetworkHubServiceException
     *             if any error occurs
     */
    private void sendMessageToQueue(String signature,
            final Destination destination, final String message,
            final Date expirationTime) throws NetworkHubServiceException {
        try {
            jmsTemplate.send(destination,
            /**
             * <p>
             * The message creator.
             * </p>
             * 
             * <p>
             * <strong>Thread Safety: </strong> This class is immutable and
             * thread safe.
             * </p>
             * 
             * @author flying2hk, sparemax
             * @version 1.0
             */
            new MessageCreator() {
                /**
                 * Creates the message.
                 * 
                 * @param session
                 *            the session
                 * 
                 * @throws JMSException
                 *             if any error occurs
                 */
                public Message createMessage(Session session)
                        throws JMSException {
                    Message jmsMessage = session.createTextMessage(message);
                    if (expirationTime != null) {
                        jmsMessage.setJMSExpiration(expirationTime.getTime());
                    }
                    return jmsMessage;
                }
            });
        } catch (JmsException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException("The URI is invalid.", e));
        }
    }

    /**
     * This method is used to generate the cache key based on data request and
     * respondent ID.
     * 
     * @param request
     *            the data request
     * @param respondentId
     *            the respondent ID
     *
     * @throws NetworkHubServiceException
     *             if any error occurs
     * 
     * @return the cache key
     */
    private String getCacheKey(DataRequest request, String respondentId) throws NetworkHubServiceException {

        final List<String> file_types = new ArrayList<String>();
        final List<String> logical_expressions = new ArrayList<String>();
        final String signature = CLASS_NAME
                + ".getCacheKey(DataRequest request, String respondentId)";
        
        try {
            JsonParser jsonParser = new JsonFactory().createParser(request.getQuery());
            //loop through the JsonTokens
            while(jsonParser.nextToken() != JsonToken.END_OBJECT){
                String name = jsonParser.getCurrentName();
                if("file_types".equals(name)){
                    jsonParser.nextToken(); // read [
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        file_types.add(jsonParser.getText());
                        
                    }
                } else if("logical_expressions".equals(name)){
                    jsonParser.nextToken(); // read [
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        logical_expressions.add(jsonParser.getText());
                        
                    }
                }
            }

            if(file_types.size() != logical_expressions.size()){
                throw Helper.logException(LOGGER, signature,
                        new NetworkHubServiceException("File_types and logical_expressions do not have same size"));

            }
            
            
            Integer[] idx = new Integer[file_types.size()];
            for( int i = 0 ; i < idx.length; i++ ) idx[i] = i;              
            Arrays.sort(idx, new Comparator<Integer>() {
                public int compare(Integer i1, Integer i2) {                        
                    return file_types.get(i1).compareTo(file_types.get(i2)) ;
                }                   
            });
            
            StringBuffer sb = new StringBuffer(respondentId + ":");
            
            for(int i = 0 ; i < idx.length; i++){
                sb.append("[").append(file_types.get(idx[i])).append(",").append(logical_expressions.get(idx[i])).append("]");
            }
            return sb.toString();
        } catch (JsonParseException e) {
            
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException("Error while parsing the query.", e));
        } catch (IOException e) {
            
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException("Error while reading the query.", e));
        }
        
    }
}
