/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.util.Date;

import javax.annotation.PostConstruct;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.CacheService;
import com.hfpp.network.hub.services.EntityNotFoundException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.models.CachedObjectWrapper;

/**
 * <p>
 * This is the implementation of CacheService that makes use of EhCache as the
 * underlying caching facility.
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
public class CacheServiceImpl extends BaseService implements CacheService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = CacheServiceImpl.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the Ehcache instance used for caching. It should be non-null.
     * It is required.
     */
    private Ehcache ehcache;

    /**
     * Creates an instance of CacheServiceImpl.
     */
    public CacheServiceImpl() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly (ehcache is
     *             null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();

        Helper.checkState(ehcache == null, "'ehcache' can't be null.");
    }

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
     * @since 1.1
     */
    @Transactional(rollbackFor = Exception.class)
    public void put(Object key, Object value) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".put(Object key, Object value";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "key", "value" },
                new Object[] { key, value });

        Helper.checkNull(LOGGER, signature, key, "key");
        Helper.checkNull(LOGGER, signature, value, "value");

        try {
            // Put to Ehcache
            ehcache.put(new Element(key, value));

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper
                    .logException(LOGGER, signature,
                            new NetworkHubServiceException(
                                    "The ehcache is invalid.", e));
        } catch (CacheException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "Failed to put an object to cache.", e));
        }
    }

    /**
     * This method is used to retrieve a CachedObjectWrapper.
     * 
     * @param key
     *            the key
     * 
     * @return the CachedObjectWrapper for the key, null will be returned if the
     *         key doesn't exist in the cache
     * 
     * @throws IllegalArgumentException
     *             if key is null
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public CachedObjectWrapper get(Object key)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".get(Object key)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "key" },
                new Object[] { key });

        Helper.checkNull(LOGGER, signature, key, "key");

        try {
            CachedObjectWrapper result = null;

            // Retrieve from Ehcache
            Element element = ehcache.get(key);
            if ((element != null) && !element.isExpired()) {
                result = new CachedObjectWrapper();
                result.setCachedObject(element.getObjectValue());
                result.setTimestamp(new Date(Math.max(
                        element.getLastUpdateTime(), element.getCreationTime())));
            }

            // Log exit
            Helper.logExit(LOGGER, signature, new Object[] { result });
            return result;
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper
                    .logException(LOGGER, signature,
                            new NetworkHubServiceException(
                                    "The ehcache is invalid.", e));
        } catch (CacheException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "Failed to retrieve a CachedObjectWrapper.", e));
        }
    }

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
     * @since 1.1
     */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Object key) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".remove(Object key)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "key" },
                new Object[] { key });

        Helper.checkNull(LOGGER, signature, key, "key");

        try {
            // Remove from Ehcache
            if (!ehcache.remove(key)) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new EntityNotFoundException(
                                "There is no such object in the cache."));
            }

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper
                    .logException(LOGGER, signature,
                            new NetworkHubServiceException(
                                    "The ehcache is invalid.", e));
        } catch (CacheException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "Failed to remove a cache object.", e));
        }
    }

    /**
     * This method is used to clear the entire cache.
     * 
     * @throws NetworkHubServiceException
     *             if any error occurred
     * @since 1.1
     */
    @Transactional(rollbackFor = Exception.class)
    public void clear() throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".clear()";

        // Log entry
        Helper.logEntrance(LOGGER, signature, null, null);

        try {
            // Clear Ehcache
            ehcache.removeAll();

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper
                    .logException(LOGGER, signature,
                            new NetworkHubServiceException(
                                    "The ehcache is invalid.", e));
        } catch (CacheException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "Failed to clear the entire cache.", e));
        }
    }

    /**
     * Sets the Ehcache instance used for caching.
     * 
     * @param ehcache
     *            the Ehcache instance used for caching.
     */
    public void setEhcache(Ehcache ehcache) {
        this.ehcache = ehcache;
    }

}
