/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.util.Date;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class represents an audit record.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class AuditRecord extends IdentifiableObject {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = 385172184548696571L;
    /**
     * <p>
     * Represents the timestamp.
     * </p>
     */
    private Date timestamp;
    /**
     * <p>
     * Represents the user ID.
     * </p>
     */
    private String userId;
    /**
     * <p>
     * Represents the action.
     * </p>
     */
    private String action;
    /**
     * <p>
     * Indicates whether the action was denied.
     * </p>
     */
    private boolean denied;
    /**
     * <p>
     * Represents the message.
     * </p>
     */
    private String message;

    /**
     * Creates an instance of AuditRecord.
     */
    public AuditRecord() {
        // Empty
    }

    /**
     * Gets the Represents the timestamp.
     *
     * @return the Represents the timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the Represents the timestamp.
     *
     * @param timestamp
     *            the Represents the timestamp.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the Represents the user ID.
     *
     * @return the Represents the user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the Represents the user ID.
     *
     * @param userId
     *            the Represents the user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the Represents the action.
     *
     * @return the Represents the action.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the Represents the action.
     *
     * @param action
     *            the Represents the action.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the Indicates whether the action was denied.
     *
     * @return the Indicates whether the action was denied.
     */
    public boolean isDenied() {
        return denied;
    }

    /**
     * Sets the Indicates whether the action was denied.
     *
     * @param denied
     *            the Indicates whether the action was denied.
     */
    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    /**
     * Gets the Represents the message.
     *
     * @return the Represents the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the Represents the message.
     *
     * @param message
     *            the Represents the message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"id", "timestamp", "userId", "action", "denied", "message"},
            new Object[] {getId(), timestamp, userId, action, denied, message});
    }
}