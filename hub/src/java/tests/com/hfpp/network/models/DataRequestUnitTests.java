/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for {@link DataRequest} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class DataRequestUnitTests {
    /**
     * <p>
     * Represents the <code>DataRequest</code> instance used in tests.
     * </p>
     */
    private DataRequest instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DataRequestUnitTests.class);
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
        instance = new DataRequest();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>DataRequest()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new DataRequest();

        assertNull("'requesterId' should be correct.", BaseUnitTests.getField(instance, "requesterId"));
        assertNull("'originalRequesterId' should be correct.", BaseUnitTests.getField(instance, "originalRequesterId"));
        assertNull("'query' should be correct.", BaseUnitTests.getField(instance, "query"));
        assertNull("'requestedPartners' should be correct.", BaseUnitTests.getField(instance, "requestedPartners"));
        assertNull("'expirationTime' should be correct.", BaseUnitTests.getField(instance, "expirationTime"));
        assertFalse("'cacheSafe' should be correct.", (Boolean) BaseUnitTests.getField(instance, "cacheSafe"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getRequesterId()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getRequesterId() {
        String value = "new_value";
        instance.setRequesterId(value);

        assertEquals("'getRequesterId' should be correct.",
            value, instance.getRequesterId());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRequesterId(String requesterId)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRequesterId() {
        String value = "new_value";
        instance.setRequesterId(value);

        assertEquals("'setRequesterId' should be correct.",
            value, BaseUnitTests.getField(instance, "requesterId"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getOriginalRequesterId()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getOriginalRequesterId() {
        String value = "new_value";
        instance.setOriginalRequesterId(value);

        assertEquals("'getOriginalRequesterId' should be correct.",
            value, instance.getOriginalRequesterId());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setOriginalRequesterId(String originalRequesterId)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setOriginalRequesterId() {
        String value = "new_value";
        instance.setOriginalRequesterId(value);

        assertEquals("'setOriginalRequesterId' should be correct.",
            value, BaseUnitTests.getField(instance, "originalRequesterId"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getQuery()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getQuery() {
        String value = "new_value";
        instance.setQuery(value);

        assertEquals("'getQuery' should be correct.",
            value, instance.getQuery());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setQuery(String query)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setQuery() {
        String value = "new_value";
        instance.setQuery(value);

        assertEquals("'setQuery' should be correct.",
            value, BaseUnitTests.getField(instance, "query"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getRequestedPartners()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getRequestedPartners() {
        List<String> value = new ArrayList<String>();
        instance.setRequestedPartners(value);

        assertSame("'getRequestedPartners' should be correct.",
            value, instance.getRequestedPartners());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRequestedPartners(List&lt;String&gt; requestedPartners)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRequestedPartners() {
        List<String> value = new ArrayList<String>();
        instance.setRequestedPartners(value);

        assertSame("'setRequestedPartners' should be correct.",
            value, BaseUnitTests.getField(instance, "requestedPartners"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getExpirationTime()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getExpirationTime() {
        Date value = new Date();
        instance.setExpirationTime(value);

        assertSame("'getExpirationTime' should be correct.",
            value, instance.getExpirationTime());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setExpirationTime(Date expirationTime)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setExpirationTime() {
        Date value = new Date();
        instance.setExpirationTime(value);

        assertSame("'setExpirationTime' should be correct.",
            value, BaseUnitTests.getField(instance, "expirationTime"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>isCacheSafe()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_isCacheSafe() {
        boolean value = true;
        instance.setCacheSafe(value);

        assertTrue("'isCacheSafe' should be correct.", instance.isCacheSafe());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setCacheSafe(boolean cacheSafe)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setCacheSafe() {
        boolean value = true;
        instance.setCacheSafe(value);

        assertTrue("'setCacheSafe' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "cacheSafe"));
    }
}