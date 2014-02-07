/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for <code>SearchResult&lt;T&gt;</code> class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class SearchResultUnitTests {
    /**
     * <p>
     * Represents the <code>SearchResult&lt;T&gt;</code> instance used in tests.
     * </p>
     */
    private SearchResult<Object> instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SearchResultUnitTests.class);
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
        instance = new SearchResult<Object>();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>SearchResult()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new SearchResult<Object>();

        assertEquals("'total' should be correct.", 0, BaseUnitTests.getField(instance, "total"));
        assertEquals("'totalPages' should be correct.", 0, BaseUnitTests.getField(instance, "totalPages"));
        assertNull("'values' should be correct.", BaseUnitTests.getField(instance, "values"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getTotal()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getTotal() {
        int value = 1;
        instance.setTotal(value);

        assertEquals("'getTotal' should be correct.",
            value, instance.getTotal());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setTotal(int total)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setTotal() {
        int value = 1;
        instance.setTotal(value);

        assertEquals("'setTotal' should be correct.",
            value, BaseUnitTests.getField(instance, "total"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getTotalPages()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getTotalPages() {
        int value = 1;
        instance.setTotalPages(value);

        assertEquals("'getTotalPages' should be correct.",
            value, instance.getTotalPages());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setTotalPages(int totalPages)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setTotalPages() {
        int value = 1;
        instance.setTotalPages(value);

        assertEquals("'setTotalPages' should be correct.",
            value, BaseUnitTests.getField(instance, "totalPages"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getValues()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getValues() {
        List<Object> value = new ArrayList<Object>();
        instance.setValues(value);

        assertSame("'getValues' should be correct.",
            value, instance.getValues());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setValues(List&lt;T&gt; values)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setValues() {
        List<Object> value = new ArrayList<Object>();
        instance.setValues(value);

        assertSame("'setValues' should be correct.",
            value, BaseUnitTests.getField(instance, "values"));
    }
}