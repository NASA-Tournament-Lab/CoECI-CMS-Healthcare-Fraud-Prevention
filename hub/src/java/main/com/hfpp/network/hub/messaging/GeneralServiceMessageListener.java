/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

/**
 * <p>
 * This is the message listener that handles general services messages,
 * currently Partner List Request and Partner Statistics Request messages.
 * 
 * This class extends from MultiPurposeMessageListener and currently doesn't
 * provide any additional logic, but the class is introduced for future
 * expansion.
 * 
 * It will be configured with two handlers
 * PartnerStatisticsRequestMessageHandler and PartnerListRequestMessageHandler
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
public class GeneralServiceMessageListener extends MultiPurposeMessageListener {

    /**
     * Empty constructor.
     */
    public GeneralServiceMessageListener() {
    }
}
