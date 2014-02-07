/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.util.List;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class is the concrete search criteria for user.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class UserSearchCriteria extends BaseSearchCriteria {
    /**
     * <p>
     * Represents the username to match. It will be used to do a LIKE match for username.
     * </p>
     */
    private String username;
    /**
     * <p>
     * Represents the organization name to match. It will be used to do a LIKE match for organization name.
     * </p>
     */
    private String organizationName;
    /**
     * <p>
     * Represents the role names to match. It will be used to do an IN match for roles.
     * </p>
     */
    private List<String> roles;
    /**
     * <p>
     * Represents "Eligible to receive data requests" option. If it is null, then no specific filtering based on
     * "Eligible to receive data requests" option. If it is true, then only users that are eligible to receive data
     * requests will be included. If it is false, then only users that are not eligible to receive data requests will be
     * included.
     * </p>
     */
    private Boolean eligibleToReceiveDataRequests;
    /**
     * <p>
     * Represents "Eligible to initiate data requests" option. If it is null, then no specific filtering based on
     * "Eligible to initiate data requests" option. If it is true, then only users that are eligible to initiate data
     * requests will be included. If it is false, then only users that are not eligible to initiate data requests will
     * be included.
     * </p>
     */
    private Boolean eligibleToInitiateDataRequests;

    /**
     * Creates an instance of UserSearchCriteria.
     */
    public UserSearchCriteria() {
        // Empty
    }

    /**
     * Gets the Represents the username to match. It will be used to do a LIKE match for username.
     *
     * @return the Represents the username to match. It will be used to do a LIKE match for username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the Represents the username to match. It will be used to do a LIKE match for username.
     *
     * @param username
     *            the Represents the username to match. It will be used to do a LIKE match for username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the Represents the organization name to match. It will be used to do a LIKE match for organization name.
     *
     * @return the Represents the organization name to match. It will be used to do a LIKE match for organization name.
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the Represents the organization name to match. It will be used to do a LIKE match for organization name.
     *
     * @param organizationName
     *            the Represents the organization name to match. It will be used to do a LIKE match for organization
     *            name.
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the Represents the role names to match. It will be used to do an IN match for roles.
     *
     * @return the Represents the role names to match. It will be used to do an IN match for roles.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the Represents the role names to match. It will be used to do an IN match for roles.
     *
     * @param roles
     *            the Represents the role names to match. It will be used to do an IN match for roles.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Gets the Represents "Eligible to receive data requests" option. If it is null, then no specific filtering based
     * on "Eligible to receive data requests" option. If it is true, then only users that are eligible to receive data
     * requests will be included. If it is false, then only users that are not eligible to receive data requests will be
     * included.
     *
     * @return the Represents "Eligible to receive data requests" option. If it is null, then no specific filtering
     *         based on "Eligible to receive data requests" option. If it is true, then only users that are eligible to
     *         receive data requests will be included. If it is false, then only users that are not eligible to receive
     *         data requests will be included.
     */
    public Boolean getEligibleToReceiveDataRequests() {
        return eligibleToReceiveDataRequests;
    }

    /**
     * Sets the Represents "Eligible to receive data requests" option. If it is null, then no specific filtering based
     * on "Eligible to receive data requests" option. If it is true, then only users that are eligible to receive data
     * requests will be included. If it is false, then only users that are not eligible to receive data requests will be
     * included.
     *
     * @param eligibleToReceiveDataRequests
     *            the Represents "Eligible to receive data requests" option. If it is null, then no specific filtering
     *            based on "Eligible to receive data requests" option. If it is true, then only users that are eligible
     *            to receive data requests will be included. If it is false, then only users that are not eligible to
     *            receive data requests will be included.
     */
    public void setEligibleToReceiveDataRequests(Boolean eligibleToReceiveDataRequests) {
        this.eligibleToReceiveDataRequests = eligibleToReceiveDataRequests;
    }

    /**
     * Gets the Represents "Eligible to initiate data requests" option. If it is null, then no specific filtering based
     * on "Eligible to initiate data requests" option. If it is true, then only users that are eligible to initiate data
     * requests will be included. If it is false, then only users that are not eligible to initiate data requests will
     * be included.
     *
     * @return the Represents "Eligible to initiate data requests" option. If it is null, then no specific filtering
     *         based on "Eligible to initiate data requests" option. If it is true, then only users that are eligible to
     *         initiate data requests will be included. If it is false, then only users that are not eligible to
     *         initiate data requests will be included.
     */
    public Boolean getEligibleToInitiateDataRequests() {
        return eligibleToInitiateDataRequests;
    }

    /**
     * Sets the Represents "Eligible to initiate data requests" option. If it is null, then no specific filtering based
     * on "Eligible to initiate data requests" option. If it is true, then only users that are eligible to initiate data
     * requests will be included. If it is false, then only users that are not eligible to initiate data requests will
     * be included.
     *
     * @param eligibleToInitiateDataRequests
     *            the Represents "Eligible to initiate data requests" option. If it is null, then no specific filtering
     *            based on "Eligible to initiate data requests" option. If it is true, then only users that are eligible
     *            to initiate data requests will be included. If it is false, then only users that are not eligible to
     *            initiate data requests will be included.
     */
    public void setEligibleToInitiateDataRequests(Boolean eligibleToInitiateDataRequests) {
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
            new String[] {"pageSize", "pageNumber", "sortBy", "sortType", "username",
                "organizationName", "roles", "eligibleToReceiveDataRequests",
                "eligibleToInitiateDataRequests"},
            new Object[] {getPageSize(), getPageNumber(), getSortBy(), getSortType(), getUsername(),
                getOrganizationName(), getRoles(), getEligibleToReceiveDataRequests(),
                getEligibleToInitiateDataRequests()});
    }
}