/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hfpp.network.hub.ConfigurationExceptionUnitTests;
import com.hfpp.network.hub.NetworkHubDaemonExceptionUnitTests;
import com.hfpp.network.hub.NetworkHubDaemonTest;
import com.hfpp.network.hub.messaging.AnalysisResultMessageListenerUnitTests;
import com.hfpp.network.hub.messaging.DataRequestMessageListenerUnitTests;
import com.hfpp.network.hub.messaging.DataResponseMessageListenerUnitTests;
import com.hfpp.network.hub.messaging.MultiPurposeMessageListenerUnitTests;
import com.hfpp.network.hub.messaging.RecoverableMessageHandlingExceptionUnitTests;
import com.hfpp.network.hub.messaging.handlers.AnalysisResultMessageHandlerUnitTests;
import com.hfpp.network.hub.messaging.handlers.DataRequestMessageHandlerUnitTests;
import com.hfpp.network.hub.messaging.handlers.DataResponseMessageHandlerUnitTests;
import com.hfpp.network.hub.messaging.handlers.PartnerStatisticsRequestMessageHandlerUnitTests;
import com.hfpp.network.hub.services.AuditServiceTest;
import com.hfpp.network.hub.services.AuthenticationExceptionUnitTests;
import com.hfpp.network.hub.services.AuthenticationServiceTest;
import com.hfpp.network.hub.services.AuthorizationExceptionUnitTests;
import com.hfpp.network.hub.services.AuthorizationServiceTest;
import com.hfpp.network.hub.services.CacheServiceTest;
import com.hfpp.network.hub.services.DataExchangeServiceTest;
import com.hfpp.network.hub.services.DataRequestExpiredExceptionUnitTests;
import com.hfpp.network.hub.services.EntityNotFoundExceptionUnitTests;
import com.hfpp.network.hub.services.InsufficientParticipationRatioExceptionUnitTests;
import com.hfpp.network.hub.services.LookupServiceTest;
import com.hfpp.network.hub.services.NetworkHubServiceExceptionUnitTests;
import com.hfpp.network.hub.services.UserServiceTest;

/**
 * Test suite for this assembly.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
@Transactional(propagation = Propagation.REQUIRED)
@RunWith(Suite.class)
@SuiteClasses({ 
    NetworkHubDaemonExceptionUnitTests.class,
    RecoverableMessageHandlingExceptionUnitTests.class,
    PartnerStatisticsRequestMessageHandlerUnitTests.class,
    DataRequestMessageHandlerUnitTests.class,
    DataResponseMessageHandlerUnitTests.class,
    AnalysisResultMessageHandlerUnitTests.class,
    DataRequestMessageListenerUnitTests.class,
    DataResponseMessageListenerUnitTests.class,
    AnalysisResultMessageListenerUnitTests.class,
    MultiPurposeMessageListenerUnitTests.class,
    NetworkHubDaemonExceptionUnitTests.class,
    RecoverableMessageHandlingExceptionUnitTests.class,
    NetworkHubDaemonTest.class,
    //previous tests
    ConfigurationExceptionUnitTests.class,
    AuthenticationExceptionUnitTests.class,
    AuthorizationExceptionUnitTests.class,
    DataRequestExpiredExceptionUnitTests.class,
    EntityNotFoundExceptionUnitTests.class,
    InsufficientParticipationRatioExceptionUnitTests.class,
    NetworkHubServiceExceptionUnitTests.class,
    DataExchangeServiceTest.class, AuditServiceTest.class,
        AuthenticationServiceTest.class, AuthorizationServiceTest.class,
        CacheServiceTest.class, LookupServiceTest.class, UserServiceTest.class })
public class MyAllTests {

}
