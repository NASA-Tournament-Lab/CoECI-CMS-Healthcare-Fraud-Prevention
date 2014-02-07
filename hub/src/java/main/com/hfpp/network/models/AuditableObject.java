/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.util.Date;

/**
 * <p>
 * This is the base class for data models which need to keep auditing fields (created by user, created date, last
 * modified user, last modified date).
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public abstract class AuditableObject extends IdentifiableObject {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -2180570022703193253L;
    /**
     * <p>
     * Represents the ID of the user that created the entity.
     * </p>
     */
    private String createdBy;
    /**
     * <p>
     * Represents created date time.
     * </p>
     */
    private Date createdDate;
    /**
     * <p>
     * Represents the ID of the user that updated the entity.
     * </p>
     */
    private String updatedBy;
    /**
     * <p>
     * Represents updated date time.
     * </p>
     */
    private Date updatedDate;

    /**
     * Creates an instance of AuditableObject.
     */
    protected AuditableObject() {
        // Empty
    }

    /**
     * Gets the Represents the ID of the user that created the entity.
     *
     * @return the Represents the ID of the user that created the entity.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the Represents the ID of the user that created the entity.
     *
     * @param createdBy
     *            the Represents the ID of the user that created the entity.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the Represents created date time.
     *
     * @return the Represents created date time.
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the Represents created date time.
     *
     * @param createdDate
     *            the Represents created date time.
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets the Represents the ID of the user that updated the entity.
     *
     * @return the Represents the ID of the user that updated the entity.
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the Represents the ID of the user that updated the entity.
     *
     * @param updatedBy
     *            the Represents the ID of the user that updated the entity.
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Gets the Represents updated date time.
     *
     * @return the Represents updated date time.
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Sets the Represents updated date time.
     *
     * @param updatedDate
     *            the Represents updated date time.
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}