/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;
import com.hfpp.network.hub.services.AuthorizationException;
import com.hfpp.network.hub.services.AuthorizationService;

/**
 * <p>
 * Unit tests for {@link AuthorizationServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get authorization service by spring context from base class in
 * test_Spring</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class AuthorizationServiceImplUnitTests extends BaseUnitTests {
    /**
     * <p>
     * Represents the <code>AuthorizationServiceImpl</code> instance used in
     * tests.
     * </p>
     */
    private AuthorizationServiceImpl instance;

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Represents permissions used in tests.
     * </p>
     */
    private Map<String, Set<String>> permissions;

    /**
     * <p>
     * Represents user id used in tests.
     * </p>
     */
    private String userId = "1";

    /**
     * <p>
     * Represents operation used in tests.
     * </p>
     */
    private String operation = "operation1";

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     * 
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AuthorizationServiceImplUnitTests.class);
    }

    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        instance = new AuthorizationServiceImpl();

        entityManager = getEntityManager();

        permissions = new HashMap<String, Set<String>>();
        Set<String> roles = new HashSet<String>();
        roles.add("role1");
        permissions.put("operation1", roles);
        roles = new HashSet<String>();
        roles.add("role1");
        roles.add("role3");
        permissions.put("operation2", roles);

        instance.setEntityManager(entityManager);
        instance.setPermissions(permissions);

        instance.checkConfiguration();
    }

    /**
     * <p>
     * Accuracy test with Spring.<br>
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     * @since 1.1
     */
    @Test
    public void test_Spring() throws Exception {
        // Get service
        AuthorizationService service = (AuthorizationService) APP_CONTEXT
                .getBean("authorizationService");

        service.checkAuthorization(userId, operation);
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>AuthorizationServiceImpl()</code>
     * .<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new AuthorizationServiceImpl();

        assertNull("'entityManager' should be correct.",
                BaseUnitTests.getField(instance, "entityManager"));
        assertNull("'permissions' should be correct.",
                BaseUnitTests.getField(instance, "permissions"));
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>checkAuthorization(String userId, String operation)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_checkAuthorization() throws Exception {
        instance.checkAuthorization(userId, operation);

        // Good
    }

    /**
     * <p>
     * Failure test for the method
     * <code>checkAuthorization(String userId, String operation)</code> with the
     * user isn't authorized to perform the operation.<br>
     * <code>AuthorizationException</code> is expected.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected = AuthorizationException.class)
    public void test_checkAuthorization_NotAuthorized1() throws Exception {
        userId = "note_exist";

        instance.checkAuthorization(userId, operation);
    }

    /**
     * <p>
     * Failure test for the method
     * <code>checkAuthorization(String userId, String operation)</code> with the
     * user isn't authorized to perform the operation.<br>
     * <code>AuthorizationException</code> is expected.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected = AuthorizationException.class)
    public void test_checkAuthorization_NotAuthorized2() throws Exception {
        // Deleted user
        userId = "3";
        operation = "operation2";

        instance.checkAuthorization(userId, operation);
    }

    /**
     * <p>
     * Failure test for the method
     * <code>checkAuthorization(String userId, String operation)</code> with the
     * user isn't authorized to perform the operation.<br>
     * <code>AuthorizationException</code> is expected.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected = AuthorizationException.class)
    public void test_checkAuthorization_NotAuthorized3() throws Exception {
        userId = "2";
        operation = "operation2";

        instance.checkAuthorization(userId, operation);
    }
}