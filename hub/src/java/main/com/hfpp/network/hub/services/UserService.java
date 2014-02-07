/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import com.hfpp.network.models.SearchResult;
import com.hfpp.network.models.User;
import com.hfpp.network.models.UserSearchCriteria;
import com.hfpp.network.models.UserStatistics;

/**
 * <p>
 * The UserService is used to manage users. Besides simple CRUD operations, the following methods are also provided:
 * <ul>
 * <li>setPassword</li>
 * <li>getUserStatistics</li>
 * <li>isParticipationRatioLow</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> Implementations need to be effectively thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public interface UserService {
    /**
     * This method is used to create a user.
     *
     * @param user
     *            the user
     * @return the created user.
     *
     * @throws IllegalArgumentException
     *             if user is null
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public User create(User user) throws NetworkHubServiceException;

    /**
     * This method is used to update a user.
     *
     * @param user
     *            the user
     *
     * @return the updated user.
     *
     * @throws IllegalArgumentException
     *             if user is null
     * @throws EntityNotFoundException
     *             if the user to update doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public User update(User user) throws NetworkHubServiceException;

    /**
     * This method is used to get a user by user ID.
     *
     * @param id
     *            the user ID
     *
     * @return the user, null will be returned if there's no such user.
     *
     * @throws IllegalArgumentException
     *             if id is null or empty string.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public User get(String id) throws NetworkHubServiceException;

    /**
     * This method is used to get a user by username.
     *
     * @param username
     *            the username
     *
     * @return the user, null will be returned if there's no such user.
     *
     * @throws IllegalArgumentException
     *             if username is null or empty string.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public User getByUsername(String username) throws NetworkHubServiceException;

    /**
     * This method is used to delete a user by user ID.
     *
     * @param id
     *            the user ID
     *
     * @throws IllegalArgumentException
     *             if id is null or empty string.
     * @throws EntityNotFoundException
     *             if the user to delete doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void delete(String id) throws NetworkHubServiceException;

    /**
     * This method is used to search users.
     *
     * @param criteria
     *            the search criteria
     *
     * @return the search result.
     *
     * @throws IllegalArgumentException
     *             if criteria is null or criteria.pageSize &lt;= 0 when criteria.pageNumber &gt; 0.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public SearchResult<User> search(UserSearchCriteria criteria) throws NetworkHubServiceException;

    /**
     * This method is used to set user's password.
     *
     * @param id
     *            the user ID
     * @param password
     *            the new password
     *
     * @throws IllegalArgumentException
     *             if id or password is null or empty string.
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void setPassword(String id, String password) throws NetworkHubServiceException;

    /**
     * This method is used to get a user's statistics.
     *
     * @param userId
     *            the user ID
     *
     * @return the user's statistics
     *
     * @throws IllegalArgumentException
     *             if userId is null or empty string.
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public UserStatistics getUserStatistics(String userId) throws NetworkHubServiceException;

    /**
     * This method is used to check if a user's participation ratio is lower than a threshold.
     *
     * @param userId
     *            the user ID
     *
     * @return true if the user's participation ratio is lower than a threshold, false otherwise.
     *
     * @throws IllegalArgumentException
     *             if userId is null or empty string.
     * @throws EntityNotFoundException
     *             if the user doesn't exist in persistence
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public boolean isParticipationRatioLow(String userId) throws NetworkHubServiceException;
}
