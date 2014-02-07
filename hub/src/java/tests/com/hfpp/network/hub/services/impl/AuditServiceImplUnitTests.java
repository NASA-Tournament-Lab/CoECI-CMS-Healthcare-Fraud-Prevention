/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import javax.persistence.EntityManager;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.hfpp.network.BaseUnitTests;
import com.hfpp.network.hub.services.AuditService;
import com.hfpp.network.models.AuditRecord;

/**
 * <p>
 * Unit tests for {@link AuditServiceImpl} class.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>get audit service by spring context from base class in test_Spring</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class AuditServiceImplUnitTests extends BaseUnitTests {
    /**
     * <p>
     * Represents the <code>AuditServiceImpl</code> instance used in tests.
     * </p>
     */
    private AuditServiceImpl instance;

    /**
     * <p>
     * Represents entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Represents record used in tests.
     * </p>
     */
    private AuditRecord record;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     * 
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AuditServiceImplUnitTests.class);
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

        instance = new AuditServiceImpl();

        entityManager = getEntityManager();

        instance.setEntityManager(entityManager);

        instance.checkConfiguration();

        record = new AuditRecord();
        record.setId("id1");
        record.setTimestamp(new Date());
        record.setUserId("1");
        record.setAction("action1");
        record.setDenied(true);
        record.setMessage("message1");
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>AuditServiceImpl()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new AuditServiceImpl();

        assertNull("'entityManager' should be correct.",
                BaseUnitTests.getField(instance, "entityManager"));
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
        AuditService service = (AuditService) APP_CONTEXT
                .getBean("auditService");

        service.audit(record);
    }

    /**
     * <p>
     * Accuracy test for the method <code>audit(AuditRecord record)</code>.<br>
     * The result should be correct.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_audit() throws Exception {
        entityManager.getTransaction().begin();
        instance.audit(record);
        entityManager.getTransaction().commit();

        String id = record.getId();

        AuditRecord retrievedRecord = getEntityManager().find(
                AuditRecord.class, id);
        assertNotNull("'audit' should be correct.",
                retrievedRecord.getTimestamp());
        assertEquals("'audit' should be correct.", record.getUserId(),
                retrievedRecord.getUserId());
        assertEquals("'audit' should be correct.", record.getAction(),
                retrievedRecord.getAction());
        assertEquals("'audit' should be correct.", record.isDenied(),
                retrievedRecord.isDenied());
        assertEquals("'audit' should be correct.", record.getMessage(),
                retrievedRecord.getMessage());
    }
}