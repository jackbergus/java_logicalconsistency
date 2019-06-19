package algos.graphs;



import algos.YielderRecursionWithStacks;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.DependencyGraph;
import utils.yield.YieldDefinition;
import utils.yield.Yielderable;

import java.util.*;

public class Tarjan {
    private final DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;
    private HashMap<String, Integer> nodeIndex;
    private HashMap<String, Integer> minDistance;
    int index;
    Stack<String> stack;

    private class TarjanRecursive extends YielderRecursionWithStacks<String, List<String>> {
        public TarjanRecursive(String initCall) {
            super(initCall);
        }
        @Override
        protected Iterable<String> beforeRecursiveCall(String v) {
            nodeIndex.put(v, index);
            minDistance.put(v, index);
            index++;
            stack.push(v);
            Set<DependencyGraph.Edge> ls = graph.outgoingEdgesOf(v);
            ArrayList<String> rec = new ArrayList<>(ls.size());
            for (DependencyGraph.Edge e : ls) {
                Integer vpIndex = nodeIndex.get(e.dst);
                if (vpIndex == null) {
                    rec.add(e.dst);
                }
            }
            if (v.equals("2") || v.equals("74")) {
                System.err.println("DEBUG");
            }
            return rec;
        }
        @Override
        protected void afterRecursiveCall(String prev, Iterable<String> statuses) {
            if (prev.equals("2") || prev.equals("74")) {
                System.err.println("DEBUG");
            }
            Integer val = minDistance.get(prev);
            for (DependencyGraph.Edge e : graph.outgoingEdgesOf(prev)) {
                val = Math.min(val, minDistance.get(e.dst));
            }
            minDistance.put(prev, val);
            checkIfYielding(yield(), stack, prev, minDistance, nodeIndex);
        }

    }

    public Tarjan(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> g) {
        graph = g;
        nodeIndex = new HashMap<>();
        minDistance = new HashMap<>();
        index = 0;
        stack = new Stack<>();
    }

    public Yielderable<List<String>> run() {
        return yield -> {
            for (String currentVertex : graph.vertexSet()) {
                if (nodeIndex.get(currentVertex) == null) {
                    stack.clear();
                    new TarjanRecursive(currentVertex).execute(yield);
                    /*Stack<String> mimedRecursion = new Stack<>();
                    mimedRecursion.push(currentVertex);
                    while (!mimedRecursion.empty()) {
                        String v = mimedRecursion.pop();
                        nodeIndex.put(v, index);
                        minDistance.put(v, index);
                        index++;
                        stack.push(v);
                        for (DependencyGraph.Edge e : graph.outgoingEdgesOf(v)) {
                            Integer vpIndex = nodeIndex.get(e.dst);
                            if (vpIndex == null) {
                                mimedRecursion.push(e.dst);
                                minDistance.put(v, Math.min(minDistance.get(v), minDistance.get(e.dst)));
                            } else if (stack.contains(e.dst)) {
                                minDistance.put(v, Math.min(minDistance.get(v), minDistance.get(e.dst)));
                            }
                        }
                        checkIfYielding(yield, stack, v, minDistance, nodeIndex);
                    }*/
                }
            }
        };
    }

    private static void checkIfYielding(YieldDefinition<List<String>> yield, Stack<String> stack, String v, HashMap<String, Integer> minDistance, HashMap<String, Integer> nodeIndex) {
        if (minDistance.get(v).equals(nodeIndex.get(v))) {
            ArrayList<String> path = new ArrayList<>(stack.size());
            String vp = null;
            do {
                vp = stack.pop();
                path.add(vp);
            } while (!vp.equals(v));
            // TODO: more efficiently, do reverse insertion directly in the right order
            Collections.reverse(path);
            yield.returning(path);
        }
    }
}
