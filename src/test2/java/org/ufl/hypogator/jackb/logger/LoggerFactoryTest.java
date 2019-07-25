package org.ufl.hypogator.jackb.logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoggerFactoryTest {

    Logger logger;

    @Before
    public void setUp() throws Exception {
        logger = LoggerFactory.getLogger(LoggerFactoryTest.class);
    }

    @After
    public void tearDown() throws Exception {
        logger.close();
    }

    @Test
    public void getLogger() {
        logger.debug("Test works");
    }
}