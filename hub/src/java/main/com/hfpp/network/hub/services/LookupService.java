/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import java.util.List;

import com.hfpp.network.models.Role;

/**
 * <p>
 * The LookupService is used to retrieve lookup entities.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface LookupService {
    /**
     * This method is used to get all roles.
     *
     * @return all roles, empty list will be returned if no roles.
     *
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public List<Role> getAllRoles() throws NetworkHubServiceException;
}
