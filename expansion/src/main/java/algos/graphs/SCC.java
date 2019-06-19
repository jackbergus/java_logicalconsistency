package algos.graphs;

import algos.YielderRecursionWithStacks;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Streams;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.DependencyGraph;
import utils.yield.Yielderable;

import java.util.*;
import java.util.stream.Stream;

public class SCC {

    private final DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;
    Set<String> visited = new HashSet<>();

    public SCC(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        this.graph = graph;
    }

    private class DFSStack extends YielderRecursionWithStacks<String, List<String>> {
        final Stack<String> stack;
        private final DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;
        public DFSStack(String initCall, Stack<String> stack, DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
            super(initCall);
            this.stack = stack;
            this.graph = graph;
        }
        @Override
        protected void afterRecursiveCall(String prev, Iterable<String> strings) {
            stack.push(prev);
        }
        @Override
        protected Iterable<String> beforeRecursiveCall(String curr) {
            visited.add(curr);
            ArrayList<String> al = new ArrayList<>(graph.outDegreeOf(curr));
            for (DependencyGraph.Edge dst : graph.outgoingEdgesOf(curr)) {
                if (!visited.contains(dst.dst))
                    al.add(dst.dst);

            }
            return al;
        }
    }

    private void dfsStack(String initCall, Stack<String> stack, DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        visited.add(initCall);
        ArrayList<String> al = new ArrayList<>(graph.outDegreeOf(initCall));
        for (DependencyGraph.Edge dst : graph.outgoingEdgesOf(initCall)) {
            if (!visited.contains(dst.dst))
                dfsStack(dst.dst, stack, graph);
        }
        stack.push(initCall);
    }

    private Map<String, Integer> cc(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> G, String[] order) {
        Map<String, Integer> id = new HashMap<>();
        int count = 0;

        for (int i = 0, n = G.vertexSet().size(); i<n; i++) {
            if (id.get(order[i]) == null) {
                count++;
                ccdfs(G, count, order[i], id);
            }
        }
        return id;
    }

    private void ccdfs(DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> g, int count, String order, Map<String, Integer> id) {
        id.put(order, count);
        for (DependencyGraph.Edge e : g.outgoingEdgesOf(order)) {
            if (id.get(e.dst) == null)
                ccdfs(g, count, e.dst, id);
        }
    }

    private List<String> rectifySCC(Collection<String> cycle, String v, List<String> al) {
        if (al.size() == cycle.size()) {
            assert (cycle.containsAll(al));
            return al;
        }
        Set<String> elements = new HashSet<>();
        graph.outgoingEdgesOf(v).forEach(x -> elements.add(x.dst));
        elements.retainAll(cycle);
        al.add(v);
        return rectifySCC(cycle, elements.iterator().next(), al);
    }

    private List<String> rectifySCC(Collection<String> cycle) {
        List<String> al = new ArrayList<>(cycle);
        return rectifySCC(cycle, cycle.iterator().next(), al);
    }

    public Stream<List<String>> runDFSStack() {
        Stack<String> stringstack = new Stack<>();
        for (String currentVertex : graph.vertexSet()) {
            dfsStack(currentVertex, stringstack, graph);
        }
        DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> T = new DefaultDirectedWeightedGraph<>(DependencyGraph.Edge.class);
        graph.vertexSet().forEach(T::addVertex);
        graph.edgeSet().forEach(x -> {
            DependencyGraph.Edge e = T.addEdge(x.dst, x.src);
            e.src = x.dst;
            e.dst = x.src;
        });

        String[] order = new String[T.vertexSet().size()];
        for (int i = 0, n = T.vertexSet().size(); i<n; i++)
            order[i] = stringstack.pop();

        Map<String, Integer> map = cc(T, order);
        HashMultimap<Integer, String> reverseMap = HashMultimap.create();
        map.forEach((k, v) -> reverseMap.put(v, k));
        return reverseMap.asMap().values().stream().map(this::rectifySCC);
    }

    public DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> getSubgraph(Collection<String> vertices) {
        DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> subgraph = new DefaultDirectedWeightedGraph<>(DependencyGraph.Edge.class);
        for (String x : vertices) {
            if (graph.containsVertex(x))
                subgraph.addVertex(x);
        }
        for (String x : vertices) {
            for (DependencyGraph.Edge e : graph.outgoingEdgesOf(x)) {
                if (vertices.contains(e.dst))
                {
                    DependencyGraph.Edge ep = subgraph.addEdge(x, e.dst);
                    ep.src = x;
                    ep.dst = e.dst;
                }
            }
        }
        return subgraph;
    }

}
