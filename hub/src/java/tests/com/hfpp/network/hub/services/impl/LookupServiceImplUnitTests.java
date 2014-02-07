/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Cache;
import javax.persistence.EntityManager;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;
import com.hfpp.network.hub.services.LookupService;
import com.hfpp.network.models.Role;

/**
 * <p>
 * Unit tests for {@link LookupServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get look up service by spring context from base class in test_Spring</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class LookupServiceImplUnitTests extends BaseUnitTests {
    /**
     * <p>
     * Represents the <code>LookupServiceImpl</code> instance used in tests.
     * </p>
     */
    private LookupServiceImpl instance;

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     * 
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LookupServiceImplUnitTests.class);
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

        instance = new LookupServiceImpl();

        entityManager = getEntityManager();

        instance.setEntityManager(entityManager);

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
        LookupService service = (LookupService) APP_CONTEXT
                .getBean("lookupService");

        List<Role> res = service.getAllRoles();
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>LookupServiceImpl()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new LookupServiceImpl();

        assertNull("'entityManager' should be correct.",
                BaseUnitTests.getField(instance, "entityManager"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>getAllRoles()</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getAllRoles_1() throws Exception {
        clearDB();

        List<Role> res = instance.getAllRoles();

        assertEquals("'getAllRoles' should be correct.", 0, res.size());
    }

    /**
     * <p>
     * Accuracy test for the method <code>getAllRoles()</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_getAllRoles_2() throws Exception {
        List<Role> res = instance.getAllRoles();

        assertEquals("'getAllRoles' should be correct.", 3, res.size());
        Collections.sort(res, new Comparator<Role>() {
            public int compare(Role o1, Role o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        Cache cache = entityManager.getEntityManagerFactory().getCache();

        Role role = res.get(0);
        assertEquals("'getAllRoles' should be correct.", "1", role.getId());
        assertEquals("'getAllRoles' should be correct.", "role1",
                role.getName());
        assertTrue("'getAllRoles' should be correct.",
                cache.contains(Role.class, role.getId()));

        role = res.get(1);
        assertEquals("'getAllRoles' should be correct.", "2", role.getId());
        assertEquals("'getAllRoles' should be correct.", "role2",
                role.getName());
        assertTrue("'getAllRoles' should be correct.",
                cache.contains(Role.class, role.getId()));

        role = res.get(2);
        assertEquals("'getAllRoles' should be correct.", "3", role.getId());
        assertEquals("'getAllRoles' should be correct.", "role3",
                role.getName());
        assertTrue("'getAllRoles' should be correct.",
                cache.contains(Role.class, role.getId()));
    }
}