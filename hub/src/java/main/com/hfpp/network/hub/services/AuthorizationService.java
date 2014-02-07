/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * The AuthorizationService is used to check authorization.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface AuthorizationService {
    /**
     * This method is used to check if a user is authorized to perform an operation.
     *
     * @param userId
     *            the user ID
     * @param operation
     *            the operation name
     *
     * @throws IllegalArgumentException
     *             if userId or operation is null or empty
     * @throws AuthorizationException
     *             if the user isn't authorized to perform the operation
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void checkAuthorization(String userId, String operation) throws NetworkHubServiceException;
}
