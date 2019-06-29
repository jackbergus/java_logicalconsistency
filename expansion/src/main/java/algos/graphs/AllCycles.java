package algos.graphs;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.DependencyGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AllCycles {
    private final DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;
    private HashSet<String> visited;
    private Set<List<String>> cycles = new HashSet<>();

    public AllCycles(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        this.graph = graph;
        visited = new HashSet<>();
    }

    public List<List<String>> run() {
        List<List<String>> toret;
        {
            visited.clear();
            Stack<String> array = new Stack<>();
            cycles.clear();

            // Getting all the distinct cycles
            array.ensureCapacity(graph.vertexSet().size() + 1);
            for (String x : graph.vertexSet()) {
                if (!visited.contains(x)) {
                    dfsLoopVisit(x, 0, array);
                }
            }

            // Moving that into a list, so we can iterate over those and access by id
            toret = new ArrayList<>(cycles);
            toret.sort(Comparator.comparingInt(List::size));
        }

        // Removing all the redundant cycles
        for (int i = 0, n = cycles.size(); i<n; i++) {
            List<String> curI = toret.get(i);
            for (int j = 0; j<i; j++) {
                List<String> curJ = toret.get(j);
                if (!cycles.contains(curJ)) continue;
                // Removing all the cycles that are contained in bigger cycles: we reduce the number of the loops to visit
                if (curI.containsAll(curJ)) {
                    cycles.remove(curJ);
                }
            }
        }

        // Returning the remained loops
        return new ArrayList<>(cycles);
    }

    public static List<List<String>> removeSubpaths(Iterable<List<String>> collection) {
        List<List<String>> toret;
        {
            Set<List<String>> intermediate = new HashSet<>();
            collection.forEach(intermediate::add);
            toret = new ArrayList<>(intermediate);
        }
        toret.sort(Comparator.comparingInt(List::size));
        Set<Integer> posToRemove = new HashSet<>(toret.size());

        // Removing all the redundant cycles
        for (int i = 0, n = toret.size(); i<n; i++) {
            List<String> curI = toret.get(i);
            for (int j = 0; j<i; j++) {
                List<String> curJ = toret.get(j);
                if (posToRemove.contains(j)) continue;
                // Removing all the cycles that are contained in bigger cycles: we reduce the number of the loops to visit
                if (Collections.indexOfSubList(curI,(curJ)) != -1) {
                    posToRemove.add(j);
                }
            }
        }

        // Returning the remained loops
        return IntStream.range(0, toret.size())
                .filter(i -> !posToRemove.contains(i))
                .mapToObj(toret::get)
                .collect(Collectors.toList());
    }

    public static List<List<String>> removeSuppaths(Iterable<List<String>> collection) {
        List<List<String>> toret;
        {
            Set<List<String>> intermediate = new HashSet<>();
            collection.forEach(intermediate::add);
            toret = new ArrayList<>(intermediate);
        }
        toret.sort(Comparator.comparingInt(List::size));
        Set<Integer> posToRemove = new HashSet<>(toret.size());

        // Removing all the redundant cycles
        for (int i = 0, n = toret.size(); i<n; i++) {
            List<String> curI = toret.get(i);
            for (int j = 0; j<i; j++) {
                List<String> curJ = toret.get(j);
                // Removing all the cycles that are contained in bigger cycles: we reduce the number of the loops to visit
                if (Collections.indexOfSubList(curI,(curJ)) != -1) {
                    posToRemove.add(i);
                    break;
                }
            }
        }

        // Returning the remained loops
        return IntStream.range(0, toret.size())
                .filter(i -> !posToRemove.contains(i))
                .mapToObj(toret::get)
                .distinct()
                .collect(Collectors.toList());
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
