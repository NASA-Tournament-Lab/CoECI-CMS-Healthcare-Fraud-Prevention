/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub;

import java.util.Map;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.hfpp.network.hub.services.impl.Helper;

/**
 * <p>
 * This class implements a Daemon for the Network Hub.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> NetworkHubDaemon isn't required to be thread
 * safe, because it is not expected to be used by multiple threads since the it
 * will be only initialized and started once.
 * </p>
 * 
 * @author flying2hk,TCSASSEMBLER
 * @version 1.0
 */
public class NetworkHubDaemon implements Daemon {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = NetworkHubDaemon.class.getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the NetworkHubDaemon used in main method.
     */
    private static final NetworkHubDaemon NETWORKHUBDAEMON = new NetworkHubDaemon();

    /**
     * Represents the Spring application context that contains the objects
     * needed for the Network Hub.
     */
    private AbstractApplicationContext context;

    /**
     * Creates an instance of NetworkHubDaemon.
     */
    public NetworkHubDaemon() {

    }

    /**
     * This method is used to destroy the daemon.
     */
    @Override
    public void destroy() {
        final String signature = CLASS_NAME + ".destroy()";
        // Log entry
        Helper.logEntrance(LOGGER, signature, null, null);
        if (this.context != null) {
            // close the context
            this.context.close();
        }
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }

    /**
     * This method is used to initialize the daemon.
     * 
     * @param context
     *            the daemon context.
     * @throws DaemonInitException
     *             throws if context is null or contains invalid argument or
     *             fail to load spring configuration file
     * @throws Exception
     *             throws if any error happen.
     */
    @Override
    public void init(DaemonContext context) throws DaemonInitException,
            Exception {
        final String signature = CLASS_NAME + ".init(DaemonContext context)";
        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "context" },
                new Object[] { context });
        if (context == null) {
            throw Helper.logException(LOGGER, signature,
                    new DaemonInitException(
                            "should provide context when init service"));
        }
        if (context.getArguments() == null
                || context.getArguments().length == 0
                || context.getArguments()[0] == null
                || context.getArguments()[0].trim().length() == 0) {
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new DaemonInitException(
                                    "should provide valid spring configuration file path"));
        }
        try {
            // Load application context
            this.context = new FileSystemXmlApplicationContext(
                    context.getArguments()[0]);
        } catch (BeansException e) {
            LOGGER.error("error happen when load spring configuration file", e);
            throw Helper
                    .logException(LOGGER, signature, new DaemonInitException(
                            "error happen when load spring configuration file"));
        }
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }

    /**
     * This method is used to start the daemon.
     * 
     * @throws NetworkHubDaemonException
     *             throws if any error occurred during the operation
     * @throws Exception
     *             throws if any error occurred during the operation
     */
    @Override
    public void start() throws Exception {
        final String signature = CLASS_NAME + ".start()";
        // Log entry
        Helper.logEntrance(LOGGER, signature, null, null);
        if (this.context != null) {
            try {
                // Start message listener containers
                Map<String, DefaultMessageListenerContainer> containers = this.context
                        .getBeansOfType(DefaultMessageListenerContainer.class);

                for (DefaultMessageListenerContainer container : containers
                        .values()) {
                    container.start();
                }
            } catch (BeansException e) {
                throw Helper
                        .logException(
                                LOGGER,
                                signature,
                                new NetworkHubDaemonException(
                                        "error happen when get message listener container in spring container",
                                        e));
            } catch (JmsException e) {
                throw Helper
                        .logException(
                                LOGGER,
                                signature,
                                new NetworkHubDaemonException(
                                        "error happen when start message listener container in spring container",
                                        e));
            }
        }
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }

    /**
     * This method is used to stop the daemon.
     * 
     * @throws NetworkHubDaemonException
     *             throws if any error occurred during the operation
     * @throws Exception
     *             throws if any error occurred during the operation
     */
    @Override
    public void stop() throws Exception {
        final String signature = CLASS_NAME + ".stop()";
        // Log entry
        Helper.logEntrance(LOGGER, signature, null, null);
        if (this.context != null) {
            try {
                // Stop message listener containers
                Map<String, DefaultMessageListenerContainer> containers = this.context
                        .getBeansOfType(DefaultMessageListenerContainer.class);

                for (DefaultMessageListenerContainer container : containers
                        .values()) {
                    container.stop();
                }
            } catch (BeansException e) {
                throw Helper
                        .logException(
                                LOGGER,
                                signature,
                                new NetworkHubDaemonException(
                                        "error happen when get message listener container in spring container",
                                        e));
            } catch (JmsException e) {
                throw Helper
                        .logException(
                                LOGGER,
                                signature,
                                new NetworkHubDaemonException(
                                        "error happen when stop message listener container in spring container",
                                        e));
            }
        }
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }

    /**
     * This is the entrance for this class.
     * 
     * @param args
     *            the arguments
     * @throws Exception
     *             throws if any error happen.
     */
    public static void main(final String[] args) throws Exception {
        final String signature = CLASS_NAME + ".main(String[] args)";
        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "args" },
                new Object[] { args });
        if (args.length > 0) {
            if ("start".equals(args[0]) && args.length > 1) {
                // create instance of daemon context.
                NETWORKHUBDAEMON.init(
                /**
                 * <p>
                 * The daemon context.
                 * </p>
                 * 
                 * <p>
                 * <strong>Thread Safety: </strong> This class is immutable and
                 * thread safe.
                 * </p>
                 * 
                 * @author flying2hk, TCSASSEMBLER
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
                        return new String[] { args[1] };
                    }
                });
                NETWORKHUBDAEMON.start();
            } else if ("stop".equals(args[0])) {
                NETWORKHUBDAEMON.destroy();
            }
        }
        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }
}
