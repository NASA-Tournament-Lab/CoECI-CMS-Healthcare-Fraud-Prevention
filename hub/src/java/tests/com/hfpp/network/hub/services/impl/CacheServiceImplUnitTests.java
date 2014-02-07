/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;
import com.hfpp.network.hub.services.CacheService;
import com.hfpp.network.models.CachedObjectWrapper;

/**
 * <p>
 * Unit tests for {@link CacheServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get cache service by spring context from base class in test_Spring</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class CacheServiceImplUnitTests {
    /**
     * <p>
     * Represents the <code>CacheServiceImpl</code> instance used in tests.
     * </p>
     */
    private CacheServiceImpl instance;

    /**
     * <p>
     * Represents ehcache used in tests.
     * </p>
     */
    private Ehcache ehcache;

    /**
     * <p>
     * Represents key used in tests.
     * </p>
     */
    private Object key = "key1";

    /**
     * <p>
     * Represents value used in tests.
     * </p>
     */
    private Object value = "value1";

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     * 
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CacheServiceImplUnitTests.class);
    }

    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Before
    public void setUp() throws Exception {
        instance = new CacheServiceImpl();

        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.addCacheIfAbsent("cache1");
        ehcache = cacheManager.getEhcache("cache1");
        ehcache.removeAll();

        instance.setEhcache(ehcache);

        instance.checkConfiguration();
    }

    /**
     * <p>
     * Cleans up the unit tests.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @After
    public void tearDown() throws Exception {
        CacheManager.getInstance().removeCache("cache1");
    }

    /**
     * <p>
     * Accuracy test with Spring.<br>
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     * @since 1.1
     */
    @SuppressWarnings("unused")
    @Test
    public void test_Spring() throws Exception {
        // Get service
        CacheService service = (CacheService) BaseUnitTests.APP_CONTEXT
                .getBean("cacheService");
        service.clear();
        service.put(key, value);
        service.put("key2", "value2");

        CachedObjectWrapper cachedObjectWrapper = service.get(key);

        service.remove(key);
        service.clear();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>CacheServiceImpl()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new CacheServiceImpl();

        assertNull("'ehcache' should be correct.",
                BaseUnitTests.getField(instance, "ehcache"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>put(Object key, Object value)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_put() throws Exception {
        instance.put(key, value);

        Element element = ehcache.get(key);

        assertEquals("'put' should be correct.", key, element.getObjectKey());
        assertEquals("'put' should be correct.", value,
                element.getObjectValue());
    }

    /**
     * <p>
     * Accuracy test for the method <code>get(Object key)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_get1() throws Exception {
        CachedObjectWrapper res = instance.get("not_exist");

        assertNull("'get' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method <code>get(Object key)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_get2() throws Exception {
        instance.put(key, value);

        CachedObjectWrapper res = instance.get(key);

        assertEquals("'get' should be correct.", value, res.getCachedObject());
        assertNotNull("'get' should be correct.", res.getTimestamp());
    }

    /**
     * <p>
     * Accuracy test for the method <code>remove(Object key)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_remove() throws Exception {
        instance.put(key, value);

        instance.remove(key);

        CachedObjectWrapper res = instance.get(key);

        assertNull("'remove' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method <code>clear()</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_clear() throws Exception {
        instance.put(key, value);
        instance.put("key2", "value2");

        instance.clear();

        CachedObjectWrapper res = instance.get(key);
        assertNull("'clear' should be correct.", res);

        res = instance.get("key2");
        assertNull("'clear' should be correct.", res);
    }
}