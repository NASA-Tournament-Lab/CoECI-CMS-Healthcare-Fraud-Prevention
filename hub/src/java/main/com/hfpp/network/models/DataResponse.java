/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.io.Serializable;
import java.util.Date;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class represents a Data Response to a Data Request. Note that the "data"
 * field will not be persisted in database.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 * 
 * *
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>override equals and hashCode,this is requirement of composite key entity</li>
 * </ul>
 * </p>
 * 
 * <p>
 * v1.2 Changes(Healthcare Fraud Prevention Release Assembly v1.0):
 * <ul>
 * <li>added errorMessage and its getter and setter
 * </ul>
 * </p>
 * 
 * @author flying2hk, sparemax,TCSASSEMBLER
 * @version 1.2
 */
public class DataResponse implements Serializable {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -8939397065160702782L;
    /**
     * <p>
     * Represents the request ID.
     * </p>
     */
    private String requestId;
    /**
     * <p>
     * Represents the respondent ID.
     * </p>
     */
    private String respondentId;
    /**
     * <p>
     * Represents the response timestamp.
     * </p>
     */
    private Date responseTimestamp;
    /**
     * <p>
     * Represents the response data.
     * </p>
     */
    private String data;    
    /**
     * <p>
     * Indicates whether the response denied the request.
     * </p>
     */
    private boolean requestDenied;
    /**
     * <p>
     * Represents the error message.
     * </p>
     * @since 1.2
     */
    private String errorMessage;

    /**
     * Creates an instance of DataResponse.
     */
    public DataResponse() {
        // Empty
    }

    /**
     * Gets the Represents the request ID.
     * 
     * @return the Represents the request ID.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the Represents the request ID.
     * 
     * @param requestId
     *            the Represents the request ID.
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the Represents the respondent ID.
     * 
     * @return the Represents the respondent ID.
     */
    public String getRespondentId() {
        return respondentId;
    }

    /**
     * Sets the Represents the respondent ID.
     * 
     * @param respondentId
     *            the Represents the respondent ID.
     */
    public void setRespondentId(String respondentId) {
        this.respondentId = respondentId;
    }

    /**
     * Gets the Represents the response timestamp.
     * 
     * @return the Represents the response timestamp.
     */
    public Date getResponseTimestamp() {
        return responseTimestamp;
    }

    /**
     * Sets the Represents the response timestamp.
     * 
     * @param responseTimestamp
     *            the Represents the response timestamp.
     */
    public void setResponseTimestamp(Date responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    /**
     * Gets the Represents the response data.
     * 
     * @return the Represents the response data.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the Represents the response data.
     * 
     * @param data
     *            the Represents the response data.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the Indicates whether the response denied the request.
     * 
     * @return the Indicates whether the response denied the request.
     */
    public boolean isRequestDenied() {
        return requestDenied;
    }

    /**
     * Sets the Indicates whether the response denied the request.
     * 
     * @param requestDenied
     *            the Indicates whether the response denied the request.
     */
    public void setRequestDenied(boolean requestDenied) {
        this.requestDenied = requestDenied;
    }
    
    /**
     * Gets the errorMessage.
     *
     * @return the errorMessage
     * @since 1.2
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the errorMessage.
     *
     * @param errorMessage the errorMessage to set
     * @since 1.2
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Generate the custom hash code.
     * 
     * @return the custom hash code
     * @since 1.1
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result
                + ((respondentId == null) ? 0 : respondentId.hashCode());
        return result;
    }

    /**
     * Compare object by using requestId and respondentId.
     * 
     * @param obj
     *            the object.
     * @return if same requestId and respondentId return true
     * @since 1.1
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataResponse other = (DataResponse) obj;
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        } else if (!requestId.equals(other.requestId)) {
            return false;
        }
        if (respondentId == null) {
            if (other.respondentId != null) {
                return false;
            }
        } else if (!respondentId.equals(other.respondentId)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(), new String[] {
                "requestId", "respondentId", "responseTimestamp", "data",
                "requestDenied" }, new Object[] { requestId, respondentId,
                responseTimestamp, data, requestDenied });
    }
}