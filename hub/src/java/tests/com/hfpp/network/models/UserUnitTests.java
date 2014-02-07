/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;

/**
 * <p>
 * Unit tests for {@link User} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class UserUnitTests {
    /**
     * <p>
     * Represents the <code>User</code> instance used in tests.
     * </p>
     */
    private User instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(UserUnitTests.class);
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
        instance = new User();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>User()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new User();

        assertNull("'username' should be correct.", BaseUnitTests.getField(instance, "username"));
        assertNull("'organizationName' should be correct.", BaseUnitTests.getField(instance, "organizationName"));
        assertNull("'role' should be correct.", BaseUnitTests.getField(instance, "role"));
        assertFalse("'autoRetrieveCachedData' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "autoRetrieveCachedData"));
        assertFalse("'eligibleToReceiveDataRequests' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "eligibleToReceiveDataRequests"));
        assertFalse("'eligibleToInitiateDataRequests' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "eligibleToInitiateDataRequests"));
    }


    /**
     * <p>
     * Accuracy test for the method <code>getUsername()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getUsername() {
        String value = "new_value";
        instance.setUsername(value);

        assertEquals("'getUsername' should be correct.",
            value, instance.getUsername());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setUsername(String username)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setUsername() {
        String value = "new_value";
        instance.setUsername(value);

        assertEquals("'setUsername' should be correct.",
            value, BaseUnitTests.getField(instance, "username"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getOrganizationName()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getOrganizationName() {
        String value = "new_value";
        instance.setOrganizationName(value);

        assertEquals("'getOrganizationName' should be correct.",
            value, instance.getOrganizationName());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setOrganizationName(String organizationName)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setOrganizationName() {
        String value = "new_value";
        instance.setOrganizationName(value);

        assertEquals("'setOrganizationName' should be correct.",
            value, BaseUnitTests.getField(instance, "organizationName"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getRole()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getRole() {
        Role value = new Role();
        instance.setRole(value);

        assertSame("'getRole' should be correct.",
            value, instance.getRole());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRole(Role role)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRole() {
        Role value = new Role();
        instance.setRole(value);

        assertSame("'setRole' should be correct.",
            value, BaseUnitTests.getField(instance, "role"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>isAutoRetrieveCachedData()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_isAutoRetrieveCachedData() {
        boolean value = true;
        instance.setAutoRetrieveCachedData(value);

        assertTrue("'isAutoRetrieveCachedData' should be correct.", instance.isAutoRetrieveCachedData());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setAutoRetrieveCachedData(boolean autoRetrieveCachedData)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setAutoRetrieveCachedData() {
        boolean value = true;
        instance.setAutoRetrieveCachedData(value);

        assertTrue("'setAutoRetrieveCachedData' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "autoRetrieveCachedData"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>isEligibleToReceiveDataRequests()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_isEligibleToReceiveDataRequests() {
        boolean value = true;
        instance.setEligibleToReceiveDataRequests(value);

        assertTrue("'isEligibleToReceiveDataRequests' should be correct.", instance.isEligibleToReceiveDataRequests());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setEligibleToReceiveDataRequests(boolean
     * eligibleToReceiveDataRequests)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setEligibleToReceiveDataRequests() {
        boolean value = true;
        instance.setEligibleToReceiveDataRequests(value);

        assertTrue("'setEligibleToReceiveDataRequests' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "eligibleToReceiveDataRequests"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>isEligibleToInitiateDataRequests()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_isEligibleToInitiateDataRequests() {
        boolean value = true;
        instance.setEligibleToInitiateDataRequests(value);

        assertTrue("'isEligibleToInitiateDataRequests' should be correct.",
            instance.isEligibleToInitiateDataRequests());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setEligibleToInitiateDataRequests(boolean
     * eligibleToInitiateDataRequests)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setEligibleToInitiateDataRequests() {
        boolean value = true;
        instance.setEligibleToInitiateDataRequests(value);

        assertTrue("'setEligibleToInitiateDataRequests' should be correct.",
            (Boolean) BaseUnitTests.getField(instance, "eligibleToInitiateDataRequests"));
    }
}