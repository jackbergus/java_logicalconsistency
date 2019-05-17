package org.ufl.hypogator.jackb.utils.debuggers;

import com.google.common.collect.HashMultimap;
import org.jsoup.nodes.Element;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.disambiguation.DisambiguationAlgorithm;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.*;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.ResolvedSpace;
import org.ufl.hypogator.jackb.html.ListBuild;
import org.ufl.hypogator.jackb.html.Table;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.server.handlers.abstracts.GetRequest;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VisualResolver extends GetRequest {

    ConceptNetVocabulary voc;

    public VisualResolver() {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public String handleRequest(HashMultimap<String, String> parameters) {
        Element e = new Element("html");
        e.appendChild(new Element("head").append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"));
        e.appendChild(new Element("style").html("table {\n" +
                "    font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;\n" +
                "    border-collapse: collapse;\n" +
                "    width: 100%;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "    border: 1px solid #ddd;\n" +
                "    padding: 8px;\n" +
                "}\n" +
                "\n" +
                "tr:nth-child(even){background-color: #f2f2f2;}\n" +
                "\n" +
                "tr:hover {background-color: #ddd;}\n" +
                "\n" +
                "th {\n" +
                "    padding-top: 12px;\n" +
                "    padding-bottom: 12px;\n" +
                "    text-align: left;\n" +
                "    background-color: #4CAF50;\n" +
                "    color: white;\n" +
                "}"));
        Element b = new Element("body");

        boolean disambleDisambiguation = parameters.get("disTypingShow").stream().anyMatch(x->x.toLowerCase().equals("true"));

        if (!disambleDisambiguation)  for (String type : parameters.get("type")) {
            Dimension dc = (Dimension) TupleComparator.generateFromField(type);
            DisambiguationAlgorithm disambiguator
                = dc.getAlgorithm(ConfigurationEntrypoint.getInstance().threshold);

            b.appendChild(new Element("h1").text("Type: "+type));
            double scoreProdU = 1.0;
            List<Resolved> rLeft = new ArrayList<>();
            for (String term : parameters.get("term")) {
                DisambiguatedValue u = disambiguator.disambiguate(term);
                for (Object disX : u.disambiguation) {
                    Table t = Table.edgeVertexTable();
                    Triple<String, Resolved, Double> dis = (Triple<String, Resolved, Double>) disX;
                    rLeft.add(dis.second);
                    scoreProdU *= (1.0 - dis.third);
                    if (dis.second instanceof ResolvedConcept) {
                        for (SemanticNetworkEntryPoint ep : ((ResolvedConcept) dis.second).list) {
                            ((EdgeVertex) ep).addToHTMLTable(t);
                        }
                    } else if (dis.second instanceof ResolvedSpace) {
                        for (SemanticNetworkEntryPoint ep : ((ResolvedSpace) dis.second).asSMEPList())
                            ((EdgeVertex) ep).addToHTMLTable(t);
                    }
                    b.appendChild(new Element("h2").text(term + " [" + dis.third + "]"));
                    b.appendChild(t.build());
                    b.appendChild(new Element("p"));
                }
            }

            scoreProdU = 1.0-scoreProdU;

                List<Resolved> rRight = new ArrayList<>();
                double scoreProdV = 1.0;
                if (!disambleDisambiguation)
                for (String term2 : parameters.get("other")) {
                    DisambiguatedValue u2 = disambiguator.disambiguate(term2);
                    for (Object disX : u2.disambiguation) {
                        Table t2 = Table.edgeVertexTable();
                        Triple<String, Resolved, Double> dis = (Triple<String, Resolved, Double>) disX;
                        rRight.add(dis.second);
                        scoreProdV *= (1.0 - dis.third);
                        if (dis.second instanceof ResolvedConcept) {
                            for (SemanticNetworkEntryPoint ep : ((ResolvedConcept)dis.second).list) {
                                ((EdgeVertex) ep).addToHTMLTable(t2);
                            }
                        } else if (dis.second instanceof ResolvedSpace) {
                            for (SemanticNetworkEntryPoint ep: ((ResolvedSpace)dis.second).asSMEPList())
                                ((EdgeVertex)ep).addToHTMLTable(t2);
                        }
                        b.appendChild(new Element("h2").text(term2+" ["+dis.third+"]"));
                        b.appendChild(t2.build());
                        b.appendChild(new Element("p"));
                    }
                }

            scoreProdV = 1.0-scoreProdV;

                b.appendChild(new Element("hr"));
                b.appendChild(new Element("h3").text("Comparisons:"));

                for (String term : parameters.get("term")) {
                    for (String term2 : parameters.get("other")) {
                        PartialOrderComparison cmp = dc.compare(term, term2);
                        b.appendChild(new Element("h4").text(term + " ->" + term2));
                        ListBuild ls = ListBuild.unordered();
                        ls.add("Direction: " + cmp.t);
                        ls.add("Score: " + (cmp.uncertainty /**0.6+ (scoreProdU*scoreProdV*0.4)*/));
                        b.appendChild(ls.build());
                        /* Print no more available

                        Table t3 = Table.edgeVertexTable();
                        for (SemanticNetworkEntryPoint ep : val.getValue())
                            ((EdgeVertex) ep).addToHTMLTable(t3);
                        b.appendChild(t3.build());*/
                        b.appendChild(new Element("p"));
                    }
                }
            //PartialOrderComparison cmp = dc.compare(rLeft, rRight);
                /*for (Resolved l : rLeft) {
                    for (Resolved r : rRight) {
                        Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> cmp = dc.informativeCompare(l, r);
                        if (cmp != null) {
                            String leftString = null;
                            String rightStirng = null;
                            if (l instanceof ResolvedConcept) {
                                leftString = ((ResolvedConcept)l).str;
                            } else if (l instanceof ResolvedSpace) {
                                leftString = ((ResolvedSpace)l).getMatchedName();
                            }
                            if (r instanceof ResolvedConcept) {
                                rightStirng = ((ResolvedConcept)r).str;
                            } else if (r instanceof ResolvedSpace) {
                                rightStirng = ((ResolvedSpace)r).getMatchedName();
                            }
                            if (!cmp.getKey().equals(Direction.NONE)) {
                                b.appendChild(new Element("h4").text(leftString + " ->" + rightStirng));
                                ListBuild ls = ListBuild.unordered();
                                ls.add("Direction: " + cmp.getKey().toString());
                                Pair<Double, List<SemanticNetworkEntryPoint>> val = null;
                                if (cmp.getValue().isPresent()) {
                                    val = cmp.getValue().get();
                                    ls.add("Score: " + (val.getKey() *0.6+ (scoreProdU*scoreProdV*0.4)));
                                }
                                b.appendChild(ls.build());
                                if (val != null) {
                                    Table t3 = Table.edgeVertexTable();
                                    for (SemanticNetworkEntryPoint ep : val.getValue())
                                        ((EdgeVertex) ep).addToHTMLTable(t3);
                                    b.appendChild(t3.build());
                                }
                                b.appendChild(new Element("p"));
                            }
                        }
                    }
                    b.appendChild(new Element("p"));
                    b.appendChild(new Element("hr"));
                }*/

            }
            e.appendChild(b);
            b.appendChild(new Element("p"));
            b.appendChild(new Element("hr"));

        setAnswerBody("text/html");
        return e.toString();
    }

}
