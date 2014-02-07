/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for {@link DataResponse} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class DataResponseUnitTests {
    /**
     * <p>
     * Represents the <code>DataResponse</code> instance used in tests.
     * </p>
     */
    private DataResponse instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DataResponseUnitTests.class);
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
        instance = new DataResponse();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>DataResponse()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new DataResponse();

        assertNull("'requestId' should be correct.", BaseUnitTests.getField(instance, "requestId"));
        assertNull("'respondentId' should be correct.", BaseUnitTests.getField(instance, "respondentId"));
        assertNull("'responseTimestamp' should be correct.", BaseUnitTests.getField(instance, "responseTimestamp"));
        assertNull("'data' should be correct.", BaseUnitTests.getField(instance, "data"));
        assertFalse("'requestDenied' should be correct.", (Boolean) BaseUnitTests.getField(instance, "requestDenied"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getRequestId()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getRequestId() {
        String value = "new_value";
        instance.setRequestId(value);

        assertEquals("'getRequestId' should be correct.",
            value, instance.getRequestId());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRequestId(String requestId)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRequestId() {
        String value = "new_value";
        instance.setRequestId(value);

        assertEquals("'setRequestId' should be correct.",
            value, BaseUnitTests.getField(instance, "requestId"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getRespondentId()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getRespondentId() {
        String value = "new_value";
        instance.setRespondentId(value);

        assertEquals("'getRespondentId' should be correct.",
            value, instance.getRespondentId());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRespondentId(String respondentId)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRespondentId() {
        String value = "new_value";
        instance.setRespondentId(value);

        assertEquals("'setRespondentId' should be correct.",
            value, BaseUnitTests.getField(instance, "respondentId"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getResponseTimestamp()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getResponseTimestamp() {
        Date value = new Date();
        instance.setResponseTimestamp(value);

        assertSame("'getResponseTimestamp' should be correct.",
            value, instance.getResponseTimestamp());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setResponseTimestamp(Date responseTimestamp)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setResponseTimestamp() {
        Date value = new Date();
        instance.setResponseTimestamp(value);

        assertSame("'setResponseTimestamp' should be correct.",
            value, BaseUnitTests.getField(instance, "responseTimestamp"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getData()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getData() {
        String value = "new_value";
        instance.setData(value);

        assertEquals("'getData' should be correct.",
            value, instance.getData());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setData(String data)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setData() {
        String value = "new_value";
        instance.setData(value);

        assertEquals("'setData' should be correct.",
            value, BaseUnitTests.getField(instance, "data"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>isRequestDenied()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_isRequestDenied() {
        boolean value = true;
        instance.setRequestDenied(value);

        assertTrue("'isRequestDenied' should be correct.", instance.isRequestDenied());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRequestDenied(boolean requestDenied)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRequestDenied() {
        boolean value = true;
        instance.setRequestDenied(value);

        assertTrue("'setRequestDenied' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "requestDenied"));
    }
}