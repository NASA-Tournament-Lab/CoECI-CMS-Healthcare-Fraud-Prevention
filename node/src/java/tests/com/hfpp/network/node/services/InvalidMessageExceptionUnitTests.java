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
 * Unit tests for <code>{@link InvalidMessageException}</code> class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class InvalidMessageExceptionUnitTests {
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
        return new JUnit4TestAdapter(InvalidMessageExceptionUnitTests.class);
    }

    /**
     * <p>
     * <code>InvalidMessageException</code> should be subclass of <code>NetworkNodeServiceException</code>.
     * </p>
     */
    @Test
    public void testInheritance() {
        assertTrue("InvalidMessageException should be subclass of NetworkNodeServiceException.",
            InvalidMessageException.class.getSuperclass() == NetworkNodeServiceException.class);
    }

    /**
     * <p>
     * Tests accuracy of <code>InvalidMessageException(String)</code> constructor.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor1() {
        InvalidMessageException exception =
            new InvalidMessageException(DETAIL_MESSAGE);

        // Verify the error message
        assertEquals("NetworkNodeServiceException message should be correct.", DETAIL_MESSAGE, exception.getMessage());
    }

    /**
     * <p>
     * Tests accuracy of <code>InvalidMessageException(String, Throwable)</code> constructor.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor2() {
        InvalidMessageException exception =
            new InvalidMessageException(DETAIL_MESSAGE, CAUSE);

        // Verify the error message
        assertEquals("Error message should be correct.", DETAIL_MESSAGE, exception.getMessage());
        // Verify the error cause
        assertSame("Error cause should be correct.", CAUSE, exception.getCause());
    }
}
