/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * <p>
 * Unit tests for <code>{@link NetworkNodeServiceException}</code> class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class NetworkNodeServiceExceptionUnitTests {
    /**
     * <p>
     * Represents a detail message.
     * </p>
     */
    private static final String DETAIL_MESSAGE = "detail";

    /**
     * <p>
     * Represents an error cause.
     * </p>
     */
    private static final Throwable CAUSE = new Exception("UnitTests");

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(NetworkNodeServiceExceptionUnitTests.class);
    }

    /**
     * <p>
     * <code>NetworkNodeServiceException</code> should be subclass of <code>Exception</code>.
     * </p>
     */
    @Test
    public void testInheritance() {
        assertTrue("NetworkNodeServiceException should be subclass of Exception.",
            NetworkNodeServiceException.class.getSuperclass() == Exception.class);
    }

    /**
     * <p>
     * Tests accuracy of <code>NetworkNodeServiceException(String)</code> constructor.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor1() {
        NetworkNodeServiceException exception =
            new NetworkNodeServiceException(DETAIL_MESSAGE);

        // Verify the error message
        assertEquals("Exception message should be correct.", DETAIL_MESSAGE, exception.getMessage());
    }

    /**
     * <p>
     * Tests accuracy of <code>NetworkNodeServiceException(String, Throwable)</code> constructor.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor2() {
        NetworkNodeServiceException exception =
            new NetworkNodeServiceException(DETAIL_MESSAGE, CAUSE);

        // Verify the error message
        assertEquals("Error message should be correct.", DETAIL_MESSAGE, exception.getMessage());
        // Verify the error cause
        assertSame("Error cause should be correct.", CAUSE, exception.getCause());
    }
}
