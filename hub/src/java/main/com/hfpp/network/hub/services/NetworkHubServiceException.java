/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * This is the base class for all exceptions thrown by Network Hub business services.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class NetworkHubServiceException extends Exception {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = 2631204097300431505L;

    /**
     * <p>
     * Constructs a new <code>NetworkHubServiceException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public NetworkHubServiceException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>NetworkHubServiceException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public NetworkHubServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
