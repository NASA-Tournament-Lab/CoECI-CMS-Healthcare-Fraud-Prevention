/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.hub.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hfpp.network.hub.ConfigurationException;
import com.hfpp.network.hub.services.AuditService;
import com.hfpp.network.hub.services.AuthenticationException;
import com.hfpp.network.hub.services.AuthenticationService;
import com.hfpp.network.hub.services.AuthorizationException;
import com.hfpp.network.hub.services.AuthorizationService;
import com.hfpp.network.hub.services.NetworkHubServiceException;
import com.hfpp.network.hub.services.impl.Helper;
import com.hfpp.network.models.AuditRecord;
import com.hfpp.network.models.User;

/**
 * <p>
 * This is the base class for all message listeners. This class mainly provides
 * a set of protected utility methods that can be used by subclasses.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since
 * its internal state isn't expected to change after Spring IoC initialization,
 * and all dependencies are thread safe.
 * </p>
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>change velocityEngine with null log</li>
 * </ul>
 * </p>
 * @author flying2hk,TCSASSEMBLER
 * @version 1.1
 */
public abstract class BaseMessageListener implements MessageListener {

    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = BaseMessageListener.class
            .getName();

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger BASELOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the AuditService used to perform auditing.
     */
    private AuditService auditService;

    /**
     * Represents the header name for the username.
     * <p>
     * It should be non-null, non-empty string. It will be initialized to
     * "x-hfpp-username" by default. Usually there is no need to set this
     * explicitly, it is configurable just for possible future header name
     * changes.
     * </p>
     */
    private String usernameHeader;

    /**
     * Represents the header name for the password.
     * <p>
     * It should be non-null, non-empty string. It will be initialized to
     * "x-hfpp-password" by default. Usually there is no need to set this
     * explicitly, it is configurable just for possible future header name
     * changes.
     * </p>
     */
    private String passwordHeader;

    /**
     * Represents the authentication service used to authenticate user.
     */
    private AuthenticationService authenticationService;

    /**
     * Represents the authorization service used to check authorization.
     */
    private AuthorizationService authorizationService;

    /**
     * Represents the velocity engine used to generate XML response messages.
     */
    private VelocityEngine velocityEngine;

    /**
     * Represents the jms template used to send JMS messages.
     */
    private JmsTemplate jmsTemplate;

    /**
     * Represents the default error code.
     * <p>
     * It should be non-null, non-empty string. It will be initialized to "000"
     * by default. Usually there is no need to set this explicitly, it is
     * configurable just for possible future changes.
     * </p>
     */
    private String defaultErrorCode;

    /**
     * Represents the default error message.
     */
    private String defaultErrorMessage;

    /**
     * Represents the XPathFactory used to create XPath instances.
     */
    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    /**
     * Represents the DocumentBuilderFactory used to parse XML documents.
     */
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
            .newInstance();

    /**
     * Represents the mapping from exception class to corresponding error code
     * that is written to the XML &lt;ErrorCode&gt; element.
     * 
     * It should be non-null, possibly empty map.
     * 
     * The key should be non-null exception class. The value should be non-null,
     * non-empty string representing the error code.
     */
    private Map<Class<? extends Exception>, String> exceptionErrorCodes;

    /**
     * Represents the mapping from exception class to corresponding error
     * message that is written to the XML &lt;ErrorMessage&gt; element.
     * 
     * It should be non-null, possibly empty map.
     * 
     * The key should be non-null exception class. The value should be non-null,
     * non-empty string representing the error message.
     */
    private Map<Class<? extends Exception>, String> exceptionErrorMessages;

    /**
     * Represents a list of recoverable exception classes. If onMessage method
     * catches any exception of a type that is contained in this list,
     * RecoverableMessageHandlingException will be thrown to instruct the
     * external Transaction Manager to rollback the transaction thus not
     * acknowledge the received message.
     * 
     * It should be possibly null, possibly empty list.
     * 
     * If it is non-null, non-empty, then each item should be non-null exception
     * class.
     * 
     * In this initial implementation, this will be left to null since there's
     * no identified recoverable exception scenario.
     */
    private List<Class<? extends Exception>> recoverableExceptions;

    /**
     * Empty constructor.
     */
    protected BaseMessageListener() {
    }

    /**
     * Check if all required fields are initialized properly.
     * 
     * @throws ConfigurationException
     *             if any required field is not initialized properly
	 * @since 1.1
     */
    @PostConstruct
    public void checkConfiguration() {
        Helper.checkState(auditService == null, "'auditService' can't be null.");
        Helper.checkState(Helper.isNullOrEmpty(usernameHeader),
                "'usernameHeader' can't be null or empty.");
        Helper.checkState(Helper.isNullOrEmpty(passwordHeader),
                "'passwordHeader' can't be null or empty.");
        Helper.checkState(authenticationService == null,
                "'authenticationService' can't be null.");
        Helper.checkState(authorizationService == null,
                "'authorizationService' can't be null.");
        Helper.checkState(velocityEngine == null,
                "'velocityEngine' can't be null.");
        Helper.checkState(jmsTemplate == null, "'jmsTemplate' can't be null.");
        Helper.checkState(Helper.isNullOrEmpty(defaultErrorCode),
                "'defaultErrorCode' can't be null or empty.");
        Helper.checkState(Helper.isNullOrEmpty(defaultErrorMessage),
                "'defaultErrorMessage' can't be null or empty.");
        Helper.checkState(
                exceptionErrorCodes == null || exceptionErrorCodes.isEmpty(),
                "'exceptionErrorCodes' can't be null or empty map.");

        for (Entry<Class<? extends Exception>, String> entry : exceptionErrorCodes
                .entrySet()) {
            Helper.checkState(entry.getKey() == null,
                    "'exceptionErrorCodes' can't contains null key.");
            Helper.checkState(Helper.isNullOrEmpty(entry.getValue()),
                    "'exceptionErrorCodes' can't contains null or empty value.");
        }

        Helper.checkState(exceptionErrorMessages == null
                || exceptionErrorMessages.isEmpty(),
                "'exceptionErrorMessages' can't be null or empty map.");

        for (Entry<Class<? extends Exception>, String> entry : exceptionErrorMessages
                .entrySet()) {
            Helper.checkState(entry.getKey() == null,
                    "'exceptionErrorMessages' can't contains null key.");
            Helper.checkState(Helper.isNullOrEmpty(entry.getValue()),
                    "'exceptionErrorMessages' can't contains null or empty value.");
        }

        if (recoverableExceptions != null && !recoverableExceptions.isEmpty()) {
            for (Class<? extends Exception> clazz : recoverableExceptions) {
                Helper.checkState(
                        clazz == null,
                        "'recoverableExceptions' can't contains null value when not null or empty list.");
            }
        }
        java.util.Properties p = new java.util.Properties();
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        velocityEngine.init(p);
    }

    /**
     * This method is used to parse XML message to DOM Document.
     * 
     * @param message
     *            the received message
     * @return the xml document parsed from message
     * @throws IllegalArgumentException
     *             if message is null
     * @throws UnsupportedEncodingException
     *             throws If the named charset is not supported
     * @throws JMSException
     *             throws if jms error happen
     * @throws IOException
     *             throws If any IO errors occur.
     * @throws SAXException
     *             throws If any parse errors occur.
     * 
     * @throws Exception
     *             throws if any error happen
     */
    protected Document parseXMLMessage(TextMessage message) throws Exception {
        Helper.checkNull(message, "message");
        return documentBuilderFactory.newDocumentBuilder().parse(
                new ByteArrayInputStream(message.getText().getBytes("utf-8")));
    }

    /**
     * This method is used to validate XML message.
     * 
     * @param schema
     *            the XSD schema
     * @param message
     *            the received message
     * @throws IllegalArgumentException
     *             if message or schema is null
     * @throws UnsupportedEncodingException
     *             throws If the named charset is not supported
     * @throws JMSException
     *             throws if jms error happen
     * @throws IOException
     *             throws If any IO errors occur.
     * @throws SAXException
     *             throws If any parse errors occur.
     * @throws Exception
     *             throws if any error happen
     */
    protected void validateXMLMessage(TextMessage message, Schema schema)
            throws Exception {
        Helper.checkNull(message, "message");
        Helper.checkNull(schema, "schema");
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new ByteArrayInputStream(message
                .getText().getBytes("utf-8"))));
    }

    /**
     * This method is used to check authorization.
     * 
     * @param operation
     *            the operation
     * @param user
     *            the authenticated user
     * @throws IllegalArgumentException
     *             throws if any argument is null or empty
     * @throws AuthorizationException
     *             if the user isn't authorized to perform the operation
     * @throws NetworkHubServiceException
     *             if any other error occurred
     */
    protected void checkAuthorization(User user, String operation)
            throws NetworkHubServiceException {
        Helper.checkNull(user, "user");
        Helper.checkNullOrEmpty(operation, "operation");
        authorizationService.checkAuthorization(user.getId(), operation);
    }

    /**
     * This method is used to authenticate user.
     * 
     * @param message
     *            the received message
     * @return the authenticated user
     * @throws IllegalArgumentException
     *             throws if any argument is null or empty
     * @throws JMSException
     *             throws if jms error happen
     * @throws AuthenticationException
     *             if the user fails the authentication
     * @throws NetworkHubServiceException
     *             if any other error occurred
     * @throws Exception
     *             throws if any error happen
     */
    protected User authenticate(Message message) throws Exception {
        Helper.checkNull(message, "message");
        String username = message.getStringProperty(usernameHeader);
        String password = message.getStringProperty(passwordHeader);
        return authenticationService.authenticate(username, password);
    }

    /**
     * This method is used to send message to specified destination. The message
     * will be generated using Velocity.
     * 
     * @param destination
     *            the destination
     * @param templatePath
     *            the path of Velocity template that is used to generate the
     *            message
     * @param context
     *            the Velocity context
     * @throws IllegalArgumentException
     *             if any argument is null, or empty
     * @throws ResourceNotFoundException
     *             throws if template not found
     * @throws ParseErrorException
     *             throws if error happen when parse template
     * @throws JMSException
     *             throws if jms error happen
     * @throws Exception
     *             throws if any error happen
     */
    protected void sendMessage(Destination destination, String templatePath,
            VelocityContext context) throws Exception {
        Helper.checkNull(destination, "destination");
        Helper.checkNullOrEmpty(templatePath, "templatePath");
        Helper.checkNull(context, "context");
        Template template = velocityEngine.getTemplate(templatePath);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        final String message = sw.toString();
        jmsTemplate.send(destination,
        /**
         * <p>
         * The message creator.
         * </p>
         * 
         * <p>
         * <strong>Thread Safety: </strong> This class is immutable and thread
         * safe.
         * </p>
         * 
         * @author flying2hk,TCSASSEMBLER
         * @version 1.0
         */
        new MessageCreator() {
            /**
             * Create jms message.
             * 
             * @param session
             *            the jms session
             * @return the text jms message.
             * @throws JMSException
             *             throws if any jms error happen
             */
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message jmsMessage = session.createTextMessage(message);
                return jmsMessage;
            }
        });
    }

    /**
     * This method is used to create XPath instance.
     * 
     * @return new xpath
     */
    protected XPath createXPath() {
        return xpathFactory.newXPath();
    }

    /**
     * This method is used to handle exceptions.
     * 
     * @param message
     *            the received message
     * @param ex
     *            the exception
     * @param context
     *            the VelocityContext
     * @param templatePath
     *            the path of the Velocity template that is used to generate
     *            response XML
     * @throws IllegalArgumentException
     *             if any argument is null, or empty
     * @throws RecoverableMessageHandlingException
     *             throws if specified and the exception's class is in the list.
     */
    protected void handleException(Exception ex, Message message,
            String templatePath, VelocityContext context) {
        Helper.checkNull(ex, "ex");
        Helper.checkNull(message, "message");
        Helper.checkNullOrEmpty(templatePath, "templatePath");
        Helper.checkNull(context, "context");
        final String signature = CLASS_NAME
                + ".handleException(Exception ex, Message message,String templatePath, VelocityContext context)";

        if (recoverableExceptions != null
                && recoverableExceptions.contains(ex.getClass())) {
            throw Helper.logException(BASELOGGER, signature,
                    new RecoverableMessageHandlingException(
                            "Recoverable exception caught.", ex));
        }
        String errorCode = exceptionErrorCodes.get(ex.getClass());
        String errorMessage = exceptionErrorMessages.get(ex.getClass());
        context.put("errorCode", errorCode == null ? defaultErrorCode
                : errorCode);
        context.put("errorMessage", errorMessage == null ? defaultErrorMessage
                : errorMessage);
        BASELOGGER.error(errorMessage,ex);
        try {
            sendMessage(message.getJMSReplyTo(), templatePath, context);
        } catch (Exception e) {
            Helper.logException(BASELOGGER, signature, e);
        }
    }

    /**
     * This method is used to perform auditing.
     * 
     * @param message
     *            the received message
     * @param operation
     *            the operation
     * @param denied
     *            whether the request was denied
     * @param user
     *            the user
     * @throws IllegalArgumentException
     *             throws if any argument is null or empty string.
     */
    protected void performAuditing(User user, String operation, boolean denied,
            TextMessage message) {
        Helper.checkNull(user, "user");
        Helper.checkNull(message, "message");
        Helper.checkNullOrEmpty(operation, "operation");
        final String signature = CLASS_NAME
                + ".performAuditing(User user, String operation, boolean denied,TextMessage message)";
        try {
            AuditRecord record = new AuditRecord();
            record.setId(UUID.randomUUID().toString());
            record.setTimestamp(new Date());
            record.setUserId(user.getId());
            record.setAction(operation);
            record.setDenied(denied);
            record.setMessage(message.getText());
            auditService.audit(record);
        } catch (NetworkHubServiceException e) {
            Helper.logException(BASELOGGER, signature, e);
        } catch (JMSException e) {
            Helper.logException(BASELOGGER, signature, e);
        }
    }

    /**
     * Getter for the auditService field
     * 
     * @return the auditService
     */
    public AuditService getAuditService() {
        return auditService;
    }

    /**
     * Setter for the auditService field
     * 
     * @param auditService
     *            the auditService to set
     */
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Getter for the usernameHeader field
     * 
     * @return the usernameHeader
     */
    public String getUsernameHeader() {
        return usernameHeader;
    }

    /**
     * Setter for the usernameHeader field
     * 
     * @param usernameHeader
     *            the usernameHeader to set
     */
    public void setUsernameHeader(String usernameHeader) {
        this.usernameHeader = usernameHeader;
    }

    /**
     * Getter for the passwordHeader field
     * 
     * @return the passwordHeader
     */
    public String getPasswordHeader() {
        return passwordHeader;
    }

    /**
     * Setter for the passwordHeader field
     * 
     * @param passwordHeader
     *            the passwordHeader to set
     */
    public void setPasswordHeader(String passwordHeader) {
        this.passwordHeader = passwordHeader;
    }

    /**
     * Getter for the authenticationService field
     * 
     * @return the authenticationService
     */
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    /**
     * Setter for the authenticationService field
     * 
     * @param authenticationService
     *            the authenticationService to set
     */
    public void setAuthenticationService(
            AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Getter for the authorizationService field
     * 
     * @return the authorizationService
     */
    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    /**
     * Setter for the authorizationService field
     * 
     * @param authorizationService
     *            the authorizationService to set
     */
    public void setAuthorizationService(
            AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Getter for the velocityEngine field
     * 
     * @return the velocityEngine
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * Setter for the velocityEngine field
     * 
     * @param velocityEngine
     *            the velocityEngine to set
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     * Getter for the jmsTemplate field
     * 
     * @return the jmsTemplate
     */
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    /**
     * Setter for the jmsTemplate field
     * 
     * @param jmsTemplate
     *            the jmsTemplate to set
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Getter for the defaultErrorCode field
     * 
     * @return the defaultErrorCode
     */
    public String getDefaultErrorCode() {
        return defaultErrorCode;
    }

    /**
     * Setter for the defaultErrorCode field
     * 
     * @param defaultErrorCode
     *            the defaultErrorCode to set
     */
    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    /**
     * Getter for the defaultErrorMessage field
     * 
     * @return the defaultErrorMessage
     */
    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }

    /**
     * Setter for the defaultErrorMessage field
     * 
     * @param defaultErrorMessage
     *            the defaultErrorMessage to set
     */
    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }

    /**
     * Getter for the exceptionErrorCodes field
     * 
     * @return the exceptionErrorCodes
     */
    public Map<Class<? extends Exception>, String> getExceptionErrorCodes() {
        return exceptionErrorCodes;
    }

    /**
     * Setter for the exceptionErrorCodes field
     * 
     * @param exceptionErrorCodes
     *            the exceptionErrorCodes to set
     */
    public void setExceptionErrorCodes(
            Map<Class<? extends Exception>, String> exceptionErrorCodes) {
        this.exceptionErrorCodes = exceptionErrorCodes;
    }

    /**
     * Getter for the exceptionErrorMessages field
     * 
     * @return the exceptionErrorMessages
     */
    public Map<Class<? extends Exception>, String> getExceptionErrorMessages() {
        return exceptionErrorMessages;
    }

    /**
     * Setter for the exceptionErrorMessages field
     * 
     * @param exceptionErrorMessages
     *            the exceptionErrorMessages to set
     */
    public void setExceptionErrorMessages(
            Map<Class<? extends Exception>, String> exceptionErrorMessages) {
        this.exceptionErrorMessages = exceptionErrorMessages;
    }

    /**
     * Getter for the recoverableExceptions field
     * 
     * @return the recoverableExceptions
     */
    public List<Class<? extends Exception>> getRecoverableExceptions() {
        return recoverableExceptions;
    }

    /**
     * Setter for the recoverableExceptions field
     * 
     * @param recoverableExceptions
     *            the recoverableExceptions to set
     */
    public void setRecoverableExceptions(
            List<Class<? extends Exception>> recoverableExceptions) {
        this.recoverableExceptions = recoverableExceptions;
    }
}
