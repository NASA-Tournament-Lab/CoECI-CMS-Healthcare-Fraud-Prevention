/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * This exception will be thrown by Network Hub services to indicate that relevant entity can not be found when updating
 * or deleting an entity.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class EntityNotFoundException extends NetworkHubServiceException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -1228134823358153052L;

    /**
     * <p>
     * Constructs a new <code>EntityNotFoundException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>EntityNotFoundException</code> instance with error message and inner cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
