/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.UUID;

import junit.framework.Assert;

import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.AuthenticationServiceImpl;
import com.hfpp.network.hub.services.impl.UserServiceImpl;
import com.hfpp.network.models.User;

/**
 * Functional tests for {@link AuthenticationService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AuthenticationServiceTest extends BaseTestCase {

    /**
     * Represents the authentication service
     */
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Represents the user service
     */
    @Autowired
    private UserServiceImpl userService;

    /**
     * Represents the password encryptor
     */
    @Autowired
    private PasswordEncryptor passwordEncryptor;

    /**
     * Represents the user name.
     */
    private String username = UUID.randomUUID().toString();

    /**
     * Represents the password
     */
    private String password = "password";

    /**
     * Represents the authentication service for test
     */
    private AuthenticationServiceImpl testAuthenticationService;

    /**
     * Prepare authentication service for test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        userService.setEntityManager(entityManager);
        testAuthenticationService = new AuthenticationServiceImpl();
        testAuthenticationService.setEntityManager(entityManager);
        testAuthenticationService.setUserService(userService);
        testAuthenticationService.setPasswordEncryptor(passwordEncryptor);
    }

    /**
     * Accuracy test for AuthenticationServiceImpl#checkConfiguration
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testAuthenticationService.checkConfiguration();
    }

    /**
     * Failure test for AuthenticationServiceImpl#checkConfiguration when entity
     * manager is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail1() throws Exception {
        testAuthenticationService.setEntityManager(null);
        testAuthenticationService.checkConfiguration();
    }

    /**
     * Failure test for AuthenticationServiceImpl#checkConfiguration when
     * PasswordEncryptor is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail2() throws Exception {
        testAuthenticationService.setUserService(null);
        testAuthenticationService.checkConfiguration();
    }

    /**
     * Failure test for AuthenticationServiceImpl#checkConfiguration when
     * PasswordEncryptor is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail3() throws Exception {
        testAuthenticationService.setPasswordEncryptor(null);
        testAuthenticationService.checkConfiguration();
    }

    /**
     * Accuracy test for AuthenticationServiceImpl#authenticate
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAuthenticate() throws Exception {
        User user = getTestUser();
        user.setUsername(username);
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        String userId = user.getId();
        userService.setPassword(userId, password);
        User loginUser = authenticationService.authenticate(username, password);
        Assert.assertEquals(user.toString(), loginUser.toString());
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when user name is
     * null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateFail1() throws Exception {
        authenticationService.authenticate(null, password);
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when user name is
     * empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateFail2() throws Exception {
        authenticationService.authenticate("", password);
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when user
     * password is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateFail3() throws Exception {
        authenticationService.authenticate(username, null);
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when user
     * password is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateFail4() throws Exception {
        authenticationService.authenticate(username, "");
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when user name
     * not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthenticationException.class)
    public void testAuthenticateFail5() throws Exception {
        authenticationService.authenticate("not exist user", password);
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when password is
     * wrong
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthenticationException.class)
    public void testAuthenticateFail6() throws Exception {
        User user = getTestUser();
        user.setUsername(username);
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        userService.setPassword(user.getId(), password);
        authenticationService.authenticate(username, "wrong password");
    }

    /**
     * Failure test for AuthenticationServiceImpl#authenticate when user is
     * deleted
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = AuthenticationException.class)
    public void testAuthenticateFail7() throws Exception {
        User user = getTestUser();
        user.setUsername(username);
        entityManager.persist(user.getRole());
        entityManager.flush();
        user = userService.create(user);
        entityManager.flush();
        userService.setPassword(user.getId(), password);
        authenticationService.authenticate(username, password);
        userService.delete(user.getId());
        entityManager.flush();
        entityManager.clear();
        authenticationService.authenticate(username, password);
    }

}
