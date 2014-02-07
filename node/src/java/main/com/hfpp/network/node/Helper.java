/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

/**
 * <p>
 * Helper class for the component. It provides useful common methods for all the classes in this component.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class has no state, and thus it is thread safe.
 * </p>
 *
 * @author sparemax
 * @version 1.0
 */
public final class Helper {
    /**
     * <p>
     * Represents the entrance message.
     * </p>
     */
    private static final String MESSAGE_ENTRANCE = "Entering method %1$s.";

    /**
     * <p>
     * Represents the exit message.
     * </p>
     */
    private static final String MESSAGE_EXIT = "Exiting method %1$s.";

    /**
     * <p>
     * Represents the error message.
     * </p>
     */
    private static final String MESSAGE_ERROR = "Error in method %1$s. Details:";

    /**
     * <p>
     * Prevents to create a new instance.
     * </p>
     */
    private Helper() {
        // empty
    }

    /**
     * <p>
     * Validates the value of a string.
     * </p>
     *
     * @param value
     *            the value of the variable to be validated.
     *
     * @return <code>true</code> if value is <code>null</code> or empty
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * <p>
     * Checks state of object.
     * </p>
     *
     * @param isInvalid
     *            the state of object.
     * @param message
     *            the error message.
     *
     * @throws ConfigurationException
     *             if isInvalid is <code>true</code>
     */
    public static void checkState(boolean isInvalid, String message) {
        if (isInvalid) {
            throw new ConfigurationException(message);
        }
    }

    /**
     * <p>
     * Logs for entrance into public methods at <code>DEBUG</code> level.
     * </p>
     *
     * @param logger
     *            the logger object.
     * @param signature
     *            the signature of the method to be logged.
     * @param paramNames
     *            the names of parameters to log .
     * @param params
     *            the values of parameters to log.
     */
    public static void logEntrance(Logger logger, String signature, String[] paramNames, Object[] params) {
        // Do logging
        logger.debug(String.format(MESSAGE_ENTRANCE, signature));

        if (paramNames != null) {
            // Log parameters
            logParameters(logger, paramNames, params);
        }
    }

    /**
     * <p>
     * Logs for exit from public methods at <code>DEBUG</code> level.
     * </p>
     *
     * @param logger
     *            the logger object.
     * @param signature
     *            the signature of the method to be logged.
     * @param value
     *            the return value to log.
     */
    public static void logExit(Logger logger, String signature, Object[] value) {
        // Do logging
        logger.debug(String.format(MESSAGE_EXIT, signature));

        if (value != null) {
            // Log return value
            logger.debug("Output parameter: " + toString(value[0]));
        }
    }

    /**
     * <p>
     * Logs the given exception and message at <code>ERROR</code> level.
     * </p>
     *
     * @param <T>
     *            the exception type.
     * @param logger
     *            the logger object.
     * @param signature
     *            the signature of the method to log.
     * @param e
     *            the exception to log.
     *
     * @return the passed in exception.
     */
    public static <T extends Throwable> T logException(Logger logger, String signature, T e) {
        if (logger != null) {
            String errorMessage = String.format(MESSAGE_ERROR, signature);

            // Do logging
            logger.error(errorMessage, e);
        }
        return e;
    }

    /**
     * <p>
     * Logs the parameters at <code>DEBUG</code> level.
     * </p>
     *
     *
     * @param logger
     *            the logger object (not <code>null</code>).
     * @param paramNames
     *            the names of parameters to log (not <code>null</code>).
     * @param params
     *            the values of parameters to log (not <code>null</code>).
     */
    private static void logParameters(Logger logger, String[] paramNames, Object[] params) {
        StringBuilder sb = new StringBuilder("Input parameters: {");

        for (int i = 0; i < params.length; i++) {

            if (i > 0) {
                // Append a comma
                sb.append(", ");
            }

            sb.append(paramNames[i]).append(":").append(toString(params[i]));
        }
        sb.append("}.");

        // Do logging
        logger.debug(sb.toString());
    }

    /**
     * Converts the object to string.
     *
     * @param obj
     *            the object
     *
     * @return the string representation of the object.
     */
    private static String toString(Object obj) {
        if (obj instanceof Exception) {
            return toString((Exception) obj);
        }

        return String.valueOf(obj);
    }

    /**
     * <p>
     * Converts the Exception object to a string.
     * </p>
     *
     * @param obj
     *            the object
     *
     * @return the string
     */
    private static String toString(Exception obj) {
        OutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        obj.printStackTrace(printStream);

        return output.toString();
    }
}
