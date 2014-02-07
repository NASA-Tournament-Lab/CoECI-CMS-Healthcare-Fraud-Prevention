/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.util.Date;
import java.util.List;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class represents a Data Request sent to the Network.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 * 
 * v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
 *      - added studyId and its getter and setter
 *
 * @author flying2hk, sparemax, TCSASSEMBLER
 * @version 1.1
 */
public class DataRequest extends IdentifiableObject {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -6831980216326676834L;
    /**
     * <p>
     * Represents the requester ID.
     * </p>
     */
    private String requesterId;
    
    /**
     * <p>
     * Represents the study ID.
     * </p>
     * @since 1.1
     */
    private String studyId;

    /**
     * <p>
     * Represents the original requester ID.
     * </p>
     */
    private String originalRequesterId;
    /**
     * <p>
     * Represents the query.
     * </p>
     */
    private String query;
    /**
     * <p>
     * Represents the IDs of the requested partners.
     * </p>
     */
    private List<String> requestedPartners;
    /**
     * <p>
     * Represents the expiration time.
     * </p>
     */
    private Date expirationTime;
    /**
     * <p>
     * Indicates whether the data request is cache safe.
     * </p>
     */
    private boolean cacheSafe;

    /**
     * Creates an instance of DataRequest.
     */
    public DataRequest() {
        // Empty
    }

    /**
     * Gets the Represents the requester ID.
     *
     * @return the Represents the requester ID.
     */
    public String getRequesterId() {
        return requesterId;
    }

    /**
     * Sets the Represents the requester ID.
     *
     * @param requesterId
     *            the Represents the requester ID.
     */
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    /**
     * Gets the Represents the original requester ID.
     *
     * @return the Represents the original requester ID.
     */
    public String getOriginalRequesterId() {
        return originalRequesterId;
    }

    /**
     * Sets the Represents the original requester ID.
     *
     * @param originalRequesterId
     *            the Represents the original requester ID.
     */
    public void setOriginalRequesterId(String originalRequesterId) {
        this.originalRequesterId = originalRequesterId;
    }

    /**
     * Gets the Represents the query.
     *
     * @return the Represents the query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the Represents the query.
     *
     * @param query
     *            the Represents the query.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the Represents the IDs of the requested partners.
     *
     * @return the Represents the IDs of the requested partners.
     */
    public List<String> getRequestedPartners() {
        return requestedPartners;
    }

    /**
     * Sets the Represents the IDs of the requested partners.
     *
     * @param requestedPartners
     *            the Represents the IDs of the requested partners.
     */
    public void setRequestedPartners(List<String> requestedPartners) {
        this.requestedPartners = requestedPartners;
    }

    /**
     * Gets the Represents the expiration time.
     *
     * @return the Represents the expiration time.
     */
    public Date getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the Represents the expiration time.
     *
     * @param expirationTime
     *            the Represents the expiration time.
     */
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Gets the Indicates whether the data request is cache safe.
     *
     * @return the Indicates whether the data request is cache safe.
     */
    public boolean isCacheSafe() {
        return cacheSafe;
    }

    /**
     * Sets the Indicates whether the data request is cache safe.
     *
     * @param cacheSafe
     *            the Indicates whether the data request is cache safe.
     */
    public void setCacheSafe(boolean cacheSafe) {
        this.cacheSafe = cacheSafe;
    }
    
    /**
     * Gets the studyId.
     *
     * @return the studyId
     * @since 1.1
     */
    public String getStudyId() {
        return studyId;
    }

    /**
     * Sets the studyId.
     *
     * @param studyId the studyId to set
     * @since 1.1
     */
    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"id", "requesterId", "originalRequesterId", "query", "requestedPartners",
                "expirationTime", "cacheSafe"},
            new Object[] {getId(), requesterId, originalRequesterId, query, requestedPartners,
                expirationTime, cacheSafe});
    }
}