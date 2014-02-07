/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import javax.annotation.PostConstruct;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.DataExchangeService;
import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This is the base class for message handlers related to data exchange.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public abstract class BaseDataExchangeMessageHandler extends BaseMessageHandler {
    /**
     * Represents the data exchange service used to access the Data Exchange Network.
     */
    private DataExchangeService dataExchangeService;

    /**
     * Empty constructor.
     */
    protected BaseDataExchangeMessageHandler() {
    }


    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (dataExchangeService is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkState(dataExchangeService == null, "'dataExchangeService' can't be null.");
    }

    /**
     * Protected getter for the data exchange service  field.
     * @return  the data exchange service
     */
    protected DataExchangeService getDataExchangeService() {
        return dataExchangeService;
    }


    /**
     * Setter for the dataExchangeService field
     * @param dataExchangeService the dataExchangeService to set
     */
    public void setDataExchangeService(DataExchangeService dataExchangeService) {
        this.dataExchangeService = dataExchangeService;
    }
    
}

