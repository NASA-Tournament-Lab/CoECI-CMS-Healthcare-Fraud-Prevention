/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node;

/**
 * <p>
 * This exception will be thrown to indicate any configuration error.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class ConfigurationException extends RuntimeException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -8070695066610337708L;

    /**
     * <p>
     * Constructs a new <code>ConfigurationException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>ConfigurationException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
