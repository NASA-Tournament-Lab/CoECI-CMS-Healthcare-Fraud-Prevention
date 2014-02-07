/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for {@link AuditableObject} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class AuditableObjectUnitTests {
    /**
     * <p>
     * Represents the <code>AuditableObject</code> instance used in tests.
     * </p>
     */
    private AuditableObject instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AuditableObjectUnitTests.class);
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
        instance = new MockAuditableObject();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>AuditableObject()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new MockAuditableObject();

        assertNull("'createdBy' should be correct.", BaseUnitTests.getField(instance, "createdBy"));
        assertNull("'createdDate' should be correct.", BaseUnitTests.getField(instance, "createdDate"));
        assertNull("'updatedBy' should be correct.", BaseUnitTests.getField(instance, "updatedBy"));
        assertNull("'updatedDate' should be correct.", BaseUnitTests.getField(instance, "updatedDate"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getCreatedBy()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getCreatedBy() {
        String value = "new_value";
        instance.setCreatedBy(value);

        assertEquals("'getCreatedBy' should be correct.",
            value, instance.getCreatedBy());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setCreatedBy(String createdBy)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setCreatedBy() {
        String value = "new_value";
        instance.setCreatedBy(value);

        assertEquals("'setCreatedBy' should be correct.",
            value, BaseUnitTests.getField(instance, "createdBy"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getCreatedDate()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getCreatedDate() {
        Date value = new Date();
        instance.setCreatedDate(value);

        assertSame("'getCreatedDate' should be correct.",
            value, instance.getCreatedDate());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setCreatedDate(Date createdDate)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setCreatedDate() {
        Date value = new Date();
        instance.setCreatedDate(value);

        assertSame("'setCreatedDate' should be correct.",
            value, BaseUnitTests.getField(instance, "createdDate"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getUpdatedBy()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getUpdatedBy() {
        String value = "new_value";
        instance.setUpdatedBy(value);

        assertEquals("'getUpdatedBy' should be correct.",
            value, instance.getUpdatedBy());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setUpdatedBy(String updatedBy)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setUpdatedBy() {
        String value = "new_value";
        instance.setUpdatedBy(value);

        assertEquals("'setUpdatedBy' should be correct.",
            value, BaseUnitTests.getField(instance, "updatedBy"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getUpdatedDate()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getUpdatedDate() {
        Date value = new Date();
        instance.setUpdatedDate(value);

        assertSame("'getUpdatedDate' should be correct.",
            value, instance.getUpdatedDate());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setUpdatedDate(Date updatedDate)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setUpdatedDate() {
        Date value = new Date();
        instance.setUpdatedDate(value);

        assertSame("'setUpdatedDate' should be correct.",
            value, BaseUnitTests.getField(instance, "updatedDate"));
    }
}