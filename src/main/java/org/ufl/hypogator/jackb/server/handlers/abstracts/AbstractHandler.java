/*
 * AbstractHandler.java
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

package org.ufl.hypogator.jackb.server.handlers.abstracts;

import com.google.common.collect.HashMultimap;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

public abstract class AbstractHandler implements HttpHandler {

    protected static void doAnswer(HttpExchange he, Headers headers, String responseBody, boolean returnError, String answerBody) throws IOException {
        if (returnError || (responseBody == null)) {
            he.sendResponseHeaders(404, 0);
            he.getResponseBody().write(new byte[]{});
        } else {
            headers.set("Content-Type", String.format(answerBody+"; charset=%s", StandardCharsets.UTF_8));
            final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
            he.sendResponseHeaders(200, rawResponseBody.length);
            he.getResponseBody().write(rawResponseBody);
        }
    }

    protected static HashMultimap<String, String> getRequestParameters(final URI requestUri) {
        final HashMultimap<String, String> requestParameters = HashMultimap.create();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                final String requestParameterName = decodeUrlComponent(requestParameter[0]);
                final String requestParameterValue = requestParameter.length > 1 ? decodeUrlComponent(requestParameter[1]) : null;
                requestParameters.put(requestParameterName, requestParameterValue);
            }
        }
        return requestParameters;
    }

    protected static String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, UTF_8);
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }

}
