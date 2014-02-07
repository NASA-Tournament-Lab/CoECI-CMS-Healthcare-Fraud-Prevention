/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.services;

/**
 * <p>
 * This exception will be thrown by Network Node service to indicate data request expired error.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class DataRequestExpiredException extends NetworkNodeServiceException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -6457022315062945930L;

    /**
     * <p>
     * Constructs a new <code>DataRequestExpiredException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public DataRequestExpiredException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>DataRequestExpiredException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public DataRequestExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
