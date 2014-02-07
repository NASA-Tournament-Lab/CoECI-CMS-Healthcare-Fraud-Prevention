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
 * Unit tests for {@link UserSearchCriteria} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class UserSearchCriteriaUnitTests {
    /**
     * <p>
     * Represents the <code>UserSearchCriteria</code> instance used in tests.
     * </p>
     */
    private UserSearchCriteria instance;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(UserSearchCriteriaUnitTests.class);
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
        instance = new UserSearchCriteria();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>UserSearchCriteria()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new UserSearchCriteria();

        assertNull("'username' should be correct.", BaseUnitTests.getField(instance, "username"));
        assertNull("'organizationName' should be correct.", BaseUnitTests.getField(instance, "organizationName"));
        assertNull("'roles' should be correct.", BaseUnitTests.getField(instance, "roles"));
        assertNull("'eligibleToReceiveDataRequests' should be correct.",
            BaseUnitTests.getField(instance, "eligibleToReceiveDataRequests"));
        assertNull("'eligibleToInitiateDataRequests' should be correct.",
            BaseUnitTests.getField(instance, "eligibleToInitiateDataRequests"));
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
     * Accuracy test for the method <code>getRoles()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getRoles() {
        List<String> value = new ArrayList<String>();
        instance.setRoles(value);

        assertSame("'getRoles' should be correct.",
            value, instance.getRoles());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setRoles(List&lt;String&gt; roles)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setRoles() {
        List<String> value = new ArrayList<String>();
        instance.setRoles(value);

        assertSame("'setRoles' should be correct.",
            value, BaseUnitTests.getField(instance, "roles"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getEligibleToReceiveDataRequests()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getEligibleToReceiveDataRequests() {
        Boolean value = true;
        instance.setEligibleToReceiveDataRequests(value);

        assertEquals("'getEligibleToReceiveDataRequests' should be correct.",
            value, instance.getEligibleToReceiveDataRequests());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setEligibleToReceiveDataRequests(Boolean
     * eligibleToReceiveDataRequests)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setEligibleToReceiveDataRequests() {
        Boolean value = true;
        instance.setEligibleToReceiveDataRequests(value);

        assertEquals("'setEligibleToReceiveDataRequests' should be correct.",
            value, BaseUnitTests.getField(instance, "eligibleToReceiveDataRequests"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getEligibleToInitiateDataRequests()</code>.<br>
     * The value should be properly retrieved.
     * </p>
     */
    @Test
    public void test_getEligibleToInitiateDataRequests() {
        Boolean value = true;
        instance.setEligibleToInitiateDataRequests(value);

        assertEquals("'getEligibleToInitiateDataRequests' should be correct.",
            value, instance.getEligibleToInitiateDataRequests());
    }

    /**
     * <p>
     * Accuracy test for the method <code>setEligibleToInitiateDataRequests(Boolean
     * eligibleToInitiateDataRequests)</code>.<br>
     * The value should be properly set.
     * </p>
     */
    @Test
    public void test_setEligibleToInitiateDataRequests() {
        Boolean value = true;
        instance.setEligibleToInitiateDataRequests(value);

        assertEquals("'setEligibleToInitiateDataRequests' should be correct.",
            value, BaseUnitTests.getField(instance, "eligibleToInitiateDataRequests"));
    }
}