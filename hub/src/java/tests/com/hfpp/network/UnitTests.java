/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hfpp.network.hub.ConfigurationExceptionUnitTests;
import com.hfpp.network.hub.services.AuthenticationExceptionUnitTests;
import com.hfpp.network.hub.services.AuthorizationExceptionUnitTests;
import com.hfpp.network.hub.services.DataRequestExpiredExceptionUnitTests;
import com.hfpp.network.hub.services.EntityNotFoundExceptionUnitTests;
import com.hfpp.network.hub.services.InsufficientParticipationRatioExceptionUnitTests;
import com.hfpp.network.hub.services.NetworkHubServiceExceptionUnitTests;
import com.hfpp.network.hub.services.impl.AuditServiceImplUnitTests;
import com.hfpp.network.hub.services.impl.AuthenticationServiceImplUnitTests;
import com.hfpp.network.hub.services.impl.AuthorizationServiceImplUnitTests;
import com.hfpp.network.hub.services.impl.CacheServiceImplUnitTests;
import com.hfpp.network.hub.services.impl.DataExchangeServiceImplUnitTests;
import com.hfpp.network.hub.services.impl.LookupServiceImplUnitTests;
import com.hfpp.network.hub.services.impl.UserServiceImplUnitTests;
import com.hfpp.network.models.AuditRecordUnitTests;
import com.hfpp.network.models.AuditableObjectUnitTests;
import com.hfpp.network.models.BaseSearchCriteriaUnitTests;
import com.hfpp.network.models.CachedObjectWrapperUnitTests;
import com.hfpp.network.models.DataRequestUnitTests;
import com.hfpp.network.models.DataResponseUnitTests;
import com.hfpp.network.models.IdentifiableObjectUnitTests;
import com.hfpp.network.models.LookupObjectUnitTests;
import com.hfpp.network.models.RoleUnitTests;
import com.hfpp.network.models.SearchResultUnitTests;
import com.hfpp.network.models.SortTypeUnitTests;
import com.hfpp.network.models.UserSearchCriteriaUnitTests;
import com.hfpp.network.models.UserStatisticsUnitTests;
import com.hfpp.network.models.UserUnitTests;

/**
 * <p>
 * This test case aggregates all Unit test cases.
 * </p>
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

        suite.addTest(DataExchangeServiceImplUnitTests.suite());
        suite.addTest(UserServiceImplUnitTests.suite());
        suite.addTest(AuthorizationServiceImplUnitTests.suite());
        suite.addTest(AuthenticationServiceImplUnitTests.suite());
        suite.addTest(AuditServiceImplUnitTests.suite());
        suite.addTest(LookupServiceImplUnitTests.suite());
        suite.addTest(CacheServiceImplUnitTests.suite());

        suite.addTest(SortTypeUnitTests.suite());
        suite.addTest(IdentifiableObjectUnitTests.suite());
        suite.addTest(AuditableObjectUnitTests.suite());
        suite.addTest(LookupObjectUnitTests.suite());
        suite.addTest(AuditRecordUnitTests.suite());
        suite.addTest(RoleUnitTests.suite());
        suite.addTest(UserUnitTests.suite());
        suite.addTest(DataRequestUnitTests.suite());
        suite.addTest(DataResponseUnitTests.suite());
        suite.addTest(UserStatisticsUnitTests.suite());
        suite.addTest(CachedObjectWrapperUnitTests.suite());
        suite.addTest(BaseSearchCriteriaUnitTests.suite());
        suite.addTest(UserSearchCriteriaUnitTests.suite());
        suite.addTest(SearchResultUnitTests.suite());

        // Exceptions
        suite.addTest(ConfigurationExceptionUnitTests.suite());
        suite.addTest(NetworkHubServiceExceptionUnitTests.suite());
        suite.addTest(AuthenticationExceptionUnitTests.suite());
        suite.addTest(AuthorizationExceptionUnitTests.suite());
        suite.addTest(DataRequestExpiredExceptionUnitTests.suite());
        suite.addTest(EntityNotFoundExceptionUnitTests.suite());
        suite.addTest(InsufficientParticipationRatioExceptionUnitTests.suite());
        return suite;
    }
}
