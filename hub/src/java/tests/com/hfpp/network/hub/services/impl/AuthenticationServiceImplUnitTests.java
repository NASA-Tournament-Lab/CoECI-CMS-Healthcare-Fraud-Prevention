/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.JUnit4TestAdapter;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;
import com.hfpp.network.hub.services.AuthenticationException;
import com.hfpp.network.hub.services.AuthenticationService;
import com.hfpp.network.models.User;

/**
 * <p>
 * Unit tests for {@link AuthenticationServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get authentication service by spring context from base class in
 * test_Spring</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class AuthenticationServiceImplUnitTests extends BaseUnitTests {
    /**
     * <p>
     * Represents the <code>AuthenticationServiceImpl</code> instance used in
     * tests.
     * </p>
     */
    private AuthenticationServiceImpl instance;

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Represents password encryptor used in tests.
     * </p>
     */
    private PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    /**
     * <p>
     * Represents roles eligible to receive data requests used in tests.
     * </p>
     */
    private List<String> rolesEligibleToReceiveDataRequests = new ArrayList<String>(
            Arrays.asList("role1", "role2", "role3"));

    /**
     * <p>
     * Represents roles eligible to initiate data requests used in tests.
     * </p>
     */
    private List<String> rolesEligibleToInitiateDataRequests = new ArrayList<String>(
            Arrays.asList("role1", "role2", "role3"));

    /**
     * <p>
     * Represents user service used in tests.
     * </p>
     */
    private UserServiceImpl userService;

    /**
     * <p>
     * Represents username used in tests.
     * </p>
     */
    private String username = "user1";

    /**
     * <p>
     * Represents password used in tests.
     * </p>
     */
    private String password = "pass1";

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     * 
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AuthenticationServiceImplUnitTests.class);
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

        instance = new AuthenticationServiceImpl();

        entityManager = getEntityManager();

        userService = new UserServiceImpl();
        userService.setEntityManager(entityManager);
        userService.setInitialRespondedRequestsValue(1);
        userService.setParticipationRatioThreshold(3);
        userService.setPasswordEncryptor(passwordEncryptor);
        userService
                .setRolesEligibleToInitiateDataRequests(rolesEligibleToInitiateDataRequests);
        userService
                .setRolesEligibleToReceiveDataRequests(rolesEligibleToReceiveDataRequests);
        userService.checkConfiguration();

        instance.setEntityManager(entityManager);
        instance.setPasswordEncryptor(passwordEncryptor);
        instance.setUserService(userService);

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
    @SuppressWarnings("unused")
    @Test
    public void test_Spring() throws Exception {
        // Get service
        AuthenticationService service = (AuthenticationService) APP_CONTEXT
                .getBean("authenticationService");

        User user = service.authenticate(username, password);
    }

    /**
     * <p>
     * Accuracy test for the constructor
     * <code>AuthenticationServiceImpl()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new AuthenticationServiceImpl();
        assertNull("'entityManager' should be correct.",
                BaseUnitTests.getField(instance, "entityManager"));
        assertNull("'passwordEncryptor' should be correct.",
                BaseUnitTests.getField(instance, "passwordEncryptor"));
        assertNull("'userService' should be correct.",
                BaseUnitTests.getField(instance, "userService"));
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>authenticate(String username, String password)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_authenticate() throws Exception {
        User res = instance.authenticate(username, password);

        assertEquals("'authenticate' should be correct.", "1", res.getId());
    }

    /**
     * <p>
     * Failure test for the method
     * <code>authenticate(String username, String password)</code> with the user
     * fails the authentication.<br>
     * <code>AuthenticationException</code> is expected.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected = AuthenticationException.class)
    public void test_authenticate_FailedAuthentication1() throws Exception {
        username = "not_exist";

        instance.authenticate(username, password);
    }

    /**
     * <p>
     * Failure test for the method
     * <code>authenticate(String username, String password)</code> with the user
     * fails the authentication.<br>
     * <code>AuthenticationException</code> is expected.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test(expected = AuthenticationException.class)
    public void test_authenticate_FailedAuthentication2() throws Exception {
        password = "invalid";

        instance.authenticate(username, password);
    }
}