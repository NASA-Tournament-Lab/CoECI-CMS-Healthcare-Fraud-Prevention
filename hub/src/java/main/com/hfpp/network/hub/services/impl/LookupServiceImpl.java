/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.hfpp.network.hub.services.LookupService;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.models.Role;

/**
 * <p>
 * This is the implementation of LookupService.
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
public class LookupServiceImpl extends BasePersistenceService implements
        LookupService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = LookupServiceImpl.class.getName();

    /**
     * Represents the string to query role.
     */
    private static final String JPQL_QUERY_ROLE = "SELECT r FROM Role r";

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Creates an instance of LookupServiceImpl.
     */
    public LookupServiceImpl() {
        // Empty
    }

    /**
     * This method is used to get all roles.
     * 
     * @return all roles, empty list will be returned if no roles.
     * 
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public List<Role> getAllRoles() throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".getAllRoles()";

        // Log entry
        Helper.logEntrance(LOGGER, signature, null, null);

        try {
            EntityManager entityManager = getEntityManager();
            TypedQuery<Role> query = entityManager.createQuery(JPQL_QUERY_ROLE,
                    Role.class);
            List<Role> result = query.getResultList();

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
}
