/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.dto;

/**
 * <p>
 * Represents a DTO to hold user and user statistics information.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 * 
 * v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
 *      - added dataRequestsDeclined and its getter and setter
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.1
 */
public class UserDTO {
    /**
     * Represents the user ID.
     */
    private String id;

    /**
     * Represents the organization name.
     */
    private String organizationName;

    /**
     * Indicates whether the user is eligible to receive data requests.
     */
    private boolean eligibleToReceiveDataRequest;

    /**
     * Indicates whether the user is eligible to initiate data requests.
     */
    private boolean eligibleToInitiateDataRequest;

    /**
     * Represents the number of data requests the user received.
     */
    private int dataRequestsReceived;

    /**
     * Represents the number of data requests the user responded.
     */
    private int dataRequestsResponded;

    /**
     * Represents the number of data requests the user initiated.
     */
    private int dataRequestsInitiated;

    /**
     * Represents the number of data requests the user declined.
     * @since 1.1
     */
    private int dataRequestsDeclined;
    /**
     * Creates an instance of UserDTO.
     */
    public UserDTO() {

    }

    /**
     * Getter for the id field.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the id field.
     * 
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the organizationName field.
     * 
     * @return the organization name
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Setter for the organizationName field.
     * 
     * @param organizationName
     *            the organization name to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Getter for the eligibleToReceiveDataRequest field.
     * 
     * @return the value indicates whether the user is eligible to receive data
     *         requests.
     */
    public boolean isEligibleToReceiveDataRequest() {
        return eligibleToReceiveDataRequest;
    }

    /**
     * Setter for the eligibleToReceiveDataRequest field.
     * 
     * @param eligibleToReceiveDataRequest
     *            the value indicates whether the user is eligible to receive
     *            data requests to set
     */
    public void setEligibleToReceiveDataRequest(
            boolean eligibleToReceiveDataRequest) {
        this.eligibleToReceiveDataRequest = eligibleToReceiveDataRequest;
    }

    /**
     * Getter for the eligibleToInitiateDataRequest field.
     * 
     * @return the value indicates whether the user is eligible to initiate data
     *         requests.
     */
    public boolean isEligibleToInitiateDataRequest() {
        return eligibleToInitiateDataRequest;
    }

    /**
     * Setter for the eligibleToInitiateDataRequest field.
     * 
     * @param eligibleToInitiateDataRequest
     *            the value indicates whether the user is eligible to initiate
     *            data requests to set
     */
    public void setEligibleToInitiateDataRequest(
            boolean eligibleToInitiateDataRequest) {
        this.eligibleToInitiateDataRequest = eligibleToInitiateDataRequest;
    }

    /**
     * Getter for the dataRequestsReceived field.
     * 
     * @return the number of data requests the user received.
     */
    public int getDataRequestsReceived() {
        return dataRequestsReceived;
    }

    /**
     * Setter for the dataRequestsReceived field.
     * 
     * @param dataRequestsReceived
     *           the number of data requests the user received to set
     */
    public void setDataRequestsReceived(int dataRequestsReceived) {
        this.dataRequestsReceived = dataRequestsReceived;
    }

    /**
     * Getter for the dataRequestsResponded field.
     * 
     * @return the number of data requests the user responded.
     */
    public int getDataRequestsResponded() {
        return dataRequestsResponded;
    }

    /**
     * Setter for the dataRequestsResponded field.
     * 
     * @param dataRequestsResponded
     *            the number of data requests the user responded to set
     */
    public void setDataRequestsResponded(int dataRequestsResponded) {
        this.dataRequestsResponded = dataRequestsResponded;
    }

    /**
     * Getter for the dataRequestsInitiated field.
     * 
     * @return the number of data requests the user initiated.
     */
    public int getDataRequestsInitiated() {
        return dataRequestsInitiated;
    }

    /**
     * Setter for the dataRequestsInitiated field.
     * 
     * @param dataRequestsInitiated
     *            the number of data requests the user initiated to set
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
}
