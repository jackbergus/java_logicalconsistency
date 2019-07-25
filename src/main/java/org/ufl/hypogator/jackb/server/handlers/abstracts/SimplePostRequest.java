/*
 * SimplePostRequest.java
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

import java.io.IOException;
import java.util.Scanner;

public abstract class SimplePostRequest extends AbstractHandler {

    private boolean returnError = false;
    public void setReturnError() {
        this.returnError = true;
    }

    private String contentType = "text";
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String answerBody = "";
    public void setAnswerBody(String answer) {
        this.answerBody = answer;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        try {
            final Headers headers = he.getResponseHeaders();
            final String requestMethod = he.getRequestMethod().toUpperCase();
            switch (requestMethod) {
                case "POST":
                    final HashMultimap<String, String> requestParameters = getRequestParameters(he.getRequestURI());
                    String responseBody = handleContent(new Scanner(he.getRequestBody()).useDelimiter("\\A").next(), requestParameters);
                    doAnswer(he, headers, responseBody, returnError, contentType);
                    break;
                default:
                    headers.set("Allow", "POST");
                    he.sendResponseHeaders(405, 0);
                    break;
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }  finally {
            he.close();
        }
    }


    /**
     *
     * @param content           Provides the file content
     * @return                  Body to be answered
     */
    public abstract String handleContent(String content, HashMultimap<String, String> requestParameters);

}
