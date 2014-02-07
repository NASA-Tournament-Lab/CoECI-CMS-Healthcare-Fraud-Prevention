/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.io.Serializable;

/**
 * <p>
 * This is the base class for an entity with an ID.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public abstract class IdentifiableObject implements Serializable {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -5704234774530724927L;
    /**
     * <p>
     * Represents the ID of the entity, it must be a UUID.
     * </p>
     */
    private String id;

    /**
     * Creates an instance of IdentifiableObject.
     */
    protected IdentifiableObject() {
        // Empty
    }

    /**
     * Gets the Represents the ID of the entity, it must be a UUID.
     *
     * @return the Represents the ID of the entity, it must be a UUID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the Represents the ID of the entity, it must be a UUID.
     *
     * @param id
     *            the Represents the ID of the entity, it must be a UUID.
     */
    public void setId(String id) {
        this.id = id;
    }
}