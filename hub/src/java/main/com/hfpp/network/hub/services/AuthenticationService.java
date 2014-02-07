/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import com.hfpp.network.models.User;

/**
 * <p>
 * The AuthenticationService is used to authenticate user.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface AuthenticationService {
    /**
     * This method is used to authenticate user.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     *
     * @return the authenticated user
     *
     * @throws IllegalArgumentException
     *             if username or password is null or empty string
     * @throws AuthenticationException
     *             if the user fails the authentication
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public User authenticate(String username, String password) throws NetworkHubServiceException;
}
