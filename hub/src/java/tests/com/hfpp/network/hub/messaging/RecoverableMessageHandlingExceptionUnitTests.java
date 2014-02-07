/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * <p>
 * Unit tests for <code>{@link RecoverableMessageHandlingException}</code> class.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class RecoverableMessageHandlingExceptionUnitTests {
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
        return new JUnit4TestAdapter(RecoverableMessageHandlingExceptionUnitTests.class);
    }

    /**
     * <p>
     * <code>RecoverableMessageHandlingException</code> should be subclass of <code>RuntimeException</code>.
     * </p>
     */
    @Test
    public void testInheritance() {
        assertTrue("RecoverableMessageHandlingException should be subclass of RuntimeException.",
            RecoverableMessageHandlingException.class.getSuperclass() == RuntimeException.class);
    }

    /**
     * <p>
     * Tests accuracy of <code>RecoverableMessageHandlingException(String)</code> constructor.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor1() {
        RecoverableMessageHandlingException exception = new RecoverableMessageHandlingException(DETAIL_MESSAGE);

        // Verify the error message
        assertEquals("RuntimeException message should be correct.", DETAIL_MESSAGE, exception.getMessage());
    }

    /**
     * <p>
     * Tests accuracy of <code>RecoverableMessageHandlingException(String, Throwable)</code> constructor.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor2() {
        RecoverableMessageHandlingException exception = new RecoverableMessageHandlingException(DETAIL_MESSAGE, CAUSE);

        // Verify the error message
        assertEquals("Error message should be correct.", DETAIL_MESSAGE, exception.getMessage());
        // Verify the error cause
        assertSame("Error cause should be correct.", CAUSE, exception.getCause());
    }
}
