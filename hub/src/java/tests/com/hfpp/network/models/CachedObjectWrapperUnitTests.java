/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for {@link CachedObjectWrapper} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class CachedObjectWrapperUnitTests {
    /**
     * <p>
     * Represents the <code>CachedObjectWrapper</code> instance used in tests.
     * </p>
     */
    private CachedObjectWrapper instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CachedObjectWrapperUnitTests.class);
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
        instance = new CachedObjectWrapper();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>CachedObjectWrapper()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new CachedObjectWrapper();

        assertNull("'cachedObject' should be correct.", BaseUnitTests.getField(instance, "cachedObject"));
        assertNull("'timestamp' should be correct.", BaseUnitTests.getField(instance, "timestamp"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getCachedObject()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getCachedObject() {
        Object value = new Object();
        instance.setCachedObject(value);

        assertSame("'getCachedObject' should be correct.",
            value, instance.getCachedObject());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setCachedObject(Object cachedObject)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setCachedObject() {
        Object value = new Object();
        instance.setCachedObject(value);

        assertSame("'setCachedObject' should be correct.",
            value, BaseUnitTests.getField(instance, "cachedObject"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getTimestamp()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getTimestamp() {
        Date value = new Date();
        instance.setTimestamp(value);

        assertSame("'getTimestamp' should be correct.",
            value, instance.getTimestamp());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setTimestamp(Date timestamp)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setTimestamp() {
        Date value = new Date();
        instance.setTimestamp(value);

        assertSame("'setTimestamp' should be correct.",
            value, BaseUnitTests.getField(instance, "timestamp"));
    }
}