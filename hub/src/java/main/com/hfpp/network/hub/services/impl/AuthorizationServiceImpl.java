/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.AuthorizationException;
import com.hfpp.network.hub.services.AuthorizationService;
import com.hfpp.network.hub.services.NetworkHubServiceException;

/**
 * <p>
 * This is the implementation of AuthorizationService.
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
 * </ul>
 * </p>
 * 
 * @author flying2hk, sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class AuthorizationServiceImpl extends BasePersistenceService implements
        AuthorizationService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = AuthorizationServiceImpl.class
            .getName();

    /**
     * Represents the string to query role name.
     */
    private static final String SQL_QUERY_ROLE_NAME = "SELECT r.name FROM user u JOIN role r ON u.role_id = r.id"
            + " WHERE u.is_deleted = 0 AND u.id = :userId";

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the permissions map, the key will be operation name and the
     * value is a set of role names (for roles that are permitted to perform the
     * operation). It should be non-null, non-empty map, keys should be non-null
     * non-empty string, values should be non-null non-empty set (each value in
     * the Set must be non-null, non-empty string). It is required.
     */
    private Map<String, Set<String>> permissions;

    /**
     * Creates an instance of AuthorizationServiceImpl.
     */
    public AuthorizationServiceImpl() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (entityManager is null; permissions is null/empty, contains
     *             null/empty key, value or null/empty element in value).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();

        Helper.checkState(permissions == null, "'permissions' can't be null.");
        Helper.checkState(permissions.isEmpty(),
                "'permissions' can't be empty.");
        for (Entry<String, Set<String>> permission : permissions.entrySet()) {
            Helper.checkState(Helper.isNullOrEmpty(permission.getKey()),
                    "'permissions' can't contain null/empty key.");

            Set<String> value = permission.getValue();
            Helper.checkCollection(value, "value of permissions");
        }
    }

    /**
     * This method is used to check if a user is authorized to perform an
     * operation.
     * 
     * @param userId
     *            the user ID
     * @param operation
     *            the operation name
     * 
     * @throws IllegalArgumentException
     *             if userId or operation is null or empty
     * @throws AuthorizationException
     *             if the user isn't authorized to perform the operation
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public void checkAuthorization(String userId, String operation)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".checkAuthorization(String userId, String operation)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "userId",
                "operation" }, new Object[] { userId, "***operation**" });

        Helper.checkNullOrEmpty(LOGGER, signature, userId, "userId");
        Helper.checkNullOrEmpty(LOGGER, signature, operation, "operation");
        try {
            // Get role for the user
            EntityManager entityManager = getEntityManager();
            Query query = entityManager.createNativeQuery(SQL_QUERY_ROLE_NAME);
            query.setParameter("userId", userId);
            List<String> list = query.getResultList();

            // Check permission
            Set<String> permittedRoles = permissions.get(operation);
            if (permittedRoles == null) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new NetworkHubServiceException("Unknow operation :"
                                + operation));

            } else if (list.isEmpty()
                    || (!permittedRoles.contains(list.get(0)))) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new AuthorizationException("User " + userId
                                + " isn't authorized to perform operation "
                                + operation));
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
     * Sets the permissions map, the key will be operation name and the value is
     * a set of role names (for roles that are permitted to perform the
     * operation).
     * 
     * @param permissions
     *            the permissions map, the key will be operation name and the
     *            value is a set of role names (for roles that are permitted to
     *            perform the operation).
     */
    public void setPermissions(Map<String, Set<String>> permissions) {
        this.permissions = permissions;
    }

}
