/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * The Role lookup entity represents a user role.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class Role extends LookupObject {
    /**
     * <p>
     * The serial version id.
     * </p>
     */
    private static final long serialVersionUID = -4836314305249530859L;

    /**
     * Creates an instance of Role.
     */
    public Role() {
        // Empty
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"id", "name"},
            new Object[] {getId(), getName()});
    }
}