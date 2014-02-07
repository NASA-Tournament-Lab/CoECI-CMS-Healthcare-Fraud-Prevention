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
 * Unit tests for {@link BaseSearchCriteria} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class BaseSearchCriteriaUnitTests {
    /**
     * <p>
     * Represents the <code>BaseSearchCriteria</code> instance used in tests.
     * </p>
     */
    private BaseSearchCriteria instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(BaseSearchCriteriaUnitTests.class);
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
        instance = new MockBaseSearchCriteria();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>BaseSearchCriteria()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new MockBaseSearchCriteria();

        assertEquals("'pageSize' should be correct.", 0, BaseUnitTests.getField(instance, "pageSize"));
        assertEquals("'pageNumber' should be correct.", 0, BaseUnitTests.getField(instance, "pageNumber"));
        assertNull("'sortBy' should be correct.", BaseUnitTests.getField(instance, "sortBy"));
        assertNull("'sortType' should be correct.", BaseUnitTests.getField(instance, "sortType"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getPageSize()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getPageSize() {
        int value = 1;
        instance.setPageSize(value);

        assertEquals("'getPageSize' should be correct.",
            value, instance.getPageSize());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setPageSize(int pageSize)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setPageSize() {
        int value = 1;
        instance.setPageSize(value);

        assertEquals("'setPageSize' should be correct.",
            value, BaseUnitTests.getField(instance, "pageSize"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getPageNumber()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getPageNumber() {
        int value = 1;
        instance.setPageNumber(value);

        assertEquals("'getPageNumber' should be correct.",
            value, instance.getPageNumber());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setPageNumber(int pageNumber)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setPageNumber() {
        int value = 1;
        instance.setPageNumber(value);

        assertEquals("'setPageNumber' should be correct.",
            value, BaseUnitTests.getField(instance, "pageNumber"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getSortBy()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getSortBy() {
        String value = "new_value";
        instance.setSortBy(value);

        assertEquals("'getSortBy' should be correct.",
            value, instance.getSortBy());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setSortBy(String sortBy)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setSortBy() {
        String value = "new_value";
        instance.setSortBy(value);

        assertEquals("'setSortBy' should be correct.",
            value, BaseUnitTests.getField(instance, "sortBy"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getSortType()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getSortType() {
        SortType value = SortType.ASC;
        instance.setSortType(value);

        assertEquals("'getSortType' should be correct.",
            value, instance.getSortType());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setSortType(SortType sortType)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setSortType() {
        SortType value = SortType.ASC;
        instance.setSortType(value);

        assertEquals("'setSortType' should be correct.",
            value, BaseUnitTests.getField(instance, "sortType"));
    }
}