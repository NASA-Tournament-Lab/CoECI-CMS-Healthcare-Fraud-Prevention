/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import com.hfpp.network.models.CachedObjectWrapper;

/**
 * <p>
 * The CacheService is used to access cache.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface CacheService {
    /**
     * This method is used to put an object to cache.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     *
     * @throws IllegalArgumentException
     *             if key or value is null
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void put(Object key, Object value) throws NetworkHubServiceException;

    /**
     * This method is used to retrieve a CachedObjectWrapper.
     *
     * @param key
     *            the key
     *
     * @return the CachedObjectWrapper for the key, null will be returned if the key doesn't exist in the cache
     *
     * @throws IllegalArgumentException
     *             if key is null
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public CachedObjectWrapper get(Object key) throws NetworkHubServiceException;

    /**
     * This method is used to remove a cache object.
     *
     * @param key
     *            the key
     *
     * @throws IllegalArgumentException
     *             if key is null
     * @throws EntityNotFoundException
     *             if there is no such object in the cache
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void remove(Object key) throws NetworkHubServiceException;

    /**
     * This method is used to clear the entire cache.
     *
     * @throws NetworkHubServiceException
     *             if any error occurred
     */
    public void clear() throws NetworkHubServiceException;
}
