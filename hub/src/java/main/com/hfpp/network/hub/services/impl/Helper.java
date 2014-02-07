/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import java.util.Collection;
import java.util.List;

import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.models.Role;

/**
 * <p>
 * Helper class for the component. It provides useful common methods for all the
 * classes in this component.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class has no state, and thus it is
 * thread safe.
 * </p>
 * 
 * *
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>change isNullOrEmpty,checkNull,checkState, logEntrance,logExit, logException methods
 * to public</li>
 * <li>modify logParameters so will not log textMessage,otherwise it will log
 * user password in header of message</li>
 * <li>add checkNull and checkNullOrEmpty without log</li>
 * </ul>
 * </p>
 * 
 * <p>
 * v1.2 Changes:
 * <ul>
 * <li>add method findRole</li>
 * </ul>
 * </p>
 * 
 * @author sparemax,TCSASSEMBLER
 * @version 1.2
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
     * Converts the fields to a string.
     * 
     * @param className
     *            the class name.
     * @param fields
     *            the fields.
     * @param values
     *            the values of fields.
     * 
     * @return the string.
     */
    public static String toString(String className, String[] fields,
            Object[] values) {
        StringBuilder sb = new StringBuilder();

        sb.append(className).append("{");

        int num = fields.length;
        for (int i = 0; i < num; i++) {
            if (i != 0) {
                // Append a comma
                sb.append(", ");
            }

            sb.append(fields[i]).append(":").append(values[i]);
        }

        sb.append("}");
        return sb.toString();
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
     * @since 1.1
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * <p>
     * Validates the value of a variable. The value can not be <code>null</code>
     * .
     * </p>
     * 
     * @param logger
     *            the logger object.
     * @param signature
     *            the signature of the method to be logged.
     * @param value
     *            the value of the variable to be validated.
     * @param name
     *            the name of the variable to be validated.
     * 
     * @throws IllegalArgumentException
     *             if the value of the variable is <code>null</code>.
     * @since 1.1
     */
    public static void checkNull(Logger logger, String signature, Object value,
            String name) {
        if (value == null) {
            // Log exception
            throw Helper.logException(logger, signature,
                    new IllegalArgumentException("'" + name
                            + "' should not be null."));
        }
    }

    /**
     * <p>
     * Validates the value of a variable. The value can not be <code>null</code>
     * .
     * </p>
     * 
     * @param value
     *            the value of the variable to be validated.
     * @param name
     *            the name of the variable to be validated.
     * 
     * @throws IllegalArgumentException
     *             if the value of the variable is <code>null</code>.
     * @since 1.1
     */
    public static void checkNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("'" + name
                    + "' should not be null.");

        }
    }

    /**
     * <p>
     * Validates the value of a string. The value can not be <code>null</code>
     * or an empty string.
     * </p>
     * 
     * @param logger
     *            the logger object (not null).
     * @param signature
     *            the signature of the method to be logged.
     * @param value
     *            the value of the variable to be validated.
     * @param name
     *            the name of the variable to be validated.
     * 
     * @throws IllegalArgumentException
     *             if the given string is <code>null</code> or an empty string.
     */
    static void checkNullOrEmpty(Logger logger, String signature, String value,
            String name) {
        checkNull(logger, signature, value, name);

        if (value.trim().length() == 0) {
            // Log exception
            throw Helper.logException(logger, signature,
                    new IllegalArgumentException("'" + name
                            + "' should not be an empty string."));
        }
    }

    /**
     * <p>
     * Validates the value of a string. The value can not be <code>null</code>
     * or an empty string.
     * </p>
     * 
     * @param value
     *            the value of the variable to be validated.
     * @param name
     *            the name of the variable to be validated.
     * 
     * @throws IllegalArgumentException
     *             if the given string is <code>null</code> or an empty string.
     */
    public static void checkNullOrEmpty(String value, String name) {
        checkNull(value, name);

        if (value.trim().length() == 0) {
            throw new IllegalArgumentException("'" + name
                    + "' should not be an empty string.");
        }
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
     * @since 1.1
     */
    public static void checkState(boolean isInvalid, String message) {
        if (isInvalid) {
            throw new ConfigurationException(message);
        }
    }

    /**
     * <p>
     * Checks state of collection.
     * </p>
     * 
     * @param collection
     *            the collection.
     * @param name
     *            the name.
     * 
     * @throws ConfigurationException
     *             if collection is null/empty or contains null/empty element
     */
    static void checkCollection(Collection<String> collection, String name) {
        checkState(collection == null, "'" + name + "' can't be null.");
        checkState(collection.isEmpty(), "'" + name + "' can't be empty.");
        for (String element : collection) {
            checkState(isNullOrEmpty(element), "'" + name
                    + "' can't contain null/empty element.");
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
     * @since 1.1
     */
    public static void logEntrance(Logger logger, String signature,
            String[] paramNames, Object[] params) {
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
     * @since 1.1
     */
    public static void logExit(Logger logger, String signature, Object[] value) {
        // Do logging
        logger.debug(String.format(MESSAGE_EXIT, signature));

        if (value != null) {
            // Log return value
            logger.debug("Output parameter: " + value[0]);
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
     * @since 1.1
     */
    public static <T extends Throwable> T logException(Logger logger,
            String signature, T e) {
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
     * @since 1.1
     */
    private static void logParameters(Logger logger, String[] paramNames,
            Object[] params) {
        StringBuilder sb = new StringBuilder("Input parameters: {");

        for (int i = 0; i < params.length; i++) {

            if (i > 0) {
                // Append a comma
                sb.append(", ");
            }
            if (params[i] instanceof TextMessage) {
                sb.append(paramNames[i]).append(":")
                        .append("**JMS TEXT MESSAGE**");
            } else {
                sb.append(paramNames[i]).append(":").append(params[i]);
            }
        }
        sb.append("}.");

        // Do logging
        logger.debug(sb.toString());
    }

    /**
     * Get role from the roleId
     * 
     * @param roleId
     *            the role id to check
     * @param roles
     *            the role list
     * @throws IllegalArgumentException
     *             if roleId is invalid
     * @return the role if the roleId is valid
     * @since 1.2
     */
    public static Role findRole(String roleId, List<Role> roles) {
        Role result = null;
        if (roles != null) {
            for (Role role : roles) {
                if (role.getId().equals(roleId)) {
                    result = role;
                    break;
                }
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("invalid roleId:" + roleId);
        }
        return result;
    }
}
