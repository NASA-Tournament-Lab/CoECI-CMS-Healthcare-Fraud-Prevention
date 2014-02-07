/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

/**
 * <p>
 * This is the message listener that handles Analysis Result messages.
 * This class extends from SinglePurposeMessageListener and currently doesn't
 * provide any additional logic, but the class is introduced for future
 * expansion.
 * </p>
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public class AnalysisResultMessageListener extends SinglePurposeMessageListener {

    /**
     * Empty constructor.
     */
    public AnalysisResultMessageListener() {
    }

}
