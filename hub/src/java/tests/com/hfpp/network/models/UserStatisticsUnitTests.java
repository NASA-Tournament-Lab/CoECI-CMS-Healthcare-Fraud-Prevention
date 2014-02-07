/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for {@link UserStatistics} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class UserStatisticsUnitTests {
    /**
     * <p>
     * Represents the <code>UserStatistics</code> instance used in tests.
     * </p>
     */
    private UserStatistics instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(UserStatisticsUnitTests.class);
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
        instance = new UserStatistics();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>UserStatistics()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new UserStatistics();

        assertNull("'userId' should be correct.", BaseUnitTests.getField(instance, "userId"));
        assertEquals("'dataRequestsReceived' should be correct.",
            0, BaseUnitTests.getField(instance, "dataRequestsReceived"));
        assertEquals("'dataRequestsResponded' should be correct.",
            0, BaseUnitTests.getField(instance, "dataRequestsResponded"));
        assertEquals("'dataRequestsInitiated' should be correct.",
            0, BaseUnitTests.getField(instance, "dataRequestsInitiated"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getUserId()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getUserId() {
        String value = "new_value";
        instance.setUserId(value);

        assertEquals("'getUserId' should be correct.",
            value, instance.getUserId());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setUserId(String userId)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setUserId() {
        String value = "new_value";
        instance.setUserId(value);

        assertEquals("'setUserId' should be correct.",
            value, BaseUnitTests.getField(instance, "userId"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getDataRequestsReceived()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getDataRequestsReceived() {
        int value = 1;
        instance.setDataRequestsReceived(value);

        assertEquals("'getDataRequestsReceived' should be correct.",
            value, instance.getDataRequestsReceived());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setDataRequestsReceived(int dataRequestsReceived)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setDataRequestsReceived() {
        int value = 1;
        instance.setDataRequestsReceived(value);

        assertEquals("'setDataRequestsReceived' should be correct.",
            value, BaseUnitTests.getField(instance, "dataRequestsReceived"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getDataRequestsResponded()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getDataRequestsResponded() {
        int value = 1;
        instance.setDataRequestsResponded(value);

        assertEquals("'getDataRequestsResponded' should be correct.",
            value, instance.getDataRequestsResponded());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setDataRequestsResponded(int dataRequestsResponded)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setDataRequestsResponded() {
        int value = 1;
        instance.setDataRequestsResponded(value);

        assertEquals("'setDataRequestsResponded' should be correct.",
            value, BaseUnitTests.getField(instance, "dataRequestsResponded"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getDataRequestsInitiated()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getDataRequestsInitiated() {
        int value = 1;
        instance.setDataRequestsInitiated(value);

        assertEquals("'getDataRequestsInitiated' should be correct.",
            value, instance.getDataRequestsInitiated());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setDataRequestsInitiated(int dataRequestsInitiated)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setDataRequestsInitiated() {
        int value = 1;
        instance.setDataRequestsInitiated(value);

        assertEquals("'setDataRequestsInitiated' should be correct.",
            value, BaseUnitTests.getField(instance, "dataRequestsInitiated"));
    }
}