/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub;

import java.io.File;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;
import org.junit.Test;

/**
 * <p>
 * Unit tests for <code>{@link NetworkHubDaemon}</code> class.
 * </p>
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class NetworkHubDaemonTest {

    /**
     * Failure test for method init when context is null.
     * 
     * @throws Exception
     *             throw if any error happen
     */
    @Test(expected = DaemonInitException.class)
    public void testInit1() throws Exception {
        NetworkHubDaemon daemon = new NetworkHubDaemon();
        daemon.init(null);
    }

    /**
     * Failure test for method init when context arguments is null.
     * 
     * @throws Exception
     *             throw if any error happen
     */
    @Test(expected = DaemonInitException.class)
    public void testInit2() throws Exception {
        NetworkHubDaemon daemon = new NetworkHubDaemon();
        daemon.init(getDaemonContext(null));
    }

    /**
     * Failure test for method init when context arguments contains null value.
     * 
     * @throws Exception
     *             throw if any error happen
     */
    @Test(expected = DaemonInitException.class)
    public void testInit3() throws Exception {
        NetworkHubDaemon daemon = new NetworkHubDaemon();
        daemon.init(getDaemonContext(new String[] { null }));
    }

    /**
     * Failure test for method init when context context arguments contains
     * empty value.
     * 
     * @throws Exception
     *             throw if any error happen
     */
    @Test(expected = DaemonInitException.class)
    public void testInit4() throws Exception {
        NetworkHubDaemon daemon = new NetworkHubDaemon();
        daemon.init(getDaemonContext(new String[] { "" }));
    }

    /**
     * Failure test for method init when context argument contains invalid
     * spring path.
     * 
     * @throws Exception
     *             throw if any error happen
     */
    @Test(expected = DaemonInitException.class)
    public void testInit5() throws Exception {
        NetworkHubDaemon daemon = new NetworkHubDaemon();
        daemon.init(getDaemonContext(new String[] { "error" }));
    }

    /**
     * Accuracy test for NetworkHubDaemon.
     * 
     * @throws Exception
     *             throw if any error happen
     */
    @Test
    public void testNetworkHubDaemon() throws Exception {
        NetworkHubDaemon daemon = new NetworkHubDaemon();
        daemon.init(getDaemonContext(new String[] { "conf" + File.separator
                + "empty.xml" }));
        daemon.start();
        daemon.stop();
        daemon.destroy();
    }

    /**
     * Create daemon context.
     * 
     * @param args
     *            the arguments.
     * @return daemon context.
     */
    private DaemonContext getDaemonContext(final String[] args) {
        return
        /**
         * <p>
         * The daemon context.
         * </p>
         * 
         * <p>
         * <strong>Thread Safety: </strong> This class is immutable and thread
         * safe.
         * </p>
         * 
         * @author TCSASSEMBLER
         * @version 1.0
         */
        new DaemonContext() {

            /**
             * Get daemon controller
             * 
             * @return daemon controller
             */
            @Override
            public DaemonController getController() {
                return null;
            }

            /**
             * Get daemon context arguments.
             * 
             * @return daemon context arguments.
             */
            @Override
            public String[] getArguments() {
                return args;
            }
        };
    }
}
