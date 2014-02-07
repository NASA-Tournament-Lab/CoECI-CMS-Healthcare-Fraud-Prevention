/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services;

import com.hfpp.network.models.DataRequest;
import com.hfpp.network.models.DataResponse;

/**
 * <p>
 * The DataExchangeService is used to
 * <ul>
 * <li>add data request to the network</li>
 * <li>get data request by ID</li>
 * <li>add data response to the network</li>
 * <li>deliver analysis result to the network</li>
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
public interface DataExchangeService {
    /**
     * This method is used to add data request to the network.
     *
     * @param request
     *            the data request
     *
     * @throws IllegalArgumentException
     *             if request is null
     * @throws InsufficientParticipationRatioException
     *             if the requester/original requester's participation ratio is low
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void addDataRequest(DataRequest request) throws NetworkHubServiceException;

    /**
     * This method is used to get a data request by ID.
     *
     * @param id
     *            the data request ID
     *
     * @return the data request, null will be returned if there's no such data request.
     *
     * @throws IllegalArgumentException
     *             if id is null or empty string.
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public DataRequest getDataRequest(String id) throws NetworkHubServiceException;

    /**
     * This method is used to add data response to the network.
     *
     * @param response
     *            the data response
     *
     * @throws IllegalArgumentException
     *             if response is null
     * @throws EntityNotFoundException
     *             if there is no corresponding request identified requestId
     * @throws DataRequestExpiredException
     *             if the request to respond has expired
     * @throws AuthorizationException
     *             if the respondent isn't on the requested partner list of the request
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void addDataResponse(DataResponse response) throws NetworkHubServiceException;

    /**
     * This method is used to deliver analysis result to the network.
     *
     * @param userId
     *            the user initiates this analysis result delivery
     * @param requestId
     *            the data request ID
     * @param result
     *            the analysis result to deliver
     *
     * @throws IllegalArgumentException
     *             if userId, requestId or result is null or empty string.
     * @throws EntityNotFoundException
     *             if there is no corresponding request identified requestId
     * @throws AuthorizationException
     *             if the user isn't the requester of the request
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    public void deliverAnalysisResult(String userId, String requestId, String result) throws NetworkHubServiceException;
}
