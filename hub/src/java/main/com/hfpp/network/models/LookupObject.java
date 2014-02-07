/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

/**
 * <p>
 * This is the base class for a look up entity with a unique name.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public abstract class LookupObject extends IdentifiableObject {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -4841495573467875005L;
    /**
     * <p>
     * Represents the unique name of the entity.
     * </p>
     */
    private String name;

    /**
     * Creates an instance of LookupObject.
     */
    protected LookupObject() {
        // Empty
    }

    /**
     * Gets the Represents the unique name of the entity.
     *
     * @return the Represents the unique name of the entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Represents the unique name of the entity.
     *
     * @param name
     *            the Represents the unique name of the entity.
     */
    public void setName(String name) {
        this.name = name;
    }
}