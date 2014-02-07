/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.services.impl;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.PasswordEncryptor;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.AuthenticationException;
import com.hfpp.network.hub.services.AuthenticationService;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.UserService;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the implementation of AuthenticationService.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>add final to signature</li>
 * <li>inject passwordEncryptor by Spring.</li>
 * </ul>
 * </p>
 * 
 * @author flying2hk, sparemax,TCSASSEMBLER
 * @version 1.1
 */
public class AuthenticationServiceImpl extends BasePersistenceService implements
        AuthenticationService {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = AuthenticationServiceImpl.class
            .getName();

    /**
     * Represents the string to query password hash.
     */
    private static final String SQL_QUERY_PASSWORD_HASH = "SELECT password_hash FROM user"
            + " WHERE is_deleted = 0 AND username = :username";

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the PasswordEncryptor used to encrypt passwords. It is
     * optional. It should be non-null. It will be initialized to
     * StrongPasswordEncryptor by default (which uses SHA256 with 16 bytes
     * random salt for 100000 iterations). Usually there is no need to set the
     * PasswordEncryptor explicitly, it is configurable just for future password
     * hashing algorithm changes.
     * 
     * @since 1.1
     */
    private PasswordEncryptor passwordEncryptor;

    /**
     * Represents the UserService to access the user from persistence. It should
     * be non-null. It is required.
     */
    private UserService userService;

    /**
     * Creates an instance of AuthenticationServiceImpl.
     */
    public AuthenticationServiceImpl() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
     *             (entityManager, passwordEncryptor or userService is null).
     */
    @PostConstruct
    public void checkConfiguration() {
        super.checkConfiguration();

        Helper.checkState(passwordEncryptor == null,
                "'passwordEncryptor' can't be null.");
        Helper.checkState(userService == null, "'userService' can't be null.");
    }

    /**
     * This method is used to authenticate user.
     * 
     * @param username
     *            the username
     * @param password
     *            the password
     * 
     * @return the authenticated user
     * 
     * @throws IllegalArgumentException
     *             if username or password is null or empty string
     * @throws AuthenticationException
     *             if the user fails the authentication
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @since 1.1
     */
    public User authenticate(String username, String password)
            throws NetworkHubServiceException {
        final String signature = CLASS_NAME
                + ".authenticate(String username, String password)";

        // Log entry
        Helper.logEntrance(LOGGER, signature, new String[] { "username",
                "password" }, new Object[] { username, "******" });

        Helper.checkNullOrEmpty(LOGGER, signature, username, "username");
        Helper.checkNullOrEmpty(LOGGER, signature, password, "password");
        try {
            EntityManager entityManager = getEntityManager();
            Query query = entityManager
                    .createNativeQuery(SQL_QUERY_PASSWORD_HASH);
            query.setParameter("username", username);

            try {
                String hash = (String) query.getSingleResult();
                if (passwordEncryptor.checkPassword(password, hash)) {
                    User result = userService.getByUsername(username);

                    // Log exit
                    Helper.logExit(LOGGER, signature, new Object[] { result });
                    return result;
                }

                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new AuthenticationException(
                                "User fails authentication."));
            } catch (EncryptionOperationNotPossibleException e) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new AuthenticationException(
                                "User fails authentication."));
            } catch (NoResultException e) {
                // Log exception
                throw Helper.logException(LOGGER, signature,
                        new AuthenticationException("No such user."));
            }

        } catch (IllegalStateException e) {
            // Log exception
            throw Helper.logException(LOGGER, signature,
                    new NetworkHubServiceException(
                            "The entity manager has been closed.", e));
        } catch (PersistenceException e) {
            // Log exception
            throw Helper
                    .logException(
                            LOGGER,
                            signature,
                            new NetworkHubServiceException(
                                    "An error has occurred while accessing the persistence.",
                                    e));
        }
    }

    /**
     * Sets the PasswordEncryptor used to encrypt passwords.
     * 
     * @param passwordEncryptor
     *            the PasswordEncryptor used to encrypt passwords.
     */
    public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }

    /**
     * Sets the UserService to access the user from persistence.
     * 
     * @param userService
     *            the UserService to access the user from persistence.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
