/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

/**
 * <p>
 * This exception will be thrown by Network Hub MessageListener implementations
 * to indicate recoverable errors that prevented the message being properly
 * handled for one time.
 * 
 * This exception is effectively used to notify the external transaction manager
 * to rollback the transaction and not acknowledge the received message.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its
 * base class is not thread safe.
 * </p>
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public class RecoverableMessageHandlingException extends RuntimeException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -5482640537342005034L;

    /**
     * <p>
     * Constructs a new <code>RecoverableMessageHandlingException</code>
     * instance with error message.
     * </p>
     * 
     * @param message
     *            the error message.
     */
    public RecoverableMessageHandlingException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>RecoverableMessageHandlingException</code>
     * instance with error message and inner cause.
     * </p>
     * 
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public RecoverableMessageHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
