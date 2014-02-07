/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.messaging;

import static org.junit.Assert.assertNull;

import javax.jms.TextMessage;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hfpp.network.node.TestsHelper;

/**
 * <p>
 * Unit tests for {@link AnalysisResultMessageListener} class.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public class AnalysisResultMessageListenerUnitTests {
    /**
     * <p>
     * Represents the <code>AnalysisResultMessageListener</code> instance used in tests.
     * </p>
     */
    private AnalysisResultMessageListener instance;

    /**
     * <p>
     * Represents callback URL used in tests.
     * </p>
     */
    private String callbackURL = "https://www.topcoder.com/";

    /**
     * <p>
     * Represents message used in tests.
     * </p>
     */
    private TextMessage message;

    /**
     * <p>
     * Adapter for earlier versions of JUnit.
     * </p>
     *
     * @return a test suite.
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AnalysisResultMessageListenerUnitTests.class);
    }

    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Before
    public void setUp() throws Exception {
        instance = new AnalysisResultMessageListener();

        instance.setCallbackURL(callbackURL);

        instance.checkConfiguration();

        message = Mockito.mock(TextMessage.class);
    }

    /**
     * <p>
     * Accuracy test for the constructor <code>AnalysisResultMessageListener()</code>.<br>
     * Instance should be correctly created.
     * </p>
     */
    @Test
    public void testCtor() {
        instance = new AnalysisResultMessageListener();

        assertNull("'callbackURL' should be correct.", TestsHelper.getField(instance, "callbackURL"));
    }

    /**
     * <p>
     * Accuracy test for the method <code>onMessage(Message message)</code>.<br>
     * The result should be correct.
     * </p>
     *
     * @throws Exception
     *             to JUnit.
     */
    @Test
    public void test_onMessage() throws Exception {
        Mockito.doReturn("<Message>The message.</Message>").when(message).getText();
        instance.onMessage(message);

        Mockito.verify(message).acknowledge();
    }
}