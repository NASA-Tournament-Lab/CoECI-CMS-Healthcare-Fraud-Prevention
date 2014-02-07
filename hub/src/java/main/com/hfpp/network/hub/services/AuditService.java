/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import com.hfpp.network.models.AuditRecord;

/**
 * <p>
 * The AuditService is used to save audit records.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface AuditService {
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
    public void audit(AuditRecord record) throws NetworkHubServiceException;
}
