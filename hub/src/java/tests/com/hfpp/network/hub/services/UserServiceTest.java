/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.Query;

import junit.framework.Assert;

import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.UserServiceImpl;
import com.hfpp.network.models.SearchResult;
import com.hfpp.network.models.SortType;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserSearchCriteria;
import com.hfpp.network.models.UserStatistics;

/**
 * Functional tests for {@link UserService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class UserServiceTest extends BaseTestCase {
    /**
     * Represents the authentication service
     */
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Represents the user service
     */
    @Autowired
    private UserService userService;

    /**
     * Represents the implementation of user service
     */
    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     * Represents the password encryptor
     */
    @Autowired
    private PasswordEncryptor passwordEncryptor;

    /**
     * Represents the initial number of responded-requests value for new users.
     */
    @Value("#{configProperties['initialRespondedRequestsValue']}")
    private int initialRespondedRequestsValue;

    /**
     * Represents the participation ratio threshold value for users.
     */
    @Value("#{configProperties['participationRatioThreshold']}")
    private int participationRatioThreshold;

    /**
     * Represents the roles eligible to receive dataRequests.
     */
    @Resource
    private List<String> rolesEligibleToReceiveDataRequests;

    /**
     * Represents the roles eligible to initiate dataRequests.
     */
    @Resource
    private List<String> rolesEligibleToInitiateDataRequests;

    /**
     * Represents the user
     */
    private User userForTest;

    /**
     * Represents the implementation of user service for test
     */
    private UserServiceImpl testUserServiceImpl;

    /**
     * Prepare user and user service to test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        userForTest = getTestUser();
        userServiceImpl.setEntityManager(entityManager);
        testUserServiceImpl = new UserServiceImpl();
        testUserServiceImpl.setEntityManager(entityManager);
        testUserServiceImpl
                .setInitialRespondedRequestsValue(initialRespondedRequestsValue);
        testUserServiceImpl
                .setParticipationRatioThreshold(participationRatioThreshold);
        testUserServiceImpl.setPasswordEncryptor(passwordEncryptor);
        testUserServiceImpl
                .setRolesEligibleToInitiateDataRequests(rolesEligibleToReceiveDataRequests);
        testUserServiceImpl
                .setRolesEligibleToReceiveDataRequests(rolesEligibleToInitiateDataRequests);
    }

    /**
     * Accuracy test for UserServiceImpl#checkConfiguration
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when entity manager
     * is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail1() throws Exception {
        testUserServiceImpl.setEntityManager(null);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * InitialRespondedRequestsValue not positive
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail2() throws Exception {
        testUserServiceImpl.setInitialRespondedRequestsValue(-1);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * ParticipationRatioThreshold not positive
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail3() throws Exception {
        testUserServiceImpl.setParticipationRatioThreshold(-1);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * PasswordEncryptor is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail4() throws Exception {
        testUserServiceImpl.setPasswordEncryptor(null);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToReceiveDataRequests is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail5() throws Exception {
        testUserServiceImpl.setRolesEligibleToReceiveDataRequests(null);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToReceiveDataRequests is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail6() throws Exception {
        testUserServiceImpl
                .setRolesEligibleToReceiveDataRequests(new ArrayList<String>());
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToReceiveDataRequests contains null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail7() throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(null);
        testUserServiceImpl.setRolesEligibleToReceiveDataRequests(list);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToReceiveDataRequests contains empty string
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail8() throws Exception {
        List<String> list = new ArrayList<String>();
        list.add("");
        testUserServiceImpl.setRolesEligibleToReceiveDataRequests(list);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToInitiateDataRequests is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail9() throws Exception {
        testUserServiceImpl.setRolesEligibleToInitiateDataRequests(null);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToInitiateDataRequests is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail10() throws Exception {
        testUserServiceImpl
                .setRolesEligibleToInitiateDataRequests(new ArrayList<String>());
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToInitiateDataRequests contains null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail11() throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(null);
        testUserServiceImpl.setRolesEligibleToInitiateDataRequests(list);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Failure test for UserServiceImpl#checkConfiguration when
     * RolesEligibleToInitiateDataRequests contains empty string
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail12() throws Exception {
        List<String> list = new ArrayList<String>();
        list.add("");
        testUserServiceImpl.setRolesEligibleToInitiateDataRequests(list);
        testUserServiceImpl.checkConfiguration();
    }

    /**
     * Accuracy test for UserServiceImpl#create
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCreate() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        checkUserHistoryExist(testUser);
    }

    /**
     * Failure test for UserServiceImpl#create when user is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateFail1() throws Exception {
        userService.create(null);
    }

    /**
     * Failure test for UserServiceImpl#create when user is invalid
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testCreateFail2() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        userForTest.setUsername(null);
        userService.create(userForTest);
    }

    /**
     * Accuracy test for UserServiceImpl#update
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testUpdate() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        entityManager.clear();
        String newUserName = UUID.randomUUID().toString();
        testUser.setUsername(newUserName);
        checkUserNotExist(testUser);
        userService.update(testUser);
        entityManager.flush();
        checkUserExist(testUser);
        checkUserHistoryExist(testUser);
    }

    /**
     * Failure test for UserServiceImpl#update when user is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateFail1() throws Exception {
        userService.update(null);
    }

    /**
     * Failure test for UserServiceImpl#update when user is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateFail2() throws Exception {
        userForTest.setId(null);
        userService.update(userForTest);
    }

    /**
     * Failure test for UserServiceImpl#update when user is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateFail3() throws Exception {
        userForTest.setId("");
        userService.update(userForTest);
    }

    /**
     * Failure test for UserServiceImpl#update when user not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testUpdateFail4() throws Exception {
        userForTest.setId("not exist Id");
        userService.update(userForTest);
    }

    /**
     * Failure test for UserServiceImpl#update when user not valid
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testUpdateFail5() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser1 = userService.create(userForTest);
        entityManager.flush();
        entityManager.clear();
        User user2 = getTestUser();
        entityManager.persist(user2.getRole());
        entityManager.flush();
        User testUser2 = userService.create(user2);
        testUser2.setUsername(testUser1.getUsername());
        userService.update(testUser2);
    }

    /**
     * Failure test for UserServiceImpl#update when user deleted
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testUpdateFail6() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        userService.delete(testUser.getId());
        userService.update(testUser);
    }

    /**
     * Accuracy test for UserServiceImpl#get
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGet() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        User testUser2 = userService.get(testUser.getId());
        Assert.assertEquals(testUser.toString(), testUser2.toString());
        userService.delete(testUser.getId());
        entityManager.flush();
        Assert.assertNull(userService.get(testUser.getId()));
    }

    /**
     * Failure test for UserServiceImpl#get when id is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFail1() throws Exception {
        userService.get(null);
    }

    /**
     * Failure test for UserServiceImpl#get when id is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFail2() throws Exception {
        userService.get("");
    }

    /**
     * Failure test for UserServiceImpl#get when user with id not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGetFail3() throws Exception {
        Assert.assertNull(userService.get("not exist Id"));
    }

    /**
     * Accuracy test for UserServiceImpl#getByUsername
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGetByUsername1() throws Exception {
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        User testUser2 = userService.getByUsername(testUser.getUsername());
        Assert.assertEquals(testUser.toString(), testUser2.toString());
        userService.delete(testUser.getId());
        entityManager.flush();
        Assert.assertNull(userService.getByUsername(testUser.getUsername()));
    }

    /**
     * Accuracy test for UserServiceImpl#getByUsername when user with given name
     * not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGetByUsername2() throws Exception {
        Assert.assertNull(userService.getByUsername("not exist user name"));
    }

    /**
     * Failure test for UserServiceImpl#getByUsername when user name is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetByUsernameFail1() throws Exception {
        userService.getByUsername(null);
    }

    /**
     * Failure test for UserServiceImpl#getByUsername when user name is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetByUsernameFail2() throws Exception {
        userService.getByUsername("");
    }

    /**
     * Accuracy test for UserServiceImpl#delete
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testDelete() throws Exception {
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        userService.delete(testUser.getId());
        entityManager.flush();
        checkUserNotExist(testUser);
        checkUserHistoryExist(testUser, true);
    }

    /**
     * Failure test for UserServiceImpl#delete when id is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFail1() throws Exception {
        userService.delete(null);
    }

    /**
     * Failure test for UserServiceImpl#delete when id is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFail2() throws Exception {
        userService.delete("");
    }

    /**
     * Failure test for UserServiceImpl#delete when user with given id not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteFail3() throws Exception {
        userService.delete("not exist Id");
    }

    /**
     * Failure test for UserServiceImpl#delete when delete twice
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteFail4() throws Exception {
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        userService.delete(testUser.getId());
        entityManager.flush();
        checkUserNotExist(testUser);
        checkUserHistoryExist(testUser, true);
        userService.delete(testUser.getId());
    }

    /**
     * Accuracy test for UserServiceImpl#search when no user in db
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch1() throws Exception {
        clearDB();
        entityManager.flush();
        UserSearchCriteria criteria = new UserSearchCriteria();
        SearchResult<User> users = userService.search(criteria);
        Assert.assertEquals(0, users.getTotal());
        Assert.assertEquals(0, users.getTotalPages());
        Assert.assertEquals(0, users.getValues().size());
    }

    /**
     * Accuracy test for UserServiceImpl#search with paging number and paging
     * size,sort by and sort type
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch2() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername(Integer.toString(i));
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            entityManager.flush();
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count, searchResult.getValues().size());
        for (int i = 0; i < count; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(Integer.parseInt(user.getUsername()))
                    .toString(), user.toString());
        }
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals((count - 1) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(Integer.parseInt(user.getUsername()))
                    .toString(), user.toString());
        }
        criteria.setPageNumber(8);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals((count - 1) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(0, searchResult.getValues().size());
        criteria.setPageNumber(0);
        criteria.setSortBy("organization_name");
        criteria.setSortType(SortType.DESC);
        SearchResult<User> searchResult1 = userService.search(criteria);
        Assert.assertEquals(count, searchResult1.getTotal());
        Assert.assertEquals(1, searchResult1.getTotalPages());
        Assert.assertEquals(count, searchResult1.getValues().size());
        criteria.setSortType(SortType.ASC);
        SearchResult<User> searchResult2 = userService.search(criteria);
        Assert.assertEquals(count, searchResult2.getTotal());
        Assert.assertEquals(1, searchResult2.getTotalPages());
        Assert.assertEquals(count, searchResult2.getValues().size());
        for (int i = 0; i < count; i++) {
            User user1 = searchResult1.getValues().get(i);
            User user2 = searchResult2.getValues().get(count - i - 1);
            Assert.assertEquals(users
                    .get(Integer.parseInt(user1.getUsername())).toString(),
                    user1.toString());
            Assert.assertEquals(user2.toString(), user1.toString());
        }
        userService.delete(users.get(0).getId());
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 1, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count - 1, searchResult.getValues().size());
        for (int i = 0; i < count - 1; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(Integer.parseInt(user.getUsername()))
                    .toString(), user.toString());
        }
    }

    /**
     * Accuracy test for UserServiceImpl#search with username
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch3() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername("username" + i);
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsername("%username%");
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count, searchResult.getValues().size());
        for (int i = 0; i < count; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
        criteria.setUsername("username1");
        searchResult = userService.search(criteria);
        Assert.assertEquals(1, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(1, searchResult.getValues().size());
        Assert.assertEquals(users.get(1).toString(), searchResult.getValues()
                .get(0).toString());
        criteria.setUsername("%username%");
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals((count - 1) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
    }

    /**
     * Accuracy test for UserServiceImpl#search with organization name
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch4() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername("username" + i);
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setOrganizationName("%organizationName%");
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count, searchResult.getValues().size());
        for (int i = 0; i < count; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
        criteria.setOrganizationName("organizationName9");
        searchResult = userService.search(criteria);
        Assert.assertEquals(1, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(1, searchResult.getValues().size());
        Assert.assertEquals(users.get(1).toString(), searchResult.getValues()
                .get(0).toString());
        criteria.setOrganizationName("%organizationName%");
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals((count - 1) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
    }

    /**
     * Accuracy test for UserServiceImpl#search with roles
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch5() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername("username" + i);
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
            roles.add(user.getRole().getName());
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setRoles(roles);
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count, searchResult.getValues().size());
        for (int i = 0; i < count; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
        criteria.setRoles(new ArrayList<String>());
        searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count, searchResult.getValues().size());
        for (int i = 0; i < count; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
        criteria.setRoles(Arrays.asList("role1"));
        searchResult = userService.search(criteria);
        Assert.assertEquals(1, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(1, searchResult.getValues().size());
        Assert.assertEquals(users.get(1).toString(), searchResult.getValues()
                .get(0).toString());
        criteria.setRoles(roles);
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count, searchResult.getTotal());
        Assert.assertEquals((count - 1) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i).toString(), user.toString());
        }
    }

    /**
     * Accuracy test for UserServiceImpl#search with
     * eligibleToInitiateDataRequests
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch6() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername("username" + i);
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            entityManager.flush();
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
            roles.add(user.getRole().getName());
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setEligibleToInitiateDataRequests(true);
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(3, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(3, searchResult.getValues().size());
        Assert.assertEquals(users.get(1).toString(), searchResult.getValues()
                .get(0).toString());
        Assert.assertEquals(users.get(2).toString(), searchResult.getValues()
                .get(1).toString());
        Assert.assertEquals(users.get(3).toString(), searchResult.getValues()
                .get(2).toString());
        criteria.setEligibleToInitiateDataRequests(false);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 3, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count - 3, searchResult.getValues().size());
        for (int i = 0; i < count - 3; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertFalse(user.isEligibleToInitiateDataRequests());
            Assert.assertEquals(users.get(i == 0 ? i : (i + 3)).toString(),
                    user.toString());
        }
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 3, searchResult.getTotal());
        Assert.assertEquals((count - 3) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i + 3).toString(), user.toString());
        }
    }

    /**
     * Accuracy test for UserServiceImpl#search with
     * eligibleToReceiveDataRequests
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch7() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername("username" + i);
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            entityManager.flush();
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
            roles.add(user.getRole().getName());
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setEligibleToReceiveDataRequests(true);
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(3, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(3, searchResult.getValues().size());
        Assert.assertEquals(users.get(1).toString(), searchResult.getValues()
                .get(0).toString());
        Assert.assertEquals(users.get(2).toString(), searchResult.getValues()
                .get(1).toString());
        Assert.assertEquals(users.get(3).toString(), searchResult.getValues()
                .get(2).toString());
        criteria.setEligibleToReceiveDataRequests(false);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 3, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count - 3, searchResult.getValues().size());
        for (int i = 0; i < count - 3; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertFalse(user.isEligibleToReceiveDataRequests());
            Assert.assertEquals(users.get(i == 0 ? i : (i + 3)).toString(),
                    user.toString());
        }
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 3, searchResult.getTotal());
        Assert.assertEquals((count - 3) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i + 3).toString(), user.toString());
        }
    }

    /**
     * Accuracy test for UserServiceImpl#search with all filter conditions
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSearch8() throws Exception {
        clearDB();
        entityManager.flush();
        int count = 10;
        List<User> users = new ArrayList<User>();
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            User user = getTestUser();
            user.setUsername("username" + i);
            user.getRole().setName("role" + i);
            user.setOrganizationName("organizationName" + (count - i));
            entityManager.persist(user.getRole());
            user = userService.create(user);
            entityManager.flush();
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            user.setEligibleToInitiateDataRequests(i <= 3 && i != 0);
            users.add(user);
            roles.add(user.getRole().getName());
        }
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setRoles(Arrays.asList("role2"));
        criteria.setUsername("username2");
        criteria.setOrganizationName("organizationName8");
        criteria.setEligibleToInitiateDataRequests(true);
        criteria.setEligibleToReceiveDataRequests(true);
        SearchResult<User> searchResult = userService.search(criteria);
        Assert.assertEquals(1, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(1, searchResult.getValues().size());
        Assert.assertEquals(users.get(2).toString(), searchResult.getValues()
                .get(0).toString());
        criteria.setRoles(Arrays.asList("role1"));
        criteria.setUsername("username1");
        criteria.setOrganizationName("organizationName9");
        criteria.setEligibleToInitiateDataRequests(true);
        criteria.setEligibleToReceiveDataRequests(true);
        searchResult = userService.search(criteria);
        Assert.assertEquals(1, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(1, searchResult.getValues().size());
        Assert.assertEquals(users.get(1).toString(), searchResult.getValues()
                .get(0).toString());
        criteria.setRoles(roles);
        criteria.setUsername("%username%");
        criteria.setOrganizationName("%organizationName%");
        criteria.setEligibleToInitiateDataRequests(false);
        criteria.setEligibleToReceiveDataRequests(false);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 3, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(count - 3, searchResult.getValues().size());
        for (int i = 0; i < count - 3; i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i == 0 ? i : (i + 3)).toString(),
                    user.toString());
        }
        criteria.setPageNumber(2);
        criteria.setPageSize(3);
        searchResult = userService.search(criteria);
        Assert.assertEquals(count - 3, searchResult.getTotal());
        Assert.assertEquals((count - 3) / criteria.getPageSize() + 1,
                searchResult.getTotalPages());
        Assert.assertEquals(criteria.getPageSize(), searchResult.getValues()
                .size());
        for (int i = criteria.getPageSize() * (criteria.getPageNumber() - 1); i < criteria
                .getPageSize(); i++) {
            User user = searchResult.getValues().get(i);
            Assert.assertEquals(users.get(i + 3).toString(), user.toString());
        }
        criteria.setEligibleToInitiateDataRequests(true);
        criteria.setEligibleToReceiveDataRequests(true);
        searchResult = userService.search(criteria);
        Assert.assertEquals(3, searchResult.getTotal());
        Assert.assertEquals(1, searchResult.getTotalPages());
        Assert.assertEquals(0, searchResult.getValues().size());
    }

    /**
     * Failure test for UserServiceImpl#search with criteria is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchFail1() throws Exception {
        userService.search(null);
    }

    /**
     * Failure test for UserServiceImpl#search with criteria page number
     * positive and page size not positive
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchFail2() throws Exception {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setPageNumber(2);
        criteria.setPageSize(-1);
        userService.search(criteria);
    }

    /**
     * Accuracy test for UserServiceImpl#setPassword
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testSetPassword() throws Exception {
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        String password = "password";
        String id = testUser.getId();
        userService.setPassword(id, password);
        entityManager.flush();
        Query query2 = entityManager
                .createNativeQuery("select password_hash from  user where id = :id");
        query2.setParameter("id", id);
        Assert.assertTrue(passwordEncryptor.checkPassword(password,
                (String) query2.getSingleResult()));
    }

    /**
     * Failure test for UserServiceImpl#setPassword when user id is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordFail1() throws Exception {
        String password = "pass";
        userService.setPassword(null, password);
    }

    /**
     * Failure test for UserServiceImpl#setPassword when user id is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordFail2() throws Exception {
        String password = "pass";
        userService.setPassword("", password);
    }

    /**
     * Failure test for UserServiceImpl#setPassword when password is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordFail3() throws Exception {
        String id = "userid";
        userService.setPassword(id, null);
    }

    /**
     * Failure test for UserServiceImpl#setPassword when password is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPasswordFail4() throws Exception {
        String id = "userid";
        userService.setPassword(id, "");
    }

    /**
     * Failure test for UserServiceImpl#setPassword when user with given id not
     * exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testSetPasswordFail5() throws Exception {
        userService.setPassword("not exist id", "password");
    }

    /**
     * Failure test for UserServiceImpl#setPassword when set password with
     * deleted user exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testSetPasswordFail6() throws Exception {
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.persist(userForTest.getRole());
        entityManager.flush();
        User testUser = userService.create(userForTest);
        entityManager.flush();
        checkUserExist(testUser);
        String password = "password";
        String id = testUser.getId();
        userService.delete(id);
        entityManager.flush();
        userService.setPassword(id, password);
        entityManager.flush();
    }

    /**
     * Accuracy test for UserServiceImpl#getUserStatistics
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGetUserStatistics() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.persist(userForTest);
        UserStatistics ust = new UserStatistics();
        ust.setUserId(userForTest.getId());
        ust.setDataRequestsInitiated(1);
        ust.setDataRequestsReceived(2);
        ust.setDataRequestsResponded(3);
        entityManager.persist(ust);
        entityManager.flush();
        UserStatistics userStatistics = userService.getUserStatistics(userForTest
                .getId());
        Assert.assertEquals(ust.toString(), userStatistics.toString());
    }

    /**
     * Failure test for UserServiceImpl#getUserStatistics when id is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserStatisticsFail1() throws Exception {
        userService.getUserStatistics(null);
    }

    /**
     * Failure test for UserServiceImpl#getUserStatistics when id is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserStatisticsFail2() throws Exception {
        userService.getUserStatistics("");
    }

    /**
     * Failure test for UserServiceImpl#getUserStatistics when user with given
     * id not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testGetUserStatisticsFail3() throws Exception {
        userService.getUserStatistics("not exist Id");
    }

    /**
     * Failure test for UserServiceImpl#getUserStatistics when user deleted
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testGetUserStatisticsFail4() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.persist(userForTest);
        UserStatistics ust = new UserStatistics();
        ust.setUserId(userForTest.getId());
        ust.setDataRequestsInitiated(1);
        ust.setDataRequestsReceived(2);
        ust.setDataRequestsResponded(3);
        entityManager.persist(ust);
        entityManager.flush();
        UserStatistics userStatistics = userService.getUserStatistics(userForTest
                .getId());
        Assert.assertEquals(ust.toString(), userStatistics.toString());
        userService.delete(userForTest.getId());
        userService.getUserStatistics(userForTest.getId());
    }

    /**
     * Accuracy test for UserServiceImpl#isParticipationRatioLow
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testIsParticipationRatioLow() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.persist(userForTest);
        UserStatistics ust = new UserStatistics();
        ust.setUserId(userForTest.getId());
        ust.setDataRequestsInitiated(0);
        ust.setDataRequestsReceived(2);
        ust.setDataRequestsResponded(-1);
        entityManager.persist(ust);
        entityManager.flush();
        Assert.assertFalse(userService.isParticipationRatioLow(userForTest.getId()));
        ust.setDataRequestsInitiated(1);
        entityManager.merge(ust);
        Assert.assertTrue(userService.isParticipationRatioLow(userForTest.getId()));
    }

    /**
     * Failure test for UserServiceImpl#isParticipationRatioLow when id is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsParticipationRatioLowFail1() throws Exception {
        userService.isParticipationRatioLow(null);
    }

    /**
     * Failure test for UserServiceImpl#isParticipationRatioLow when id is empty
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsParticipationRatioLowFail2() throws Exception {
        userService.isParticipationRatioLow("");
    }

    /**
     * Failure test for UserServiceImpl#isParticipationRatioLow when user with
     * given id not exist
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testIsParticipationRatioLowFail3() throws Exception {
        userService.isParticipationRatioLow("not exist id");
    }

    /**
     * Failure test for UserServiceImpl#isParticipationRatioLow when user
     * deleted
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = EntityNotFoundException.class)
    public void testIsParticipationRatioLowFail4() throws Exception {
        entityManager.persist(userForTest.getRole());
        entityManager.persist(userForTest);
        UserStatistics ust = new UserStatistics();
        ust.setUserId(userForTest.getId());
        ust.setDataRequestsInitiated(0);
        ust.setDataRequestsReceived(2);
        ust.setDataRequestsResponded(-1);
        entityManager.persist(ust);
        entityManager.flush();
        Assert.assertFalse(userService.isParticipationRatioLow(userForTest.getId()));
        ust.setDataRequestsInitiated(1);
        entityManager.merge(ust);
        Assert.assertTrue(userService.isParticipationRatioLow(userForTest.getId()));
        userService.delete(userForTest.getId());
        userService.isParticipationRatioLow(userForTest.getId());
    }

    /**
     * Check User exist in db.
     * 
     * @param testUser
     *            the user to test
     */
    private void checkUserExist(User testUser) {
        checkUser(testUser, true);
    }

    /**
     * Check User not exist in db.
     * 
     * @param testUser
     *            the user to test
     */
    private void checkUserNotExist(User testUser) {
        checkUser(testUser, false);
    }

    /**
     * Check user exist or not exist in db
     * 
     * @param testUser
     *            the user to test
     * @param exist
     *            the exist flag
     */
    private void checkUser(User testUser, boolean exist) {
        Query query = entityManager
                .createNativeQuery("select count(*) from user a where a.id=:id and a.username=:username "
                        + "and a.organization_name=:organizationName and "
                        + "a.auto_retrieve_cached_data=:autoRetrieveCachedData"
                        + " and a.role_id=:roleId and a.is_deleted=0");
        query.setParameter("id", testUser.getId());
        query.setParameter("username", testUser.getUsername());
        query.setParameter("organizationName", testUser.getOrganizationName());
        query.setParameter("autoRetrieveCachedData",
                testUser.isAutoRetrieveCachedData());
        query.setParameter("roleId", testUser.getRole().getId());
        Assert.assertEquals(exist ? 1 : 0,
                ((Number) query.getSingleResult()).intValue());
    }

    /**
     * Check user history exist in db.
     * 
     * @param testUser
     *            the user to test.
     */
    private void checkUserHistoryExist(User testUser) {
        checkUserHistoryExist(testUser, false);
    }

    /**
     * Check user history exist in db.
     * 
     * @param testUser
     *            the user to test.
     * @param deleted
     *            the deleted log flag
     */
    private void checkUserHistoryExist(User testUser, boolean deleted) {
        Query query = entityManager
                .createNativeQuery("select count(*) from h_user a where a.original_id=:id and "
                        + "a.username=:username and a.organization_name=:organizationName and "
                        + "a.auto_retrieve_cached_data=:autoRetrieveCachedData and a.role_id=:roleId " 
                        + "and a.is_deleted=:deleted");
        query.setParameter("id", testUser.getId());
        query.setParameter("username", testUser.getUsername());
        query.setParameter("organizationName", testUser.getOrganizationName());
        query.setParameter("autoRetrieveCachedData",
                testUser.isAutoRetrieveCachedData());
        query.setParameter("roleId", testUser.getRole().getId());
        query.setParameter("deleted", deleted);
        Assert.assertEquals(1, ((Number) query.getSingleResult()).intValue());
    }
}
