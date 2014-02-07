/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * The User class represents a user. A user represents a corporate entity that is enrolled in the HFPP network.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class User extends AuditableObject {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -7285834344857929083L;
    /**
     * <p>
     * Represents the username.
     * </p>
     */
    private String username;
    /**
     * <p>
     * Represents the organization name.
     * </p>
     */
    private String organizationName;
    /**
     * <p>
     * Represents the role.
     * </p>
     */
    private Role role;
    /**
     * <p>
     * Indicates whether the user allows the Network Hub to retrieve cached data without explicitly asking permission
     * each time.
     * </p>
     */
    private boolean autoRetrieveCachedData;
    /**
     * <p>
     * Indicates whether the user is eligible to receive data requests. This field will not be persisted in database.
     * </p>
     */
    private boolean eligibleToReceiveDataRequests;
    /**
     * <p>
     * Indicates whether the user is eligible to initiate data requests. This field will not be persisted in database.
     * </p>
     */
    private boolean eligibleToInitiateDataRequests;

    /**
     * Creates an instance of User.
     */
    public User() {
        // Empty
    }

    /**
     * Gets the Represents the username.
     *
     * @return the Represents the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the Represents the username.
     *
     * @param username
     *            the Represents the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the Represents the organization name.
     *
     * @return the Represents the organization name.
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the Represents the organization name.
     *
     * @param organizationName
     *            the Represents the organization name.
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the Represents the role.
     *
     * @return the Represents the role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the Represents the role.
     *
     * @param role
     *            the Represents the role.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the Indicates whether the user allows the Network Hub to retrieve cached data without explicitly asking
     * permission each time.
     *
     * @return the Indicates whether the user allows the Network Hub to retrieve cached data without explicitly asking
     *         permission each time.
     */
    public boolean isAutoRetrieveCachedData() {
        return autoRetrieveCachedData;
    }

    /**
     * Sets the Indicates whether the user allows the Network Hub to retrieve cached data without explicitly asking
     * permission each time.
     *
     * @param autoRetrieveCachedData
     *            the Indicates whether the user allows the Network Hub to retrieve cached data without explicitly
     *            asking permission each time.
     */
    public void setAutoRetrieveCachedData(boolean autoRetrieveCachedData) {
        this.autoRetrieveCachedData = autoRetrieveCachedData;
    }

    /**
     * Gets the Indicates whether the user is eligible to receive data requests. This field will not be persisted in
     * database.
     *
     * @return the Indicates whether the user is eligible to receive data requests. This field will not be persisted in
     *         database.
     */
    public boolean isEligibleToReceiveDataRequests() {
        return eligibleToReceiveDataRequests;
    }

    /**
     * Sets the Indicates whether the user is eligible to receive data requests. This field will not be persisted in
     * database.
     *
     * @param eligibleToReceiveDataRequests
     *            the Indicates whether the user is eligible to receive data requests. This field will not be persisted
     *            in database.
     */
    public void setEligibleToReceiveDataRequests(boolean eligibleToReceiveDataRequests) {
        this.eligibleToReceiveDataRequests = eligibleToReceiveDataRequests;
    }

    /**
     * Gets the Indicates whether the user is eligible to initiate data requests. This field will not be persisted in
     * database.
     *
     * @return the Indicates whether the user is eligible to initiate data requests. This field will not be persisted in
     *         database.
     */
    public boolean isEligibleToInitiateDataRequests() {
        return eligibleToInitiateDataRequests;
    }

    /**
     * Sets the Indicates whether the user is eligible to initiate data requests. This field will not be persisted in
     * database.
     *
     * @param eligibleToInitiateDataRequests
     *            the Indicates whether the user is eligible to initiate data requests. This field will not be persisted
     *            in database.
     */
    public void setEligibleToInitiateDataRequests(boolean eligibleToInitiateDataRequests) {
        this.eligibleToInitiateDataRequests = eligibleToInitiateDataRequests;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"id", "createdBy", "createdDate", "updatedBy", "updatedDate", "username",
                "organizationName", "role", "autoRetrieveCachedData", "eligibleToReceiveDataRequests",
                "eligibleToInitiateDataRequests"},
            new Object[] {getId(), getCreatedBy(), getCreatedDate(), getUpdatedBy(), getUpdatedDate(), getUsername(),
                getOrganizationName(), getRole(), isAutoRetrieveCachedData(), isEligibleToReceiveDataRequests(),
                isEligibleToInitiateDataRequests()});
    }
}