/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.LookupServiceImpl;
import com.hfpp.network.models.Role;

/**
 * Functional tests for {@link LookupService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class LookupServiceTest extends BaseTestCase {
    /**
     * Represents the lookup service
     */
    @Autowired
    private LookupService lookupService;

    /**
     * Represents the lookup service for test
     */
    private LookupServiceImpl testLookupService;

    /**
     * Prepare lookup service for test
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testLookupService = new LookupServiceImpl();
        testLookupService.setEntityManager(entityManager);
    }

    /**
     * Accuracy test for LookupServiceImpl#checkConfiguration
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testLookupService.checkConfiguration();
    }

    /**
     * Failure test for LookupServiceImpl#checkConfiguration when entity manager
     * is null
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail() throws Exception {
        testLookupService.setEntityManager(null);
        testLookupService.checkConfiguration();
    }

    /**
     * Accuracy test for LookupServiceImpl#getAllRoles
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testGetAllRoles() throws Exception {
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.createQuery("delete from Role").executeUpdate();
        entityManager.flush();
        List<Role> roles = lookupService.getAllRoles();
        Assert.assertEquals(0, roles.size());
        Role role1 = new Role();
        role1.setId("role1");
        role1.setName("role1Name");
        entityManager.persist(role1);
        Role role2 = new Role();
        role2.setId("role2");
        role2.setName("role2Name");
        entityManager.persist(role2);
        entityManager.flush();
        roles = lookupService.getAllRoles();
        Assert.assertEquals(2, roles.size());
        for (Role role : roles) {
            if ("role1".equals(role.getId())) {
                Assert.assertEquals("role1Name", role.getName());
            } else if ("role2".equals(role.getId())) {
                Assert.assertEquals("role2Name", role.getName());
            }
        }
    }

}
