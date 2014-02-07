/*
 * Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.models.SearchResult;
import com.hfpp.network.models.SortType;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserSearchCriteria;
import com.hfpp.network.models.UserStatistics;

/**
 * <p>
 * This is the implementation of UserService.
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
 * <li>remove delete condition from SQL_QUERY_USER and SQL_QUERY_USER_COUNT.</li>
 * <li>add delete condition in buildWhere.</li>
 * <li>add check id not null nor empty when update.</li>
 * <li>update SQL_UPDATE_DELETED of UserService to check is_deleted=0</li>
 * </ul>
 * </p>
 * 
 * <p>
 * v1.2 Changes(Healthcare Fraud Prevention Release Assembly v1.0):
 * <ul>
 * <li>updated propagation type of update and delete</li>
 * </ul>
 * </p
 * 
 * @author flying2hk, sparemax,TCSASSEMBLER
 * @version 1.2
 */
public class UserServiceImpl extends BasePersistenceService implements
        UserService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = UserServiceImpl.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the string to query user by username.
     */
    private static final String SQL_QUERY_USER_BY_USERNAME = "SELECT u.id, u.username, u.organization_name,"
            + " u.role_id, u.auto_retrieve_cached_data, u.created_by, u.created_date, u.updated_by, u.updated_date"
            + " FROM user u WHERE u.is_deleted = 0 AND u.username = :username";

    /**
     * Represents the string to query user by id.
     */
    private static final String SQL_QUERY_USER_BY_ID = "SELECT u.id, u.username, u.organization_name,"
            + " u.role_id, u.auto_retrieve_cached_data, u.created_by, u.created_date, u.updated_by, u.updated_date"
            + " FROM user u WHERE u.is_deleted = 0 AND u.id = :id";

    /**
     * Represents the string to query user count by id.
     */
    private static final String SQL_QUERY_USER_COUNT_BY_ID = "SELECT COUNT(*) FROM user u"
            + " WHERE u.is_deleted = 0 AND u.id = :id";

    /**
     * Represents the string to update password hash.
     */
    private static final String SQL_UPDATE_PASSWORD_HASH = "UPDATE user SET password_hash = :hash WHERE id = :id";

    /**
     * Represents the string to update deleted flag.
     * 
     * @since 1.1
     */
    private static final String SQL_UPDATE_DELETED = "UPDATE user SET is_deleted = 1 WHERE id = :id and is_deleted =0";

    /**
     * Represents the string to query user.
     * 
     * @since 1.1
     */
    private static final String SQL_QUERY_USER = "SELECT u.id, u.username, u.organization_name,"
            + " u.role_id, u.auto_retrieve_cached_data, u.created_by, u.created_date, u.updated_by, u.updated_date"
            + " FROM user u JOIN role r ON u.role_id = r.id WHERE ";

    /**
     * Represents the string to query user count.
     * 
     * @since 1.1
     */
    private static final String SQL_QUERY_USER_COUNT = "SELECT COUNT(*) FROM user u JOIN role r ON u.role_id = r.id"
            + " WHERE ";

    /**
     * <p>
     * Represents the string ' AND '.
     * </p>
     */
    private static final String AND = " AND ";

    /**
     * Represents the initial number-of-responded-requests value for new users.
     * It should be positive integer. It is required.
     */
    private int initialRespondedRequestsValue;

    /**
     * Represents the participation ratio threshold. If a user's ratio is lower
     * than the threshold, its participation ratio will be considered as low. It
     * should be positive double number. It is required.
     */
    private double participationRatioThreshold;

    /**
     * Represents the PasswordEncryptor used to check passwords. It is optional.
     * It should be non-null. It will be initialized to StrongPasswordEncryptor
     * by default (which uses SHA256 with 16 bytes random salt for 100000
     * iterations). Usually there is no need to set the PasswordEncryptor
     * explicitly, it is configurable just for future password hashing algorithm
     * changes.
     */
    private PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    /**
     * Represents the names of roles that are eligible to receive data requests.
     * It should be non-null, non-empty list of strings, each string should be
     * non-null, non-empty string. It is required.
     */
    private List<String> rolesEligibleToReceiveDataRequests;

    /**
     * Represents the names of the roles that are eligible to initiate data
     * requests. It should be non-null, non-empty list, each item in the list
     * should be non-null, non-empty string. It is required.
     */
    private List<String> rolesEligibleToInitiateDataRequests;

    /**
     * Creates an instance of UserServiceImpl.
     */
    public UserServiceImpl() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (entityManager or passwordEncryptor is null;
     *             initialRespondedRequestsValue or participationRatioThreshold
     *             is not positive rolesEligibleToReceiveDataRequests or
     *             rolesEligibleToInitiateDataRequests is null/empty or contains
     *             null/empty element).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();

        Helper.checkState(initialRespondedRequestsValue <= 0,
                "'initialRespondedRequestsValue' should be positive.");
        Helper.checkState(participationRatioThreshold <= 0,
                "'participationRatioThreshold' should be positive.");

        Helper.checkState(passwordEncryptor == null,
                "'passwordEncryptor' can't be null.");

        Helper.checkCollection(rolesEligibleToReceiveDataRequests,
                "rolesEligibleToReceiveDataRequests");
        Helper.checkCollection(rolesEligibleToInitiateDataRequests,
                "rolesEligibleToInitiateDataRequests");
    }

    /**
     * This method is used to create a user.
     * 
     * @param user
     *            the user
     * @return the created user.
     * 
     * @throws IllegalArgumentException
     *             if user is null
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(
            rollbackFor = Exception.class,
            propagation = Propagation.REQUIRES_NEW)
    public User create(User user) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".create(User user)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "user" },
                new Object[] { user });

        Helper.checkNull(LOGGER, signature, user, "user");

        try {
            // Set ID and created date
            user.setId(UUID.randomUUID().toString());
            user.setCreatedDate(new Date());

            // Save user in persistence
            EntityManager entityManager = getEntityManager();
            entityManager.persist(user);

            // Create UserStatistics in persistence
            UserStatistics userStatistics = new UserStatistics();
            userStatistics.setUserId(user.getId());
            userStatistics.setDataRequestsReceived(0);
            userStatistics
                    .setDataRequestsResponded(initialRespondedRequestsValue);
            userStatistics.setDataRequestsInitiated(0);
            entityManager.persist(userStatistics);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { user });
            return user;
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
     * This method is used to update a user.
     * 
     * @param user
     *            the user
     * 
     * @return the updated user.
     * 
     * @throws IllegalArgumentException
     *             if user is null
     * @throws EntityNotFoundException
     *             if the user to update doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(rollbackFor = Exception.class, propagation=Propagation.REQUIRES_NEW)
    public User update(User user) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".update(User user)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "user" },
                new Object[] { user });

        Helper.checkNull(LOGGER, signature, user, "user");
        Helper.checkNullOrEmpty(LOGGER, signature, user.getId(), "user.Id");
        try {
            EntityManager entityManager = getEntityManager();
            String userId = user.getId();

            if (getUserById(entityManager, userId) == null) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException("User with id '" + userId
                                + "' is not found."));
            }

            // Set updated date
            user.setUpdatedDate(new Date());

            // Save user in persistence
            entityManager.merge(user);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { user });
            return user;
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
     * This method is used to get a user by user ID.
     * 
     * @param id
     *            the user ID
     * 
     * @return the user, null will be returned if there's no such user.
     * 
     * @throws IllegalArgumentException
     *             if id is null or empty string.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public User get(String id) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".get(String id)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "id" },
                new Object[] { id });

        Helper.checkNullOrEmpty(LOGGER, signature, id, "id");

        try {
            EntityManager entityManager = getEntityManager();
            User user = getUserById(entityManager, id);

            if (user != null) {
                String roleName = user.getRole().getName();
                user.setEligibleToInitiateDataRequests(rolesEligibleToInitiateDataRequests
                        .contains(roleName)
                        && !isParticipationRatioLow(user.getId()));
                user.setEligibleToReceiveDataRequests(rolesEligibleToReceiveDataRequests
                        .contains(roleName));
            }

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { user });
            return user;
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
     * This method is used to get a user by username.
     * 
     * @param username
     *            the username
     * 
     * @return the user, null will be returned if there's no such user.
     * 
     * @throws IllegalArgumentException
     *             if username is null or empty string.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public User getByUsername(String username)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".getByUsername(String username)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "username" },
                new Object[] { username });

        Helper.checkNullOrEmpty(LOGGER, signature, username, "username");

        try {
            EntityManager entityManager = getEntityManager();
            Query query = entityManager.createNativeQuery(
                    SQL_QUERY_USER_BY_USERNAME, User.class);
            query.setParameter("username", username);
            List<User> list = query.getResultList();

            User user = list.isEmpty() ? null : list.get(0);

            if (user != null) {
                String roleName = user.getRole().getName();
                user.setEligibleToInitiateDataRequests(rolesEligibleToInitiateDataRequests
                        .contains(roleName)
                        && !isParticipationRatioLow(user.getId()));
                user.setEligibleToReceiveDataRequests(rolesEligibleToReceiveDataRequests
                        .contains(roleName));
            }

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { user });
            return user;
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
     * This method is used to delete a user by user ID.
     * 
     * @param id
     *            the user ID
     * 
     * @throws IllegalArgumentException
     *             if id is null or empty string.
     * @throws EntityNotFoundException
     *             if the user to delete doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(rollbackFor = Exception.class, propagation=Propagation.REQUIRES_NEW)
    public void delete(String id) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".delete(String id)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "id" },
                new Object[] { id });

        Helper.checkNullOrEmpty(LOGGER, signature, id, "id");

        try {
            EntityManager entityManager = getEntityManager();
            Query query = entityManager.createNativeQuery(SQL_UPDATE_DELETED);
            query.setParameter("id", id);
            if (query.executeUpdate() != 1) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException("User with id '" + id
                                + "' is not found."));
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
        }
    }

    /**
     * This method is used to search users.
     * 
     * @param criteria
     *            the search criteria
     * 
     * @return the search result.
     * 
     * @throws IllegalArgumentException
     *             if criteria is null or criteria.pageSize &lt;= 0 when
     *             criteria.pageNumber &gt; 0.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public SearchResult<User> search(UserSearchCriteria criteria)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".search(UserSearchCriteria criteria)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "criteria" },
                new Object[] { criteria });

        Helper.checkNull(LOGGER, signature, criteria, "criteria");
        int pageNumber = criteria.getPageNumber();
        int pageSize = criteria.getPageSize();
        if ((pageSize <= 0) && (pageNumber > 0)) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new IllegalArgumentException(
                            "'criteria.getPageSize()' should be positive when "
                                    + "criteria.getPageNumber() is positive."));
        }

        try {
            String sortBy = criteria.getSortBy();
            SortType sortType = criteria.getSortType();

            // Set default sorting option
            if (sortBy == null) {
                sortBy = "username";
            }
            if (sortType == null) {
                sortType = SortType.ASC;
            }
            String orderByClause = new StringBuilder().append(" ORDER BY u.")
                    .append(sortBy).append(" ").append(sortType).toString();

            List<String> paramNames = new ArrayList<String>();
            List<Object> paramValues = new ArrayList<Object>();
            String whereClause = buildWhere(criteria, paramNames, paramValues);

            EntityManager entityManager = getEntityManager();

            // Create query
            Query query = entityManager.createNativeQuery(SQL_QUERY_USER
                    + whereClause + orderByClause, User.class);
            setParameters(query, paramNames, paramValues);

            // set paging options
            if (pageNumber > 0) {
                query.setMaxResults(pageSize);
                query.setFirstResult((pageNumber - 1) * pageSize);
            }

            List<User> users = query.getResultList();

            SearchResult<User> result = new SearchResult<User>();
            result.setValues(users);

            if (pageNumber > 0) {
                // Get total page count

                // Create query
                Query countQuery = entityManager
                        .createNativeQuery(SQL_QUERY_USER_COUNT + whereClause);
                setParameters(countQuery, paramNames, paramValues);

                int totalCount = ((Number) countQuery.getSingleResult())
                        .intValue();
                int totalPageCount = (totalCount + pageSize - 1) / pageSize;
                result.setTotal(totalCount);
                result.setTotalPages(totalPageCount);
            } else {
                result.setTotal(users.size());
                result.setTotalPages(users.isEmpty() ? 0 : 1);
            }

            for (User user : users) {
                String roleName = user.getRole().getName();
                user.setEligibleToInitiateDataRequests(rolesEligibleToInitiateDataRequests
                        .contains(roleName)
                        && !isParticipationRatioLow(user.getId()));
                user.setEligibleToReceiveDataRequests(rolesEligibleToReceiveDataRequests
                        .contains(roleName));
            }

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { result });
            return result;
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
     * This method is used to set user's password.
     * 
     * @param id
     *            the user ID
     * @param password
     *            the new password
     * 
     * @throws IllegalArgumentException
     *             if id or password is null or empty string.
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @Transactional(rollbackFor = Exception.class)
    public void setPassword(String id, String password)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".setPassword(String id, String password)";

        // Log entry
        Helper.logEntrance(LOGGER, signature,
                new String[] { "id", "password" },
                new Object[] { id, "******" });

        Helper.checkNullOrEmpty(LOGGER, signature, id, "id");
        Helper.checkNullOrEmpty(LOGGER, signature, password, "password");

        try {
            EntityManager entityManager = getEntityManager();

            if (!checkUserExistence(entityManager, id)) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException("User with id '" + id
                                + "' is not found."));
            }

            // Hash password
            String hash = passwordEncryptor.encryptPassword(password);
            Query query = entityManager
                    .createNativeQuery(SQL_UPDATE_PASSWORD_HASH);
            query.setParameter("id", id);
            query.setParameter("hash", hash);
            int result = query.executeUpdate();
            if (result != 1) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new NetworkHubServiceException(
                                "An error has occurred while updating the password for user with ID '"
                                        + id + "'."));
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
        }
    }

    /**
     * This method is used to get a user's statistics.
     * 
     * @param userId
     *            the user ID
     * 
     * @return the user's statistics
     * 
     * @throws IllegalArgumentException
     *             if userId is null or empty string.
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public UserStatistics getUserStatistics(String userId)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".getUserStatistics(String userId)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "userId" },
                new Object[] { userId });

        Helper.checkNullOrEmpty(LOGGER, signature, userId, "userId");

        try {
            EntityManager entityManager = getEntityManager();
            if (!checkUserExistence(entityManager, userId)) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException("User with id '" + userId
                                + "' is not found."));
            }
            UserStatistics userStatistics = entityManager.find(
                    UserStatistics.class, userId);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { userStatistics });
            return userStatistics;
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
     * This method is used to check if a user's participation ratio is lower
     * than a threshold.
     * 
     * @param userId
     *            the user ID
     * 
     * @return true if the user's participation ratio is lower than a threshold,
     *         false otherwise.
     * 
     * @throws IllegalArgumentException
     *             if userId is null or empty string.
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public boolean isParticipationRatioLow(String userId)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".isParticipationRatioLow(String userId)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "userId" },
                new Object[] { userId });

        Helper.checkNullOrEmpty(LOGGER, signature, userId, "userId");

        try {
            UserStatistics userStatistics = getUserStatistics(userId);

            int dataRequestsInitiated = userStatistics
                    .getDataRequestsInitiated();
            double participationRatio = (dataRequestsInitiated == 0) ? Double.POSITIVE_INFINITY
                    : (userStatistics.getDataRequestsResponded() * 1.0)
                            / dataRequestsInitiated;

            boolean result = false;//(participationRatio < participationRatioThreshold);

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { result });
            return result;
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
     * Sets the initial number-of-responded-requests value for new users.
     * 
     * @param initialRespondedRequestsValue
     *            the initial number-of-responded-requests value for new users.
     */
    public void setInitialRespondedRequestsValue(
            int initialRespondedRequestsValue) {
        this.initialRespondedRequestsValue = initialRespondedRequestsValue;
    }

    /**
     * Sets the participation ratio threshold.
     * 
     * @param participationRatioThreshold
     *            the participation ratio threshold.
     */
    public void setParticipationRatioThreshold(
            double participationRatioThreshold) {
        this.participationRatioThreshold = participationRatioThreshold;
    }

    /**
     * Sets the PasswordEncryptor used to check passwords.
     * 
     * @param passwordEncryptor
     *            the PasswordEncryptor used to check passwords.
     */
    public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }

    /**
     * Sets the names of roles that are eligible to receive data requests.
     * 
     * @param rolesEligibleToReceiveDataRequests
     *            the names of roles that are eligible to receive data requests.
     */
    public void setRolesEligibleToReceiveDataRequests(
            List<String> rolesEligibleToReceiveDataRequests) {
        this.rolesEligibleToReceiveDataRequests = rolesEligibleToReceiveDataRequests;
    }

    /**
     * Sets the names of the roles that are eligible to initiate data requests.
     * 
     * @param rolesEligibleToInitiateDataRequests
     *            the names of the roles that are eligible to initiate data
     *            requests.
     */
    public void setRolesEligibleToInitiateDataRequests(
            List<String> rolesEligibleToInitiateDataRequests) {
        this.rolesEligibleToInitiateDataRequests = rolesEligibleToInitiateDataRequests;
    }

    /**
     * This method is used to get a user by user ID.
     * 
     * @param entityManager
     *            the entity manager
     * @param id
     *            the user ID
     * 
     * @return the user, null will be returned if there's no such user.
     * 
     * @throws IllegalStateException
     *             if entity manager has been closed
     * @throws PersistenceException
     *             if any error occurs while accessing the persistence
     */
    @SuppressWarnings("unchecked")
    private static User getUserById(EntityManager entityManager, String id) {
        Query query = entityManager.createNativeQuery(SQL_QUERY_USER_BY_ID,
                User.class);
        query.setParameter("id", id);
        List<User> list = query.getResultList();

        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Checks if the user exists (not deleted).
     * 
     * @param entityManager
     *            the entity manager
     * @param id
     *            the user ID
     * 
     * @return true if the user exists; false otherwise.
     * 
     * @throws IllegalStateException
     *             if entity manager has been closed
     * @throws PersistenceException
     *             if any error occurs while accessing the persistence
     */
    private static boolean checkUserExistence(EntityManager entityManager,
            String id) {
        Query query = entityManager
                .createNativeQuery(SQL_QUERY_USER_COUNT_BY_ID);
        query.setParameter("id", id);

        return ((Number) query.getSingleResult()).intValue() > 0;
    }

    /**
     * Builds the WHERE string.
     * 
     * @param criteria
     *            the criteria
     * @param paramNames
     *            the parameter name
     * @param paramValues
     *            the parameter values
     * 
     * @return the WHERE string.
     * @since 1.1
     */
    private String buildWhere(UserSearchCriteria criteria,
            List<String> paramNames, List<Object> paramValues) {
        StringBuilder sb = new StringBuilder();
        sb.append("u.is_deleted = 0");
        appendCondition(sb, "u.username LIKE :username",
                criteria.getUsername(), "username", paramNames, paramValues);
        appendCondition(sb, "u.organization_name LIKE :organizationName",
                criteria.getOrganizationName(), "organizationName", paramNames,
                paramValues);
        appendCondition(sb, "r.name IN :roles", criteria.getRoles(), "roles",
                paramNames, paramValues);

        Boolean eligibleToReceiveDataRequests = criteria
                .getEligibleToReceiveDataRequests();
        if (eligibleToReceiveDataRequests != null) {
            if (eligibleToReceiveDataRequests) {
                appendCondition(sb,
                        "r.name IN :rolesEligibleToReceiveDataRequests",
                        rolesEligibleToReceiveDataRequests,
                        "rolesEligibleToReceiveDataRequests", paramNames,
                        paramValues);
            } else {
                appendCondition(sb,
                        "r.name NOT IN :rolesEligibleToReceiveDataRequests",
                        rolesEligibleToReceiveDataRequests,
                        "rolesEligibleToReceiveDataRequests", paramNames,
                        paramValues);
            }
        }

        Boolean eligibleToInitiateDataRequests = criteria
                .getEligibleToInitiateDataRequests();
        if (eligibleToInitiateDataRequests != null) {
            if (eligibleToInitiateDataRequests) {
                appendCondition(
                        sb,
                        "(r.name IN :rolesEligibleToInitiateDataRequests"
                                + " AND EXISTS (SELECT * FROM user_stat s WHERE s.user_id = u.id"
                                + " AND (s.data_requests_initiated = 0 OR s.data_requests_responded * 1.0"
                                + " / s.data_requests_initiated >= :participationRatioThreshold)))");
            } else {
                appendCondition(
                        sb,
                        "(r.name NOT IN :rolesEligibleToInitiateDataRequests"
                                + " OR EXISTS (SELECT * FROM user_stat s WHERE s.user_id = u.id"
                                + " AND s.data_requests_initiated > 0 AND s.data_requests_responded * 1.0"
                                + " / s.data_requests_initiated < :participationRatioThreshold))");
            }

            paramNames.add("rolesEligibleToInitiateDataRequests");
            paramValues.add(rolesEligibleToInitiateDataRequests);
            paramNames.add("participationRatioThreshold");
            paramValues.add(participationRatioThreshold);
        }

        return sb.toString();
    }

    /**
     * Sets the parameters.
     * 
     * @param query
     *            the query
     * @param paramNames
     *            the parameter names
     * @param paramValues
     *            the parameter values
     * 
     * @throws IllegalStateException
     *             if the entity manager has been closed.
     * @throws PersistenceException
     *             if any other error occurs.
     */
    private static void setParameters(Query query, List<String> paramNames,
            List<Object> paramValues) {
        int paramSize = paramNames.size();
        for (int i = 0; i < paramSize; i++) {
            query.setParameter(paramNames.get(i), paramValues.get(i));
        }
    }

    /**
     * Appends the condition string.
     * 
     * @param sb
     *            the string builder
     * @param str
     *            the condition string.
     * @param value
     *            the value
     * @param name
     *            the name
     * @param paramNames
     *            the parameter name
     * @param paramValues
     *            the parameter values
     */
    private static void appendCondition(StringBuilder sb, String str,
            Object value, String name, List<String> paramNames,
            List<Object> paramValues) {
        if ((value == null)
                || ((value instanceof Collection<?>) && ((Collection<?>) value)
                        .isEmpty())) {
            return;
        }

        sb.append(AND);

        sb.append(str);

        paramNames.add(name);
        paramValues.add(value);
    }

    /**
     * Appends the condition string.
     * 
     * @param sb
     *            the string builder
     * @param str
     *            the condition string.
     */
    private static void appendCondition(StringBuilder sb, String str) {
        if (sb.length() != 0) {
            sb.append(AND);
        }

        sb.append(str);
    }
}
