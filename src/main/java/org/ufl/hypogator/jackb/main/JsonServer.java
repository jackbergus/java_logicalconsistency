/*
 * JsonServer.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.main;/*
 * org.ufl.hypogator.jackb.main.JsonServer.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

import com.sun.net.httpserver.HttpServer;
import org.ufl.hypogator.jackb.server.handlers.concrete.Baseline2;
import org.ufl.hypogator.jackb.server.handlers.concrete.LoadDatabaseViaRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Field;

public class JsonServer {
    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 9998;
    private static final int BACKLOG = 1;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;

    public static void main(final String... args) throws Exception {
	System.setProperty("file.encoding","UTF-8");
	Field charset = Charset.class.getDeclaredField("defaultCharset");
	charset.setAccessible(true);
	charset.set(null,null);


        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        server.createContext("/kbAllInconsistency", new Baseline2());
        server.createContext("/factLoad", new LoadDatabaseViaRequest());

        // TODO: the old version required expressively an algorithm to make the disambiguation work. Now, this thing
        // TODO: seems not to be needed anymore.
        // TODO: For debugging purposes, it would be convinient to replace it
        //server.createContext("/cnresolve", new VisualResolver());

        server.start();
    }

}