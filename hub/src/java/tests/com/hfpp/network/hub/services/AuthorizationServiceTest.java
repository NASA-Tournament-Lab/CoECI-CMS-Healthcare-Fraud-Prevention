/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.AuthorizationServiceImpl;
import com.hfpp.network.models.User;

/**
 * Functional tests for {@link AuthorizationService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AuthorizationServiceTest extends BaseTestCase {
    /**
     * Represents the authorization service
     */
    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Represents the implementation of authorization service.
     */
    @Autowired
    private AuthorizationServiceImpl authorizationServiceImpl;

    /**
     * Represents the user Id
     */
    private String userId = UUID.randomUUID().toString();

    /**
     * Represents the operation
     */
    private String operation = "operation1";

    /**
     * Represents the implementation of authorization service for test.
     */
    private AuthorizationServiceImpl testAuthorizationServiceImpl;

    /**
     * Represents the permissions of authorization service for test.
     */
    @Resource
    private Map<String, Set<String>> permissions;

    /**
     * Prepare authorization service for test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testAuthorizationServiceImpl = new AuthorizationServiceImpl();
        testAuthorizationServiceImpl.setEntityManager(entityManager);
        testAuthorizationServiceImpl.setPermissions(permissions);
    }

    /**
     * Accuracy test for AuthorizationServiceImpl#checkConfiguration
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when entity
     * manager is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail1() throws Exception {
        testAuthorizationServiceImpl.setEntityManager(null);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail2() throws Exception {
        testAuthorizationServiceImpl.setPermissions(null);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail3() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission contains null key
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail4() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        permission.put(null, new HashSet<String>());
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission contains empty key
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail5() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        permission.put("", new HashSet<String>());
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission contains null value set
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail6() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        permission.put("oper", null);
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission contains empty value set
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail7() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        permission.put("oper", new HashSet<String>());
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission contains set with null value
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail8() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        Set<String> roles = new HashSet<String>();
        roles.add(null);
        permission.put("oper", roles);
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkConfiguration when
     * permission contains set with empty value
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail9() throws Exception {
        Map<String, Set<String>> permission = new HashMap<String, Set<String>>();
        Set<String> roles = new HashSet<String>();
        roles.add("");
        permission.put("oper", roles);
        testAuthorizationServiceImpl.setPermissions(permission);
        testAuthorizationServiceImpl.checkConfiguration();
    }

    /**
     * Accuracy test for AuthorizationServiceImpl#checkAuthorization
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckAuthorization() throws Exception {
        clearDB();
        entityManager.flush();
        operation = permissions.keySet().iterator().next();
        User user = getTestUser();
        user.getRole().setName(permissions.get(operation).iterator().next());
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();
        userId = user.getId();
        authorizationService.checkAuthorization(userId, operation);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when userId
     * is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckAuthorizationFail1() throws Exception {
        authorizationService.checkAuthorization(null, operation);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when userId
     * is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckAuthorizationFail2() throws Exception {
        authorizationService.checkAuthorization("", operation);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when
     * password is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckAuthorizationFail3() throws Exception {
        authorizationService.checkAuthorization(userId, null);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when
     * password is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckAuthorizationFail4() throws Exception {
        authorizationService.checkAuthorization(userId, "");
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when not
     * exist user Id
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testCheckAuthorizationFail5() throws Exception {
        authorizationService.checkAuthorization("not exist user", operation);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when not
     * valid operation
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testCheckAuthorizationFail6() throws Exception {
        User user = getTestUser();
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        entityManager.flush();
        userId = user.getId();
        operation = "not exist operation";
        authorizationService.checkAuthorization(userId, operation);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when not
     * valid role name
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthorizationException.class)
    public void testCheckAuthorizationFail7() throws Exception {
        User user = getTestUser();
        user.getRole().setName("not valid role");
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        entityManager.flush();
        userId = user.getId();
        authorizationService.checkAuthorization(userId, operation);
    }

    /**
     * Failure test for AuthorizationServiceImpl#checkAuthorization when user
     * deleted
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testCheckAuthorizationFail8() throws Exception {
        clearDB();
        entityManager.flush();
        operation = permissions.keySet().iterator().next();
        User user = getTestUser();
        user.getRole().setName(permissions.get(operation).iterator().next());
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();
        userId = user.getId();
        authorizationService.checkAuthorization(userId, operation);
        entityManager
                .createNativeQuery(
                        "update user u set u.is_deleted=1 where u.id=:id")
                .setParameter("id", userId).executeUpdate();
        entityManager.flush();
        authorizationService.checkAuthorization(userId, operation);
    }
}
