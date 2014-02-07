/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub;

/**
 * <p>
 * This exception will be thrown by NetworkHubDaemon to indicate errors while starting or stopping the daemon.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public class NetworkHubDaemonException extends Exception {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -5462640537332005054L;

    /**
     * <p>
     * Constructs a new <code>NetworkHubDaemonException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public NetworkHubDaemonException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>NetworkHubDaemonException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public NetworkHubDaemonException(String message, Throwable cause) {
        super(message, cause);
    }
}
