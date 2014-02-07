/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.messaging;

/**
 * <p>
 * This is the message listener that handles "Analysis Result" messages received from
 * hfpp.queue.analysis_results.&lt;partner-id&gt; queue. This class extends from GenericForwardMessageListener and
 * currently doesn't provide any additional logic, but the class is introduced for future expansion. It will be
 * configured with "Analysis Result Callback URL", which should look like
 * https://&lt;sms-service-host&gt;/callbacks/analysis_result
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since its internal state isn't expected to
 * change after Spring IoC initialization, and all dependencies are thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class AnalysisResultMessageListener extends GenericForwardMessageListener {
    /**
     * Creates an instance of AnalysisResultMessageListener.
     */
    public AnalysisResultMessageListener() {
        // Empty
    }
}
