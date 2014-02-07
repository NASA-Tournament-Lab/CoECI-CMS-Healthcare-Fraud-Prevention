/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

/**
 * <p>
 * This exception will be thrown by DataExchangeService to indicate insufficient participation ratio.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Exception is not thread safe because its base class is not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class InsufficientParticipationRatioException extends NetworkHubServiceException {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = 1280702916337415783L;

    /**
     * <p>
     * Constructs a new <code>InsufficientParticipationRatioException</code> instance with error message.
     * </p>
     *
     * @param message
     *            the error message.
     */
    public InsufficientParticipationRatioException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new <code>InsufficientParticipationRatioException</code> instance with error message and inner
     * cause.
     * </p>
     *
     * @param message
     *            the error message.
     * @param cause
     *            the inner cause.
     */
    public InsufficientParticipationRatioException(String message, Throwable cause) {
        super(message, cause);
    }
}
