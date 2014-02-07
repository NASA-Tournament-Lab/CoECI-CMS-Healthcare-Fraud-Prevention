/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hfpp.network.node.httpservices.NetworkNodeHTTPServicesControllerUnitTests;
import com.hfpp.network.node.messaging.AnalysisResultMessageListenerUnitTests;
import com.hfpp.network.node.messaging.DataRequestMessageListenerUnitTests;
import com.hfpp.network.node.messaging.DataResponseMessageListenerUnitTests;
import com.hfpp.network.node.messaging.GenericForwardMessageListenerUnitTests;
import com.hfpp.network.node.services.AuthenticationExceptionUnitTests;
import com.hfpp.network.node.services.AuthorizationExceptionUnitTests;
import com.hfpp.network.node.services.DataRequestExpiredExceptionUnitTests;
import com.hfpp.network.node.services.InsufficientParticipationRatioExceptionUnitTests;
import com.hfpp.network.node.services.InvalidMessageExceptionUnitTests;
import com.hfpp.network.node.services.NetworkNodeServiceExceptionUnitTests;
import com.hfpp.network.node.services.impl.DataExchangeServiceImplUnitTests;

/**
 * <p>
 * This test case aggregates all Unit test cases.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class UnitTests extends TestCase {
    /**
     * <p>
     * All unit test cases.
     * </p>
     *
     * @return The test suite.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();

        suite.addTest(Demo.suite());

        suite.addTest(NetworkNodeHTTPServicesControllerUnitTests.suite());

        suite.addTest(DataExchangeServiceImplUnitTests.suite());

        suite.addTest(AnalysisResultMessageListenerUnitTests.suite());
        suite.addTest(DataRequestMessageListenerUnitTests.suite());
        suite.addTest(DataResponseMessageListenerUnitTests.suite());
        suite.addTest(GenericForwardMessageListenerUnitTests.suite());

        // Exceptions
        suite.addTest(ConfigurationExceptionUnitTests.suite());
        suite.addTest(NetworkNodeServiceExceptionUnitTests.suite());
        suite.addTest(AuthenticationExceptionUnitTests.suite());
        suite.addTest(AuthorizationExceptionUnitTests.suite());
        suite.addTest(DataRequestExpiredExceptionUnitTests.suite());
        suite.addTest(InsufficientParticipationRatioExceptionUnitTests.suite());
        suite.addTest(InvalidMessageExceptionUnitTests.suite());

        return suite;
    }
}
