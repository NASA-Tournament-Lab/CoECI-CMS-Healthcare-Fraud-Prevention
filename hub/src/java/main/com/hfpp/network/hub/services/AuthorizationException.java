/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * This exception will be thrown by Network Hub business services to indicate authorization error - authorization errors
 * could be role-based authorization error, or operation-specific authorization errors (e.g. if the user initiating
 * analysis result delivery isn't the user that imitated corresponding data request).
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class AuthorizationException extends NetworkHubServiceException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -2469189323051735616L;

    /**
     * <p>
     * Constructs a new <code>AuthorizationException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public AuthorizationException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>AuthorizationException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
