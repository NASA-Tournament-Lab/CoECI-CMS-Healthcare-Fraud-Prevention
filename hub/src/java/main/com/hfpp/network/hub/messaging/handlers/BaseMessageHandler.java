/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging.handlers;

import javax.annotation.PostConstruct;

import com.hfpp.network.hub.messaging.MessageHandler;

/**
 * <p>
 * This is the base class for MessageHandler implementations.
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
public abstract class BaseMessageHandler implements MessageHandler {
    /**
     * Empty constructor.
     */
    protected BaseMessageHandler() {
    }

    /**
     * Check if all required fields are initialized properly.
     */
    @PostConstruct
    public void checkConfiguration() {
    }
}
