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
 * Unit tests for {@link IdentifiableObject} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class IdentifiableObjectUnitTests {
    /**
     * <p>
     * Represents the <code>IdentifiableObject</code> instance used in tests.
     * </p>
     */
    private IdentifiableObject instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(IdentifiableObjectUnitTests.class);
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
        instance = new MockIdentifiableObject();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>IdentifiableObject()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new MockIdentifiableObject();

        assertNull("'id' should be correct.", BaseUnitTests.getField(instance, "id"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getId()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getId() {
        String value = "new_value";
        instance.setId(value);

        assertEquals("'getId' should be correct.",
            value, instance.getId());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setId(String id)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setId() {
        String value = "new_value";
        instance.setId(value);

        assertEquals("'setId' should be correct.",
            value, BaseUnitTests.getField(instance, "id"));
    }
}