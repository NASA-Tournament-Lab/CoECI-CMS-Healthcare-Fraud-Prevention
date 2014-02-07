/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

import java.util.List;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * The SearchResult&lt;T&gt; class represents a paged search result.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 *
 * @param <T>
 *            the data type
 */
public class SearchResult<T> {
    /**
     * <p>
     * Represents the total number of results.
     * </p>
     */
    private int total;
    /**
     * <p>
     * Represents the total number of pages.
     * </p>
     */
    private int totalPages;
    /**
     * <p>
     * Represents the values.
     * </p>
     */
    private List<T> values;

    /**
     * Creates an instance of SearchResult.
     */
    public SearchResult() {
        // Empty
    }

    /**
     * Gets the Represents the total number of results.
     *
     * @return the Represents the total number of results.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the Represents the total number of results.
     *
     * @param total
     *            the Represents the total number of results.
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the Represents the total number of pages.
     *
     * @return the Represents the total number of pages.
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the Represents the total number of pages.
     *
     * @param totalPages
     *            the Represents the total number of pages.
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Gets the Represents the values.
     *
     * @return the Represents the values.
     */
    public List<T> getValues() {
        return values;
    }

    /**
     * Sets the Represents the values.
     *
     * @param values
     *            the Represents the values.
     */
    public void setValues(List<T> values) {
        this.values = values;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Helper.toString(getClass().getName(),
            new String[] {"total", "totalPages", "values"},
            new Object[] {total, totalPages, values});
    }
}