package algos.graphs;


import javafx.util.Pair;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.DependencyGraph;
import utils.yield.YieldDefinition;
import utils.yield.Yielderable;

import java.util.*;

public class AllDirectedPaths2 {

    private final DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;
    //Set<List<String>> nodes;

    public AllDirectedPaths2(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        this.graph = graph;
        //nodes = new HashSet<>();
    }

    void printAllPathsUtil(String src, String d, Set<String> visited,
                           String[] path, boolean first, YieldDefinition<List<String>> yield)
    {
        Stack<Pair<String, Integer>> nextElements = new Stack<>();
        nextElements.push(new Pair<>(src, 0));
        // Mark the current node and store it in path[]

        while (!nextElements.isEmpty()) {
            Pair<String, Integer> cp = nextElements.peek();
            String u = cp.getKey();
            Integer path_index = cp.getValue();
            if (!visited.add(u)) {
                // XXXX: visited.remove(u);
                nextElements.pop();
                continue;
            }

            path[path_index] = u;
            //path_index++;

            // If current vertex is same as destination, then print
            // current path[]
            if (u.equals(d)) {
                List<String> toSet = Arrays.asList(path).subList(0, path_index+1);
               // System.out.println(toSet);
                //nodes.add(toSet);
                yield.returning(toSet);
                if (first) {
                    yield.breaking();
                    return;
                }
                // XXX: visited.remove(u);
                nextElements.pop();
            }
            else // If current vertex is not destination
            {
                // Recur for all the vertices adjacent to current vertex
                boolean insertion = false;
                for (DependencyGraph.Edge e : graph.outgoingEdgesOf(u)) {
                    String tgt = graph.getEdgeTarget(e);
                    if (!visited.contains(tgt)) {
                        nextElements.push(new Pair<>(tgt, path_index+1));
                        insertion = true;
                        /*if (printAllPathsUtil(tgt, d, visited, path, path_index, first))
                            return true;*/
                    }
                }
                if (!insertion) {
                    // XXX: visited.remove(u);
                    nextElements.pop();
                }
            }
        }

    }

    public Yielderable<List<String>> getAllPaths(Collection<String> source, Collection<String> target, boolean getFirst) {
       // nodes.clear();
        return yield -> {
            String[] nodes = new String[graph.vertexSet().size()];
            Set<String> visited = new HashSet<>();
            for (String src : source) {
                for (String dst : target) {
                    visited.clear();
                    //System.out.println(src+" -- "+dst);
                    printAllPathsUtil(src, dst, visited, nodes,  getFirst, yield);
                }
            }
        };
        //return this.nodes;
    }


    public Yielderable<List<String>> getAllPaths(String src, String dst, boolean getFirst) {
        return yield -> {
            String[] nodes = new String[graph.vertexSet().size()];
            Set<String> visited = new HashSet<>();
            visited.clear();
            //System.out.println(src + " -- " + dst);
            AllDirectedPaths2.this.printAllPathsUtil(src, dst, visited, nodes,  getFirst, yield);
        };
    }

}
