/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.models;

/**
 * <p>
 * Base class for all search criteria.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and not thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public abstract class BaseSearchCriteria {
    /**
     * <p>
     * Represents the page size.
     * </p>
     */
    private int pageSize;
    /**
     * <p>
     * Represents the page number.
     * </p>
     */
    private int pageNumber;
    /**
     * <p>
     * Represents the sort by field.
     * </p>
     */
    private String sortBy;
    /**
     * <p>
     * Represents the sort type.
     * </p>
     */
    private SortType sortType;

    /**
     * Creates an instance of BaseSearchCriteria.
     */
    protected BaseSearchCriteria() {
        // Empty
    }

    /**
     * Gets the Represents the page size.
     *
     * @return the Represents the page size.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the Represents the page size.
     *
     * @param pageSize
     *            the Represents the page size.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the Represents the page number.
     *
     * @return the Represents the page number.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the Represents the page number.
     *
     * @param pageNumber
     *            the Represents the page number.
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Gets the Represents the sort by field.
     *
     * @return the Represents the sort by field.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the Represents the sort by field.
     *
     * @param sortBy
     *            the Represents the sort by field.
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Gets the Represents the sort type.
     *
     * @return the Represents the sort type.
     */
    public SortType getSortType() {
        return sortType;
    }

    /**
     * Sets the Represents the sort type.
     *
     * @param sortType
     *            the Represents the sort type.
     */
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }
}