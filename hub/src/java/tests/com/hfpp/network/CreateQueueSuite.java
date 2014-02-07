/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test suite for this assembly,it help to create queue.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
@Transactional(propagation=Propagation.REQUIRED)
@RunWith(Suite.class)
@SuiteClasses({ CreateQueueTest.class})
public class CreateQueueSuite {

}
