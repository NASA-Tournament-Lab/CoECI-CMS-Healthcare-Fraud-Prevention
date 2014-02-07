/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network;

import javax.jms.Destination;

import org.apache.qpid.client.AMQAnyDestination;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class help to create queues in Qpid broker.
 * 
 * v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
 *      - update testCreateQueue to avoid block when the queues already exist
 * 
 * @author TCSASSEMBLER
 * @version 1.1
 */
public class CreateQueueTest extends BaseTestCase {

    /**
     * Represents the jmsTemplate
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Create queue in Qpid java broker
     * 
     * @throws Exception
     *             throws if any error happen
     */
    @Test
    public void testCreateQueue() throws Exception {
        entityManager.joinTransaction();
        String[] queues = new String[] {
                "hfpp.queue.analysis_results.091f80d7-8ecb-429c-8f0b-caeaae18dcd8",
                "hfpp.queue.analysis_results.091f80d7-8ecb-429c-8f0b-caeaae18dcd9",
                "hfpp.queue.analysis_results.091f80d7-8ecb-429c-8f0b-caeaae18dcda",
                "hfpp.queue.data_responses.091f80d7-8ecb-429c-8f0b-caeaae18dcd8",
                "hfpp.queue.data_responses.091f80d7-8ecb-429c-8f0b-caeaae18dcd9",
                "hfpp.queue.data_responses.091f80d7-8ecb-429c-8f0b-caeaae18dcda",
                "hfpp.queue.data_requests.091f80d7-8ecb-429c-8f0b-caeaae18dcd8",
                "hfpp.queue.data_requests.091f80d7-8ecb-429c-8f0b-caeaae18dcd9",
                "hfpp.queue.data_requests.091f80d7-8ecb-429c-8f0b-caeaae18dcda",
                "hfpp.queue.data_requests",
                "hfpp.queue.data_responses",
                "hfpp.queue.analysis_results",
                "hfpp.queue.general_services"};
        for (String queue : queues) {
            String destinationName = queue
                    + "; {create: always, node:{type:queue,durable:True}}";
            jmsTemplate.convertAndSend(destinationName, "create "+queue);
            
            //Destination destination = new AMQAnyDestination("ADDR:" + queue
            //        + "; {create: never, delete: never}");
            //receive create queue message
            //jmsTemplate.receive(destination);
        }
        entityManager.flush();
    }

}
