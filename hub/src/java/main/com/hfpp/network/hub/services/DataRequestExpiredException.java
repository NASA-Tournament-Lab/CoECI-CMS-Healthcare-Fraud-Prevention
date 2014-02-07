/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * This exception will be thrown by DataExchangeService to indicate that the data request has expired.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class DataRequestExpiredException extends NetworkHubServiceException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = 8367578605499198850L;

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
