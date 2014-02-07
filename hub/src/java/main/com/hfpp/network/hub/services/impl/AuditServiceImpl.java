/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hfpp.network.hub.services.AuditService;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.models.AuditRecord;

/**
 * <p>
 * This is the implementation of AuditService.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>add final to signature</li>
 * <li>add propagation=Propagation.REQUIRES_NEW so when error happen in service
 * will not rollback jms message listener</li>
 * </ul>
 * </p>
 * 
 * @author flying2hk, sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class AuditServiceImpl extends BasePersistenceService implements
        AuditService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = AuditServiceImpl.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Creates an instance of AuditServiceImpl.
     */
    public AuditServiceImpl() {
        // Empty
    }

    /**
     * This method is used to save audit record.
     * 
     * @param record
     *            the audit record
     * 
     * @throws IllegalArgumentException
     *             if record is null
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    @Transactional(
            rollbackFor = Exception.class,
            propagation = Propagation.REQUIRES_NEW)
    public void audit(AuditRecord record) throws NetworkHubServiceException {
        final String signature = CLASS_NAME + ".audit(AuditRecord record)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "record" },
                new Object[] { record });

        Helper.checkNull(LOGGER, signature, record, "record");

        try {
            // Set ID and timestamp
            record.setId(UUID.randomUUID().toString());
            record.setTimestamp(new Date());
            // Save in persistence
            EntityManager entityManager = getEntityManager();
            entityManager.persist(record);

            // Log exit
            Helper.logExit(LOGGER, signature, null);
        } catch (IllegalStateException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "The entity manager has been closed.", e));
        } catch (PersistenceException e) {
            // Log exception
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new NetworkHubServiceException(
                                    "An error has occurred while accessing the persistence.",
                                    e));
        }
    }
}
