/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network.node.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.hfpp.network.node.ConfigurationException;
import com.hfpp.network.node.Helper;

/**
 * <p>
 * This is a generic MessageListener implementation that forwards(via HTTP POST) received messages to configured
 * callback URI.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is effectively thread safe since its internal state isn't expected to
 * change after Spring IoC initialization, and all dependencies are thread safe.
 * </p>
 *
 * @author flying2hk, sparemax
 * @version 1.0
 */
public class GenericForwardMessageListener implements MessageListener {
    /**
     * <p>
     * Represents the class name.
     * </p>
     */
    private static final String CLASS_NAME = GenericForwardMessageListener.class.getName();

    /**
     * <p>
     * Represents the error message.
     * </p>
     */
    private static final String MESSAGE_ERROR = "Error in method %1$s. Details: %2$s";

    /**
     * Represents the logger used to perform logging.
     */
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Represents the callback URL to which the received messages will be posted. It should be non-null, non-empty
     * string that represents a valid URL, the "protocol" component of the URL must represent HTTPS. It is required.
     */
    private String callbackURL;

    /**
     * Represents the JmsTemplate used to send and receive JMS messages. It should be non-null. It is required.
     */
    private JmsTemplate jmsTemplate;

    /**
     * Creates an instance of GenericForwardMessageListener.
     */
    public GenericForwardMessageListener() {
        // Empty
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException
     *             if any required field is not initialized properly (callbackURL is null/empty).
     */
    @PostConstruct
    public void checkConfiguration() {
        Helper.checkState(Helper.isNullOrEmpty(callbackURL), "'callbackURL' can't be null/empty.");
        Helper.checkState(jmsTemplate == null, "'jmsTemplate' can't be null.");
    }

    /**
     * Gets the logger used to perform logging.
     *
     * @return the logger used to perform logging.
     */
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Sets the callback URL to which the received messages will be posted.
     *
     * @param callbackURL
     *            the callback URL to which the received messages will be posted.
     */
    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    /**
     * Sets the JmsTemplate used to send and receive JMS messages.
     *
     * @param jmsTemplate
     *            the JmsTemplate used to send and receive JMS messages.
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    
    /**
     * The custom trust manager
     */
    class TrustAnyTrustManager implements X509TrustManager {
        
        /**
         * The empty constructor
         */
        public TrustAnyTrustManager() {

        }
        
        /**
         * The check Client Trusted function
         * @param chain the X509Certificate
         * @param authType the auth type
         * @throws CertificateException throws if any error happen
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        /**
         * The check server Trusted function
         * @param chain the X509Certificate
         * @param authType the auth type
         * @throws CertificateException throws if any error happen
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        /**
         * The getAcceptedIssuers function
         * @return the X509Certificate
         */
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }

    }

    /**
     * Reply message to the sender specified queue.
     * 
     * @param message the message received
     * @param replyXML the string of XML format that is going to reply
     * @throws JMSException when error occurs during sending jms message
     */
	private void replyMessage(final Message message, final String replyXML) throws JMSException {
		Destination replyQueue = message.getJMSReplyTo();
		if (replyQueue == null) {
		    return;
		}
		jmsTemplate.send(replyQueue,
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
			 * @author TCSASSEMBLER
			 * @version 1.0
			 */
			new MessageCreator() {
				/**
				 * Creates the message.
				 * 
				 * @param session
				 *            the session
				 * 
				 * @throws JMSException
				 *             if any error occurs
				 */
				@Override
				public Message createMessage(Session session) throws JMSException {
					String reallyReply = "";
					if (replyXML != null && replyXML.trim().length() > 0) {
						reallyReply = replyXML;
                    } else {
                    	reallyReply = "<NoError></NoError>";
                    }
                    Message reply = session.createTextMessage(reallyReply);
                    return reply;
				}
			});
	}

    /**
     * This method is called when a message is received from the queue.
     *
     * @param message
     *            the received message
     */
    public void onMessage(Message message) {
        String signature = CLASS_NAME + ".onMessage(Message message)";

        // Log entry
        Helper.logEntrance(LOGGER, signature,
            new String[] {"message"},
            new Object[] {message});

        for(int i=0; i<5; i++) {
            try {
                if(tryProcessMessage(message, signature))break;
                Thread.sleep(3000);
            } catch (Exception e) {
                // Yield
            }
        }

       
        /*if (message instanceof TextMessage) {
            try {
                String xml = ((TextMessage) message).getText()+'\n';
                LOGGER.debug("callback url :"+callbackURL);
                LOGGER.debug("xml :"+(xml == null ? "null" : xml));
                HttpsURLConnection connection = null;
                HttpURLConnection httpConnection = null;
                try {
                    //currently just use mock callback for response and analysis
                    if(callbackURL== null ){
                        message.acknowledge();
                        return;
                    }
                    OutputStream outputStream = null;
                    URL url = new URL(callbackURL);
                    if(callbackURL.toLowerCase().startsWith("https://")){
                        //currently just use trust any for ssl support
                        SSLContext sslContext= SSLContext.getInstance("SSL");
                        TrustManager[] tm = { new TrustAnyTrustManager() };
                        sslContext.init(null, tm, new SecureRandom());
                        SSLSocketFactory ssf = sslContext.getSocketFactory();
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setSSLSocketFactory(ssf);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                        connection.setRequestProperty("Content-Length", "" + (xml == null ? 0 : xml.getBytes("utf-8").length));
                        // Use default chunk length
                        connection.setChunkedStreamingMode(0);
                        outputStream = connection.getOutputStream();
                    }else{
                        httpConnection = (HttpURLConnection) url.openConnection();
                        httpConnection.setDoOutput(true);
                        httpConnection.setRequestMethod("POST");
                        httpConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                        httpConnection.setRequestProperty("Content-Length", "" + (xml == null ? 0 : xml.getBytes("utf-8").length));
                        outputStream = httpConnection.getOutputStream();
                    }
                   
                    try {
                        outputStream.write(xml.getBytes("utf-8"));
                        outputStream.flush();
                    } finally {
                        outputStream.close();
                    }

                    int responseCode = connection!= null? connection.getResponseCode()
                            :httpConnection.getResponseCode();
                    LOGGER.debug("responseCode :"+responseCode);
                    switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                        // succeeded
                        message.acknowledge();
                        replyMessage(message, "");
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        // recoverable error, log error
                        // Log exception
                        LOGGER.error(String.format(MESSAGE_ERROR, signature, "Internal server error has occurred."));
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                    default:
                        // unrecoverable error, log error and acknowledge message
                        // Log exception
                        LOGGER.error(String.format(MESSAGE_ERROR, signature, "An error has occurred."));
                        message.acknowledge();
                        replyMessage(message, "");
                        break;
                    }
                } catch (IOException e) {
                    // Connection related error, log the error and do not acknowledge the message
                    // Log exception
                    Helper.logException(LOGGER, signature, e);
                } catch (JMSException e) {
                    // Other error, log the error and acknowledge the message
                    // Log exception
                    Helper.logException(LOGGER, signature, e);
                    message.acknowledge();
                    replyMessage(message, "");
                } catch (KeyManagementException e) {
                    Helper.logException(LOGGER, signature, e);
                } catch (NoSuchAlgorithmException e) {
                    Helper.logException(LOGGER, signature, e);
                    message.acknowledge();
                    replyMessage(message, "");
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if(httpConnection != null){
                        httpConnection.disconnect();
                    }
                }
            } catch (JMSException e) {
                // Log exception
                Helper.logException(LOGGER, signature, e);
            }
        }*/

        // Log exit
        Helper.logExit(LOGGER, signature, null);
    }

    private boolean tryProcessMessage(Message message, String signature) {
        boolean terminating = true;
        if (message instanceof TextMessage) {
            try {
                String xml = ((TextMessage) message).getText()+'\n';
                LOGGER.debug("callback url :"+callbackURL);
                LOGGER.debug("xml :"+(xml == null ? "null" : xml));
                HttpsURLConnection connection = null;
                HttpURLConnection httpConnection = null;
                try {
                    //currently just use mock callback for response and analysis
                    if(callbackURL== null ){
                        message.acknowledge();
                        return true;
                    }
                    OutputStream outputStream = null;
                    URL url = new URL(callbackURL);
                    if(callbackURL.toLowerCase().startsWith("https://")){
                        //currently just use trust any for ssl support
                        SSLContext sslContext= SSLContext.getInstance("SSL");
                        TrustManager[] tm = { new TrustAnyTrustManager() };
                        sslContext.init(null, tm, new SecureRandom());

                        SSLSocketFactory ssf = sslContext.getSocketFactory();
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setSSLSocketFactory(ssf);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                        connection.setRequestProperty("Content-Length", "" + (xml == null ? 0 : xml.getBytes("utf-8").length));
                        // Use default chunk length
                        connection.setChunkedStreamingMode(0);
                        outputStream = connection.getOutputStream();
                    }else{
                        httpConnection = (HttpURLConnection) url.openConnection();
                        httpConnection.setDoOutput(true);
                        httpConnection.setRequestMethod("POST");
                        httpConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                        httpConnection.setRequestProperty("Content-Length", "" + (xml == null ? 0 : xml.getBytes("utf-8").length));
                        outputStream = httpConnection.getOutputStream();
                    }
                   
                    try {
                        outputStream.write(xml.getBytes("utf-8"));
                        outputStream.flush();
                    } finally {
                        outputStream.close();
                    }

                    int responseCode = connection!= null? connection.getResponseCode()
                            :httpConnection.getResponseCode();
                    LOGGER.debug("responseCode :"+responseCode);
                    switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                        // succeeded
                        message.acknowledge();
                        replyMessage(message, "");
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        // recoverable error, log error
                        // Log exception
                        LOGGER.error(String.format(MESSAGE_ERROR, signature, "Internal server error has occurred."));
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        // recoverable error, log error
                        // Log exception
                        LOGGER.error(String.format(MESSAGE_ERROR, signature, "Bad request, try again."));
                        // Give another chance.
                        terminating = false;
                        break;
                    default:
                        // unrecoverable error, log error and acknowledge message
                        // Log exception
                        LOGGER.error(String.format(MESSAGE_ERROR, signature, "An error has occurred."));
                        message.acknowledge();
                        replyMessage(message, "");
                        break;
                    }
                } catch (IOException e) {
                    // Connection related error, log the error and do not acknowledge the message
                    // Log exception
                    Helper.logException(LOGGER, signature, e);
                } catch (JMSException e) {
                    // Other error, log the error and acknowledge the message
                    // Log exception
                    Helper.logException(LOGGER, signature, e);
                    message.acknowledge();
                    replyMessage(message, "");
                } catch (KeyManagementException e) {
                    Helper.logException(LOGGER, signature, e);
                } catch (NoSuchAlgorithmException e) {
                    Helper.logException(LOGGER, signature, e);
                    message.acknowledge();
                    replyMessage(message, "");
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if(httpConnection != null){
                        httpConnection.disconnect();
                    }
                }
            } catch (JMSException e) {
                // Log exception
                Helper.logException(LOGGER, signature, e);
            }
        }
        return terminating;
    }
}

