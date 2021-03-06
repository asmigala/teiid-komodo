/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.komodo.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komodo.spi.constants.StringConstants;
import org.komodo.spi.logging.KLogger.Level;
import org.komodo.utils.FileUtils;
import org.komodo.utils.KLog;
import org.komodo.utils.TestKLog;

/**
 * Configures the logging for tests and most importantly aims
 * to reduce the modeshape logging from DEBUG to INFO.
 */
//@SuppressWarnings( {"nls", "javadoc"} )
public abstract class AbstractLoggingTest implements StringConstants {

    protected static final String TEST_USER = "user";

    private static Path _dataDirectory;

    private static File configureLogPath(KLog logger) throws Exception {
        File newLogFile = File.createTempFile("TestKLog", ".log");
        newLogFile.deleteOnExit();

        logger.setLogPath(newLogFile.getAbsolutePath());
        logger.setLevel(Level.INFO);
//        logger.setLevel(Level.DEBUG);
        assertEquals(newLogFile.getAbsolutePath(), logger.getLogPath());

        return newLogFile;
    }

    protected static Path getLoggingDirectory() {
        return _dataDirectory;
    }

    /**
     * Sets the {@link KLog} level to the desired level
     *
     * @param level
     * @throws Exception
     */
    public void setLoggingLevel(Level level) throws Exception {
        KLog.getLogger().setLevel(level);
    }

    @BeforeClass
    public static void initLogging() throws Exception {
        // create data directory for engine logging
    	_dataDirectory = TestKLog.createEngineDirectory();        

        // Initialises logging and reduces modeshape logging from DEBUG to INFO
        configureLogPath(KLog.getLogger());
    }

    @AfterClass
    public static void shutdown() throws Exception {
        FileUtils.removeDirectoryAndChildren( _dataDirectory.toFile() );
    }   
}

