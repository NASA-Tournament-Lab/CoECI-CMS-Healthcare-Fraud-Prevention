/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.hfpp.network;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>
 * The base class for unit tests.
 * </p>
 * 
 * <p>
 * v1.1 Changes:
 * <ul>
 * <li>change to get EntityManagerFactory from spring container</li>
 * <li>create shared spring context for all tests </>
 * </ul>
 * </p>
 * 
 * @author sparemax, TCSASSEMBLER
 * @version 1.1
 */
public class BaseUnitTests {
    /**
     * <p>
     * Represents the path of test files.
     * </p>
     */
    public static final String TEST_FILES = "test_files" + File.separator;

    /**
     * <p>
     * Represents the path of configuration files.
     * </p>
     */
    public static final String CONF_FILES = "conf" + File.separator;

    /**
     * <p>
     * Represents the path of velocity templates.
     * </p>
     */
    public static final String VELOCIY_TEMPLATES = CONF_FILES
            + "velocity_templates" + File.separator;

    /**
     * <p>
     * Represents the path of SQL files.
     * </p>
     */
    private static final String SQL_FILES = "src" + File.separator + "sql"
            + File.separator;

    /**
     * <p>
     * Represents the <code>EntityManagerFactory </code> for tests.
     * </p>
     */
    private static EntityManagerFactory factory;

    /**
     * <p>
     * Represents the entity manager used in tests.
     * </p>
     */
    private EntityManager entityManager;

    /**
     * <p>
     * Represents the application context.
     * </p>
     * 
     * @since 1.1
     */
    public static final ApplicationContext APP_CONTEXT;

    /**
     * Initialization.
     * 
     * @since 1.1
     */
    static {
        APP_CONTEXT = new ClassPathXmlApplicationContext(
                "applicationContext.xml", "test-context.xml");
        factory = (EntityManagerFactory) APP_CONTEXT
                .getBean("entityManagerFactoryTest");
    }

    /**
     * <p>
     * Sets up the unit tests.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     * @since 1.1
     */
    @Before
    public void setUp() throws Exception {
        entityManager = factory.createEntityManager();
        clearDB();
        loadData();
    }

    /**
     * <p>
     * Cleans up the unit tests.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    @After
    public void tearDown() throws Exception {
        clearDB();

        if (entityManager.isOpen()) {
            entityManager.close();
        }
        entityManager = null;
    }

    /**
     * <p>
     * Gets value for field of given object.
     * </p>
     * 
     * @param obj
     *            the given object.
     * @param field
     *            the field name.
     * 
     * @return the field value.
     */
    public static Object getField(Object obj, String field) {
        Object value = null;
        try {
            Field declaredField = null;

            try {
                declaredField = obj.getClass().getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                // Ignore
            }

            if (declaredField == null) {
                try {
                    declaredField = obj.getClass().getSuperclass()
                            .getDeclaredField(field);
                } catch (NoSuchFieldException e) {
                    // Ignore
                }
            }

            if (declaredField == null) {
                declaredField = obj.getClass().getSuperclass().getSuperclass()
                        .getDeclaredField(field);
            }

            declaredField.setAccessible(true);

            try {
                value = declaredField.get(obj);
            } finally {
                declaredField.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            // Ignore
        } catch (NoSuchFieldException e) {
            // Ignore
        }

        return value;
    }

    /**
     * <p>
     * Gets the entity manager.
     * </p>
     * 
     * @return the entity manager.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * <p>
     * Clears the database.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    protected void clearDB() throws Exception {
        executeSQL(SQL_FILES + "clear.sql");
    }

    /**
     * <p>
     * Loads the data into database.
     * </p>
     * 
     * @throws Exception
     *             to JUnit.
     */
    private void loadData() throws Exception {
        executeSQL(SQL_FILES + "data.sql");
    }

    /**
     * <p>
     * Executes the SQL statements in the file.
     * </p>
     * 
     * @param file
     *            the file.
     * 
     * @throws Exception
     *             to JUnit.
     */
    private void executeSQL(String file) throws Exception {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        String[] values = readFile(file).split(";");

        for (int i = 0; i < values.length; i++) {
            String sql = values[i].trim();
            if ((sql.length() != 0) && (!sql.startsWith("#"))) {
                em.createNativeQuery(sql).executeUpdate();
            }
        }

        em.getTransaction().commit();
        em.close();
        em = null;
    }

    /**
     * <p>
     * Reads the content of a given file.
     * </p>
     * 
     * @param fileName
     *            the name of the file to read.
     * 
     * @return a string represents the content.
     * 
     * @throws IOException
     *             if any error occurs during reading.
     */
    private static String readFile(String fileName) throws IOException {
        Reader reader = new FileReader(fileName);

        try {
            // Create a StringBuilder instance
            StringBuilder sb = new StringBuilder();

            // Buffer for reading
            char[] buffer = new char[1024];

            // Number of read chars
            int k = 0;

            // Read characters and append to string builder
            while ((k = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, k);
            }

            // Return read content
            return sb.toString();
        } finally {
            try {
                reader.close();
            } catch (IOException ioe) {
                // Ignore
            }
        }
    }
}
