package algos.graphs;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.DependencyGraph;

import java.util.*;

public class Visiting {
    private final DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;
    private HashSet<String> visited;
    private Set<List<String>> cycles = new HashSet<>();

    public Visiting(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        this.graph = graph;
        visited = new HashSet<>();
    }

    public List<List<String>> run() {
        List<List<String>> toret;
        {
            visited.clear();
            Stack<String> array = new Stack<>();
            cycles.clear();
            array.ensureCapacity(graph.vertexSet().size() + 1);
            for (String x : graph.vertexSet()) {
                if (!visited.contains(x)) {
                    dfsLoopVisit(x, 0, array);
                }
            }
            toret = new ArrayList<>(cycles);
            toret.sort(Comparator.comparingInt(List::size));
        }

        // Removing all the redundant cycles
        for (int i = 0, n = cycles.size(); i<n; i++) {
            List<String> curI = toret.get(i);
            for (int j = 0; j<i; j++) {
                List<String> curJ = toret.get(j);
                if (!cycles.contains(curJ)) continue;
                if (curI.containsAll(curJ)) {
                    cycles.remove(curJ);
                }
            }
        }

        return new ArrayList<>(cycles);
    }

    private void dfsLoopVisit(String x, int i, Stack<String> array) {
        visited.add(x);
        array.push(x);
        for (DependencyGraph.Edge edge : graph.outgoingEdgesOf(x)) {
            if (visited.contains(edge.dst)) {
                // Checking whether the current element has been visited in the current path
                int pos = array.search(edge.dst);
                if (pos >= 0) {
                    cycles.add(new ArrayList<>(array.subList(array.size() - pos, array.size())));
                    // If it is present in the stack, then I found a loop
                }
                // Otherwise, block the recursive iteration
            } else {
                dfsLoopVisit(edge.dst, i+1, array);
            }
        }
        // continues the DFS search
        array.pop();
    }
}
