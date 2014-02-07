/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.models.Role;
import com.hfpp.network.models.SearchResult;
import com.hfpp.network.models.SortType;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserSearchCriteria;
import com.hfpp.network.models.UserStatistics;

/**
 * <p>
 * Unit tests for {@link UserServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get user service by spring context from base class in test_Spring</li>
 * <li>set passwordEncryptor field for authentication service in setUp</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class UserServiceImplUnitTests extends BaseUnitTests {
    /**
     * <p>
     * Represents the <code>UserServiceImpl</code> instance used in tests.
     * </p>
     */
    private UserServiceImpl instance;

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Represents initial responded requests value used in tests.
     * </p>
     */
    private int initialRespondedRequestsValue = 1;

    /**
     * <p>
     * Represents participation ratio threshold used in tests.
     * </p>
     */
    private double participationRatioThreshold = 3;

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
     * Represents user used in tests.
     * </p>
     */
    private User user1;

    /**
     * <p>
     * Represents user used in tests.
     * </p>
     */
    private User user2;

    /**
     * <p>
     * Represents criteria used in tests.
     * </p>
     */
    private UserSearchCriteria criteria;

    /**
     * <p>
     * Represents the <code>AuthenticationServiceImpl</code> instance used in
     * tests.
     * </p>
     */
    private AuthenticationServiceImpl authenticationService;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     * 
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(UserServiceImplUnitTests.class);
    }

    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     * @since 1.1
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        instance = new UserServiceImpl();

        entityManager = getEntityManager();

        instance.setEntityManager(entityManager);
        instance.setInitialRespondedRequestsValue(initialRespondedRequestsValue);
        instance.setParticipationRatioThreshold(participationRatioThreshold);
        instance.setPasswordEncryptor(passwordEncryptor);
        instance.setRolesEligibleToReceiveDataRequests(rolesEligibleToReceiveDataRequests);
        instance.setRolesEligibleToInitiateDataRequests(rolesEligibleToInitiateDataRequests);

        instance.checkConfiguration();

        user1 = new User();
        user1.setUsername("username1");
        user1.setOrganizationName("organization1");
        user1.setRole(entityManager.find(Role.class, "1"));
        user1.setAutoRetrieveCachedData(true);
        user1.setCreatedBy("1");

        user2 = new User();
        user2.setUsername("username2");
        user2.setOrganizationName("organization2");
        user2.setRole(entityManager.find(Role.class, "2"));
        user2.setCreatedBy("2");

        criteria = new UserSearchCriteria();

        authenticationService = new AuthenticationServiceImpl();
        authenticationService.setPasswordEncryptor(passwordEncryptor);
        authenticationService.setEntityManager(entityManager);
        authenticationService.setUserService(instance);
        authenticationService.checkConfiguration();
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
        UserService service = (UserService) APP_CONTEXT.getBean("userService");

        User user = service.create(user1);

        user.setOrganizationName("new org");
        service.update(user);

        user = service.get(user.getId());

        user = service.getByUsername(user.getUsername());

        SearchResult<User> result = service.search(new UserSearchCriteria());

        service.delete(user.getId());
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>UserServiceImpl()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new UserServiceImpl();

        assertNull("'entityManager' should be correct.",
                BaseUnitTests.getField(instance, "entityManager"));
        assertEquals("'initialRespondedRequestsValue' should be correct.", 0,
                BaseUnitTests.getField(instance,
                        "initialRespondedRequestsValue"));
        assertEquals("'participationRatioThreshold' should be correct.", 0,
                (Double) BaseUnitTests.getField(instance,
                        "participationRatioThreshold"), 0.001);
        assertNotNull("'passwordEncryptor' should be correct.",
                BaseUnitTests.getField(instance, "passwordEncryptor"));
        assertNull("'rolesEligibleToReceiveDataRequests' should be correct.",
                BaseUnitTests.getField(instance,
                        "rolesEligibleToReceiveDataRequests"));
        assertNull("'rolesEligibleToInitiateDataRequests' should be correct.",
                BaseUnitTests.getField(instance,
                        "rolesEligibleToInitiateDataRequests"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>create(User user)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_create() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        User retrievedUser = entityManager.find(User.class, user1.getId());
        assertEquals("'create' should be correct.", user1.getId(),
                retrievedUser.getId());
        assertEquals("'create' should be correct.", user1.getUsername(),
                retrievedUser.getUsername());
        assertEquals("'create' should be correct.",
                user1.getOrganizationName(),
                retrievedUser.getOrganizationName());
        assertEquals("'create' should be correct.", user1.getRole().getId(),
                retrievedUser.getRole().getId());
        assertEquals("'create' should be correct.",
                user1.isAutoRetrieveCachedData(),
                retrievedUser.isAutoRetrieveCachedData());
        assertEquals("'create' should be correct.", user1.getCreatedBy(),
                retrievedUser.getCreatedBy());
        assertNotNull("'create' should be correct.",
                retrievedUser.getCreatedDate());
        assertNull("'create' should be correct.", retrievedUser.getUpdatedBy());
        assertNull("'create' should be correct.",
                retrievedUser.getUpdatedDate());

        UserStatistics userStatistics = entityManager.find(
                UserStatistics.class, user1.getId());
        assertEquals("'create' should be correct.", user1.getId(),
                userStatistics.getUserId());
        assertEquals("'create' should be correct.", 0,
                userStatistics.getDataRequestsReceived());
        assertEquals("'create' should be correct.",
                initialRespondedRequestsValue,
                userStatistics.getDataRequestsResponded());
        assertEquals("'create' should be correct.", 0,
                userStatistics.getDataRequestsInitiated());
    }

    /**
     * <p>
     * Accuracy test for the method <code>update(User user)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_update() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        user1.setOrganizationName("new");
        user1.setUpdatedBy("2");
        entityManager.getTransaction().begin();
        instance.update(user1);
        entityManager.getTransaction().commit();

        User retrievedUser = entityManager.find(User.class, user1.getId());
        assertEquals("'update' should be correct.",
                user1.getOrganizationName(),
                retrievedUser.getOrganizationName());
        assertEquals("'update' should be correct.", user1.getUpdatedBy(),
                retrievedUser.getUpdatedBy());
        assertNotNull("'update' should be correct.",
                retrievedUser.getUpdatedDate());
    }

    /**
     * <p>
     * Accuracy test for the method <code>get(String id)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_get_1() throws Exception {
        User res = instance.get("not_exist");

        assertNull("'get' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method <code>get(String id)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_get_2() throws Exception {
        // Deleted user
        User res = instance.get("3");

        assertNull("'get' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method <code>get(String id)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_get_3() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        User res = instance.get(user1.getId());

        assertEquals("'get' should be correct.", user1.getId(), res.getId());
        assertEquals("'get' should be correct.", user1.getUsername(),
                res.getUsername());
        assertEquals("'get' should be correct.", user1.getOrganizationName(),
                res.getOrganizationName());

        assertEquals("'get' should be correct.", user1.getRole().getId(), res
                .getRole().getId());
        assertEquals("'get' should be correct.", user1.getRole().getName(), res
                .getRole().getName());

        assertEquals("'get' should be correct.",
                user1.isAutoRetrieveCachedData(),
                res.isAutoRetrieveCachedData());
        assertEquals("'get' should be correct.", user1.getCreatedBy(),
                res.getCreatedBy());
        assertNotNull("'get' should be correct.", res.getCreatedDate());
        assertNull("'get' should be correct.", res.getUpdatedBy());
        assertNull("'get' should be correct.", res.getUpdatedDate());

        assertTrue("'get' should be correct.",
                res.isEligibleToInitiateDataRequests());
        assertTrue("'get' should be correct.",
                res.isEligibleToReceiveDataRequests());
    }

    /**
     * <p>
     * Accuracy test for the method <code>getByUsername(String username)</code>.
     * <br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getByUsername_1() throws Exception {
        User res = instance.getByUsername("not_exist");

        assertNull("'getByUsername' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method <code>getByUsername(String username)</code>.
     * <br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getByUsername_2() throws Exception {
        // Deleted user
        User res = instance.getByUsername("user3");

        assertNull("'getByUsername' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method <code>getByUsername(String username)</code>.
     * <br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getByUsername_3() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        User res = instance.getByUsername(user1.getUsername());

        assertEquals("'getByUsername' should be correct.", user1.getId(),
                res.getId());
        assertEquals("'getByUsername' should be correct.", user1.getUsername(),
                res.getUsername());
        assertEquals("'getByUsername' should be correct.",
                user1.getOrganizationName(), res.getOrganizationName());

        assertEquals("'getByUsername' should be correct.", user1.getRole()
                .getId(), res.getRole().getId());
        assertEquals("'getByUsername' should be correct.", user1.getRole()
                .getName(), res.getRole().getName());

        assertEquals("'getByUsername' should be correct.",
                user1.isAutoRetrieveCachedData(),
                res.isAutoRetrieveCachedData());
        assertEquals("'getByUsername' should be correct.",
                user1.getCreatedBy(), res.getCreatedBy());
        assertNotNull("'getByUsername' should be correct.",
                res.getCreatedDate());
        assertNull("'getByUsername' should be correct.", res.getUpdatedBy());
        assertNull("'getByUsername' should be correct.", res.getUpdatedDate());

        assertTrue("'getByUsername' should be correct.",
                res.isEligibleToInitiateDataRequests());
        assertTrue("'getByUsername' should be correct.",
                res.isEligibleToReceiveDataRequests());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>search(UserSearchCriteria criteria)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_search_1() throws Exception {
        clearDB();

        SearchResult<User> res = instance.search(criteria);

        assertEquals("'search' should be correct.", 0, res.getTotal());
        assertEquals("'search' should be correct.", 0, res.getTotalPages());
        List<User> values = res.getValues();
        assertEquals("'search' should be correct.", 0, values.size());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>search(UserSearchCriteria criteria)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_search_2() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        instance.create(user2);
        entityManager.getTransaction().commit();

        criteria.setSortBy("username");
        criteria.setSortType(SortType.DESC);
        criteria.setPageNumber(2);
        criteria.setPageSize(2);

        SearchResult<User> res = instance.search(criteria);

        assertEquals("'search' should be correct.", 4, res.getTotal());
        assertEquals("'search' should be correct.", 2, res.getTotalPages());
        List<User> values = res.getValues();
        assertEquals("'search' should be correct.", 2, values.size());
        assertEquals("'search' should be correct.", "2", values.get(0).getId());
        assertEquals("'search' should be correct.", "1", values.get(1).getId());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>search(UserSearchCriteria criteria)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_search_3() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        criteria.setUsername(user1.getUsername());
        criteria.setOrganizationName(user1.getOrganizationName());
        criteria.setRoles(Arrays.asList(user1.getRole().getName()));
        criteria.setEligibleToReceiveDataRequests(true);
        criteria.setEligibleToInitiateDataRequests(true);

        SearchResult<User> res = instance.search(criteria);

        assertEquals("'search' should be correct.", 1, res.getTotal());
        assertEquals("'search' should be correct.", 1, res.getTotalPages());
        List<User> values = res.getValues();
        assertEquals("'search' should be correct.", 1, values.size());
        assertEquals("'search' should be correct.", user1.getId(), values
                .get(0).getId());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>search(UserSearchCriteria criteria)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_search_4() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        criteria.setUsername(user1.getUsername());
        criteria.setOrganizationName(user1.getOrganizationName());
        criteria.setRoles(Arrays.asList(user1.getRole().getName()));
        criteria.setEligibleToReceiveDataRequests(false);
        criteria.setEligibleToInitiateDataRequests(false);

        SearchResult<User> res = instance.search(criteria);

        assertEquals("'search' should be correct.", 0, res.getTotal());
        assertEquals("'search' should be correct.", 0, res.getTotalPages());
        List<User> values = res.getValues();
        assertEquals("'search' should be correct.", 0, values.size());
    }

    /**
     * <p>
     * Accuracy test for the method <code>delete(String id)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_delete() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        instance.delete(user1.getId());
        entityManager.getTransaction().commit();

        User res = instance.get(user1.getId());

        assertNull("'delete' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>setPassword(String id, String password)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_setPassword() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        instance.setPassword(user1.getId(), "a*32/d");
        entityManager.getTransaction().commit();

        User user = authenticationService.authenticate(user1.getUsername(),
                "a*32/d");

        assertEquals("'setPassword' should be correct.", user1.getId(),
                user.getId());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>getUserStatistics(String userId)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getUserStatistics() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        UserStatistics res = instance.getUserStatistics(user1.getId());
        assertEquals("'getUserStatistics' should be correct.", user1.getId(),
                res.getUserId());
        assertEquals("'getUserStatistics' should be correct.", 0,
                res.getDataRequestsReceived());
        assertEquals("'getUserStatistics' should be correct.",
                initialRespondedRequestsValue, res.getDataRequestsResponded());
        assertEquals("'getUserStatistics' should be correct.", 0,
                res.getDataRequestsInitiated());
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>isParticipationRatioLow(String userId)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_isParticipationRatioLow_1() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        boolean res = instance.isParticipationRatioLow(user1.getId());
        assertFalse("'isParticipationRatioLow' should be correct.", res);
    }

    /**
     * <p>
     * Accuracy test for the method
     * <code>isParticipationRatioLow(String userId)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_isParticipationRatioLow_2() throws Exception {
        entityManager.getTransaction().begin();
        instance.create(user1);
        entityManager.getTransaction().commit();

        UserStatistics userStatistics = instance.getUserStatistics(user1
                .getId());
        userStatistics.setDataRequestsInitiated(1);
        entityManager.getTransaction().begin();
        entityManager.merge(userStatistics);
        entityManager.getTransaction().commit();

        boolean res = instance.isParticipationRatioLow(user1.getId());
        assertTrue("'isParticipationRatioLow' should be correct.", res);
    }
}