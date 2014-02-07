/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.UUID;

import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hfpp.network.BaseTestCase;
import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.impl.AuditServiceImpl;
import com.hfpp.network.models.AuditRecord;
import com.hfpp.network.models.User;

/**
 * Functional tests for {@link AuditService}.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class AuditServiceTest extends BaseTestCase {

    /**
     * Represents the audit service.
     */
    @Autowired
    private AuditService auditService;

    /**
     * Represents the audit service for test.
     */
    private AuditServiceImpl testAuditService;

    /**
     * Prepare audit service for test.
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ((AuditServiceImpl) auditService).setEntityManager(entityManager);
        testAuditService = new AuditServiceImpl();
        testAuditService.setEntityManager(entityManager);
    }

    /**
     * Accuracy test for AuditServiceImpl#checkConfiguration.
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCheckConfiguration() throws Exception {
        testAuditService.checkConfiguration();
    }

    /**
     * Failure test for AuditServiceImpl#checkConfiguration when entity manager
     * is null.
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = ConfigurationException.class)
    public void testCheckConfigurationFail() throws Exception {
        testAuditService.setEntityManager(null);
        testAuditService.checkConfiguration();
    }

    /**
     * Accuracy test for AuditService#audit.
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testAudit() throws Exception {
        AuditRecord record = getAuditRecord();
        User user = getTestUser();
        record.setUserId(user.getId());
        entityManager.persist(user.getRole());
        entityManager.persist(user);
        entityManager.flush();
        TypedQuery<AuditRecord> query = entityManager
                .createQuery(
                        "select a from AuditRecord a where a.message=:message "
                                + "and a.action=:action and a.userId=:userId and denied=:denied",
                        AuditRecord.class);
        query.setParameter("message", record.getMessage());
        query.setParameter("action", record.getAction());
        query.setParameter("userId", record.getUserId());
        query.setParameter("denied", record.isDenied());
        Assert.assertEquals(0, query.getResultList().size());
        auditService.audit(record);
        Assert.assertEquals(1, query.getResultList().size());
    }

    /**
     * Failure test for AuditService#audit when record is null.
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAuditFail1() throws Exception {
        auditService.audit(null);
    }

    /**
     * Failure test for AuditService#audit when record is invalid
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test(expected = NetworkHubServiceException.class)
    public void testAuditFail2() throws Exception {
        AuditRecord record = getAuditRecord();
        record.setAction(null);
        auditService.audit(record);
    }

    /**
     * Prepare test data for audit record
     * 
     * @return test data for audit record
     */
    private AuditRecord getAuditRecord() {
        AuditRecord record = new AuditRecord();
        record.setDenied(false);
        record.setMessage(UUID.randomUUID().toString());
        record.setAction("action");
        record.setUserId("userId");
        return record;
    }
}
