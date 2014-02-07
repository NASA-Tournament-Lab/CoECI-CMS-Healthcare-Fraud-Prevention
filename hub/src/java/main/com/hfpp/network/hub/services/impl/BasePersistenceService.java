/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import com.hfpp.network.hub.ConfigurationException;

/**
 * <p>
 * This is the base class for all service implementations that access database persistence.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since its internal state isn't expected to
 * change after Spring IoC initialization, and all dependencies are thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public abstract class BasePersistenceService extends BaseService {
    /**
     * Represents the JPA EntityManager used to access database persistence. It should be non-null. It is required.
     */
    private EntityManager entityManager;

    /**
     * Creates an instance of BasePersistenceService.
     */
    protected BasePersistenceService() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException
     *             if any required field is not initialized properly (entityManager is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();

        Helper.checkState(entityManager == null, "'entityManager' can't be null.");
    }

    /**
     * Sets the JPA EntityManager used to access database persistence.
     *
     * @param entityManager
     *            the JPA EntityManager used to access database persistence.
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Gets the JPA EntityManager used to access database persistence.
     *
     * @return the JPA EntityManager used to access database persistence.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
