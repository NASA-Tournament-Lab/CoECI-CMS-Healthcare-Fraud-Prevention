/*
 * Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.io.Serializable;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class represents statistics for a user, currently the following metrics are collected:
 * <ul>
 * <li>Number of data requests received</li>
 * <li>Number of data requests responded</li>
 * <li>Number of data requests initiated</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 * 
 * v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
 *      - added dataRequestsDeclined and its getter and setter
 *
 * @author flying2hk, sparemax, TCSASSEMBLER
 * @version 1.1
 */
public class UserStatistics implements Serializable {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = 1895645156727448869L;
    /**
     * <p>
     * Represents the user ID.
     * </p>
     */
    private String userId;
    /**
     * <p>
     * Represents the number of data requests received.
     * </p>
     */
    private int dataRequestsReceived;
    /**
     * <p>
     * Represents the number of data requests responded.
     * </p>
     */
    private int dataRequestsResponded;
    /**
     * <p>
     * Represents the number of data requests initiated.
     * </p>
     */
    private int dataRequestsInitiated;
    /**
     * <p>
     * Represents the number of data requests declined.
     * </p>
     * @since 1.1
     */
    private int dataRequestsDeclined;

    /**
     * Creates an instance of UserStatistics.
     */
    public UserStatistics() {
        // Empty
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
     * Gets the Represents the number of data requests received.
     *
     * @return the Represents the number of data requests received.
     */
    public int getDataRequestsReceived() {
        return dataRequestsReceived;
    }

    /**
     * Sets the Represents the number of data requests received.
     *
     * @param dataRequestsReceived
     *            the Represents the number of data requests received.
     */
    public void setDataRequestsReceived(int dataRequestsReceived) {
        this.dataRequestsReceived = dataRequestsReceived;
    }

    /**
     * Gets the Represents the number of data requests responded.
     *
     * @return the Represents the number of data requests responded.
     */
    public int getDataRequestsResponded() {
        return dataRequestsResponded;
    }

    /**
     * Sets the Represents the number of data requests responded.
     *
     * @param dataRequestsResponded
     *            the Represents the number of data requests responded.
     */
    public void setDataRequestsResponded(int dataRequestsResponded) {
        this.dataRequestsResponded = dataRequestsResponded;
    }

    /**
     * Gets the Represents the number of data requests initiated.
     *
     * @return the Represents the number of data requests initiated.
     */
    public int getDataRequestsInitiated() {
        return dataRequestsInitiated;
    }

    /**
     * Sets the Represents the number of data requests initiated.
     *
     * @param dataRequestsInitiated
     *            the Represents the number of data requests initiated.
     */
    public void setDataRequestsInitiated(int dataRequestsInitiated) {
        this.dataRequestsInitiated = dataRequestsInitiated;
    }

    /**
     * Gets the dataRequestsDeclined.
     *
     * @return the dataRequestsDeclined
     * @since 1.1
     */
    public int getDataRequestsDeclined() {
        return dataRequestsDeclined;
    }

    /**
     * Sets the dataRequestsDeclined.
     *
     * @param dataRequestsDeclined the dataRequestsDeclined to set
     * @since 1.1
     */
    public void setDataRequestsDeclined(int dataRequestsDeclined) {
        this.dataRequestsDeclined = dataRequestsDeclined;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"userId", "dataRequestsReceived", "dataRequestsResponded",
                "dataRequestsInitiated"},
            new Object[] {getUserId(), getDataRequestsReceived(), getDataRequestsResponded(),
                getDataRequestsInitiated()});
    }
}