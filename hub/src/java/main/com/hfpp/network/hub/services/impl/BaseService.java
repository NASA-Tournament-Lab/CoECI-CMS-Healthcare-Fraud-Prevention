/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import javax.annotation.PostConstruct;

import com.hfpp.network.hub.ConfigurationException;

/**
 * <p>
 * This is the base class for all service implementations.
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
public abstract class BaseService {
    /**
     * Creates an instance of BaseService.
     */
    protected BaseService() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException
     *             if any required field is not initialized properly. (not thrown in this method)
     */
    @PostConstruct
    public void checkConfiguration() {
        // Empty
    }
}
