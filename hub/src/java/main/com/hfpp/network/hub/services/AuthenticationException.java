/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * This exception will be thrown by AuthenticationService to indicate authentication error.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class AuthenticationException extends NetworkHubServiceException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -8383015480549409897L;

    /**
     * <p>
     * Constructs a new <code>AuthenticationException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>AuthenticationException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
