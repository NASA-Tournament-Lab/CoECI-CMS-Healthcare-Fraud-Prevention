/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.util.Date;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class is a wrapper class for an object cached by the CacheService, it holds references to the cached object and
 * the timestamp when the object was cached/updated in cache.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class CachedObjectWrapper {
    /**
     * <p>
     * Represents the cached object.
     * </p>
     */
    private Object cachedObject;
    /**
     * <p>
     * Represents the timestamp when the object was cached/updated in cache.
     * </p>
     */
    private Date timestamp;

    /**
     * Creates an instance of CachedObjectWrapper.
     */
    public CachedObjectWrapper() {
        // Empty
    }

    /**
     * Gets the Represents the cached object.
     *
     * @return the Represents the cached object.
     */
    public Object getCachedObject() {
        return cachedObject;
    }

    /**
     * Sets the Represents the cached object.
     *
     * @param cachedObject
     *            the Represents the cached object.
     */
    public void setCachedObject(Object cachedObject) {
        this.cachedObject = cachedObject;
    }

    /**
     * Gets the Represents the timestamp when the object was cached/updated in cache.
     *
     * @return the Represents the timestamp when the object was cached/updated in cache.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the Represents the timestamp when the object was cached/updated in cache.
     *
     * @param timestamp
     *            the Represents the timestamp when the object was cached/updated in cache.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"cachedObject", "timestamp"},
            new Object[] {cachedObject, timestamp});
    }
}