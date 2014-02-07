/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.UUID;

import junit.framework.Assert;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.CacheServiceImpl;
import com.hfpp.network.models.CachedObjectWrapper;

/**
 * Functional tests for {@link CacheService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class CacheServiceTest extends BaseTestCase {

    /**
     * Represents the cache
     */
    @Autowired
    private Ehcache cache;

    /**
     * Represents the cache service
     */
    @Autowired
    private CacheService cacheService;

    /**
     * Represents the cache service for test
     */
    private CacheServiceImpl testCacheService;

    /**
     * Prepare cache service for test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testCacheService = new CacheServiceImpl();
        testCacheService.setEhcache(cache);
    }

    /**
     * Accuracy test for CacheServiceImpl#checkConfiguration
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testCacheService.checkConfiguration();
    }

    /**
     * Failure test for CacheServiceImpl#checkConfiguration when Ehcache is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail() throws Exception {
        testCacheService.setEhcache(null);
        testCacheService.checkConfiguration();
    }

    /**
     * Accuracy test for CacheService#put
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testPut() throws Exception {
        String key = UUID.randomUUID().toString();
        Assert.assertNull(cache.get(key));
        String value = "value";
        cacheService.put(key, value);
        Assert.assertNotNull(cache.get(key));
        Assert.assertEquals(value, cache.get(key).getObjectValue());
    }

    /**
     * Failure test for CacheService#put when key is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutFail() throws Exception {
        cacheService.put(null, "value");
    }

    /**
     * Failure test for CacheService#put when value is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutFai2() throws Exception {
        cacheService.put("key", null);
    }

    /**
     * Accuracy test for CacheService#get
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGet() throws Exception {
        String key = UUID.randomUUID().toString();
        Assert.assertNull(cacheService.get(key));
        String value = "value";
        cache.put(new Element(key, value));
        CachedObjectWrapper obj = cacheService.get(key);
        Assert.assertNotNull(obj);
        Assert.assertEquals(value, obj.getCachedObject());
        Element element = cache.get(key);
        Assert.assertTrue(Math.abs(element.getCreationTime()
                - obj.getTimestamp().getTime()) < 5);
        cache.put(new Element(key, "new Value"));
        element = cache.get(key);
        obj = cacheService.get(key);
        Assert.assertTrue(Math.abs(element.getCreationTime()
                - obj.getTimestamp().getTime()) < 5);
        Assert.assertTrue(Math.abs(element.getLastUpdateTime()
                - obj.getTimestamp().getTime()) < 5);
    }

    /**
     * Failure test for CacheService#get when key is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFail() throws Exception {
        cacheService.get(null);
    }

    /**
     * Accuracy test for CacheService#remove
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testRemove() throws Exception {
        String key = UUID.randomUUID().toString();
        cache.put(new Element(key, "ddd"));
        Assert.assertNotNull(cache.get(key));
        cacheService.remove(key);
        Assert.assertNull(cache.get(key));
    }

    /**
     * Failure test for CacheService#remove when key is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveFail1() throws Exception {
        cacheService.remove(null);
    }

    /**
     * Failure test for CacheService#remove when key not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testRemoveFail2() throws Exception {
        cacheService.clear();
        cacheService.remove("not exist key");
    }

    /**
     * Accuracy test for CacheService#clear
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testClear() throws Exception {
        String key = UUID.randomUUID().toString();
        cache.put(new Element(key, "ddd"));
        Assert.assertNotNull(cache.get(key));
        cacheService.clear();
        Assert.assertNull(cache.get(key));
        Assert.assertEquals(0, cache.getSize());
    }

}
