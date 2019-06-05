/*
 * Hypotheses.java
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

package org.ufl.hypogator.jackb.m9;

public final class Hypotheses {
    public final Subgraph subgraphs[];
    public final Query query;

    public Hypotheses(Subgraph[] subgraphs, Query query){
        this.subgraphs = subgraphs;
        this.query = query;
    }

    public static final class Subgraph {
        public final Hypothesis_scorer hypothesis_scorers[];
        public final Scorer scorers[];

        public Subgraph(Hypothesis_scorer[] hypothesis_scorers, Scorer[] scorers){
            this.hypothesis_scorers = hypothesis_scorers;
            this.scorers = scorers;
        }

        public static final class Hypothesis_scorer {
            public final Subgraph_plus_neighbor subgraph_plus_neighbors[];
            public final String name;
            public final long value;

            public Hypothesis_scorer(Subgraph_plus_neighbor[] subgraph_plus_neighbors, String name, long value){
                this.subgraph_plus_neighbors = subgraph_plus_neighbors;
                this.name = name;
                this.value = value;
            }

            public static final class Subgraph_plus_neighbor {
                public final String node_text_1;
                public final String node_text_2;
                public final long node_internal_id_2;
                public final long node_internal_id_1;
                public final boolean is_in_subgraph;
                public final String node_id_1;
                public final String node_id_2;
                public final String relation_id;
                public final String relevance;
                public final String relation_text;
                public final long relevance_score;



                public Subgraph_plus_neighbor(String node_text_1, String node_text_2, long node_internal_id_2, long node_internal_id_1, boolean is_in_subgraph, String node_id_1, String node_id_2, String relation_id, String relevance, String relation_text, long relevance_score){
                    this.node_text_1 = node_text_1;
                    this.node_text_2 = node_text_2;
                    this.node_internal_id_2 = node_internal_id_2;
                    this.node_internal_id_1 = node_internal_id_1;
                    this.is_in_subgraph = is_in_subgraph;
                    this.node_id_1 = node_id_1;
                    this.node_id_2 = node_id_2;
                    this.relation_id = relation_id;
                    this.relevance = relevance;
                    this.relation_text = relation_text;
                    this.relevance_score = relevance_score;
                }

                @Override
                public String toString() {
                    return "Subgraph_plus_neighbor{" +
                            "node_text_1='" + node_text_1 + '\'' +
                            ", node_text_2='" + node_text_2 + '\'' +
                            ", node_internal_id_2=" + node_internal_id_2 +
                            ", node_internal_id_1=" + node_internal_id_1 +
                            ", is_in_subgraph=" + is_in_subgraph +
                            ", node_id_1='" + node_id_1 + '\'' +
                            ", node_id_2='" + node_id_2 + '\'' +
                            ", relation_id='" + relation_id + '\'' +
                            ", relevance='" + relevance + '\'' +
                            ", relation_text='" + relation_text + '\'' +
                            ", relevance_score=" + relevance_score +
                            '}';
                }
            }
        }

        public static final class Scorer {
            public final String name;
            public final double value;

            public Scorer(String name, double value){
                this.name = name;
                this.value = value;
            }

            @Override
            public String toString() {
                return name+"="+value;
            }
        }
    }

    public static final class Query {
        public final String start_seed;
        public final String query_id;
        public final String end_seed;
        public final String topic_id;

        public Query(String start_seed, String query_id, String end_seed, String topic_id){
            this.start_seed = start_seed;
            this.query_id = query_id;
            this.end_seed = end_seed;
            this.topic_id = topic_id;
        }
    }
}