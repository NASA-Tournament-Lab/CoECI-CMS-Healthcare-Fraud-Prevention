/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * <p>
 * Unit tests for {@link SortType} enumeration.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class SortTypeUnitTests {
    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SortTypeUnitTests.class);
    }

    /**
     * <p>
     * Accuracy test for the constant <code>DESC</code>.<br>
     * The value should be correct.
     * </p>
     */
    @Test
    public void test_DESC() {
        assertEquals("'DESC' should be correct.", "DESC", SortType.DESC.toString());
    }

    /**
     * <p>
     * Accuracy test for the constant <code>ASC</code>.<br>
     * The value should be correct.
     * </p>
     */
    @Test
    public void test_ASC() {
        assertEquals("'ASC' should be correct.", "ASC", SortType.ASC.toString());
    }
}
