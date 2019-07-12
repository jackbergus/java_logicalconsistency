/*
 * JsonQuery.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.queries;

import com.google.common.collect.Streams;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.BatchAnswerIterator;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.FirstBatchAnswer;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.db.PgObjectString;
import org.ufl.hypogator.jackb.utils.FilteredIterator;
import org.ufl.hypogator.jackb.utils.Iterators;
import org.ufl.aida.ldc.dbloader.MapIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Stream;

/**
 * Lazy query representation
 */
public class JsonQuery implements Iterable<Edge> {

    private static final String ISEND = "end=";
    public final String rawQuery;
    private final boolean useConceptNetWebApi;
    private final boolean onlyEnglish;
    public final static Concept5ClientConfigurations confs = Concept5ClientConfigurations.instantiate();
    public final static ConceptNet5Postgres scia = ConceptNet5Postgres.getInstance();
    private static String ISQUERY = "/query?";
    private static String ISSTART = "start=";
    private static String ISREL = "rel=";
    private String end;
    private String start;
    private List<String> relationship;
    private String isSingleNode;

    private String urldecode(String decode) {
        try {
            return URLDecoder.decode(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return decode;
        }
    }

    public JsonQuery(String query, boolean useConceptNetWebApi, boolean onlyEnglish) {
        this.rawQuery = query;
        this.useConceptNetWebApi = useConceptNetWebApi;
        this.onlyEnglish = onlyEnglish;
        int queryIndex = query.indexOf(ISQUERY);
        if (queryIndex >= 0) {
            query = query.substring(queryIndex+ISQUERY.length());
            int n;
            if (query.startsWith(ISSTART)) {
                n = ISSTART.length();
                query = query.substring(n);
                int idx = query.indexOf("&");
                idx = idx >= 0 ? idx : query.length();
                start = urldecode(query.substring(0, idx));
                end = null;
            } else if (query.startsWith(ISEND)) {
                n = ISEND.length();
                query = query.substring(n);
                int idx = query.indexOf("&");
                idx = idx >= 0 ? idx : query.length();
                end = query.substring(0, idx);
                start = null;
            }
            int hasRelationship = query.indexOf(ISREL);
            if (hasRelationship >= 0) {
                relationship = Arrays.asList(urldecode(query.substring(hasRelationship+ISREL.length())).split("\\|"));
            } else {
                relationship = Collections.emptyList();
            }
            isSingleNode = null;
        } else {
            isSingleNode = urldecode(query);
        }
    }

    private Stream<String> decodeMultiQueries() {
        if (isSingleNode != null || !relationship.isEmpty()) {
            String begin = ISQUERY+(start != null ? (ISSTART+start) : (ISEND+end));
            return relationship.stream().map(x -> begin+ISREL+x);
        } else
            return Streams.stream(Optional.of(rawQuery));
    }

    public Iterator<Edge> conceptNetWebApi() {
        return new Iterators<>(new MapIterator<String, Iterator<Edge>>(decodeMultiQueries().iterator()) {
            @Override
            public Iterator<Edge> apply(String s) {
                return new Iterators<>(oldConceptNetWebApi(s));
            }
        });
    }

    public Iterator<Iterator<Edge>> oldConceptNetWebApi(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept", "application/json");
            int responseCode = con.getResponseCode();
            //System.out.println("\nSending 'GET' request to URL : " + url);
            //System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print in String
            String responseString = response.toString();
            //Read JSON response and print
            return new EdgeIterators(confs.jsonSerializer.readValue(responseString, FirstBatchAnswer.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class EdgeIterators implements Iterator<Iterator<Edge>> {
        BatchAnswerIterator ai;

        public EdgeIterators(FirstBatchAnswer first) {
            ai = new BatchAnswerIterator(first);
        }

        public boolean hasNext() {
            return ai.hasNext();
        }

        public Iterator<Edge> next() {
            return ai.next().iterator();
        }
    }

    /**
     * Provides the iterator over the outgoing edges from the current node
     *
     * @return
     */
    @Override
    public Iterator<Edge> iterator() {
        Iterator<Edge> ie = null;
        if (useConceptNetWebApi)
            ie = conceptNetWebApi();
        else {
            final boolean singleNode;
            Stream<ConceptNet5Postgres.APIRecord> ms;
            if (isSingleNode != null) {
                ms = scia.execute(isSingleNode, null, isSingleNode, true, confs.retrieveOnlyEnglishConcepts());
            } else {
                ms = scia.execute(start, relationship, end, false,  confs.retrieveOnlyEnglishConcepts());
            }
            ie = ms.map(x -> {
                try {
                    return confs.jsonSerializer.readValue(x.getPgObject(), PgObjectString.class).asBackwardCompatibilityEdge();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).iterator();
        }
        return confs.retrieveOnlyEnglishConcepts() ? new FilteredIterator<>(ie, Edge::anglophoneAndNotVerbOrAdjective) : ie;
    }

    public Iterable<Edge> asIterable() {
        return this::iterator;
    }
}
