package dev.caseresearch.caselayer.graph;

import dev.caseresearch.caselayer.model.Evidence;

import java.util.ArrayList;
import java.util.List;

import dev.caseresearch.flags.Tri;

/**
 * Small, time-scoped evidence graph (paper Section III, Step 1) for one (deployment, t). Nodes are named strings
 * (artifact:x, impl:x, flag:x, deployment:x). Edge types: IMPLEMENTS, GUARDS, SUPERSEDES, DEPLOYED_IN.
 * Only evidence timestamped at or before t is ever added.
 */
public class EvidenceGraph {

    public record Edge(String from, String type, String to, Tri state, Evidence evidence) {
    }

    private final List<Edge> edges = new ArrayList<>();

    public void add(Edge e) {
        edges.add(e);
    }

    public List<Edge> edgesTo(String node, String type) {
        return edges.stream().filter(e -> e.to().equals(node) && e.type().equals(type)).toList();
    }

    public List<Edge> edgesFrom(String node, String type) {
        return edges.stream().filter(e -> e.from().equals(node) && e.type().equals(type)).toList();
    }

    public List<Edge> edges() {
        return List.copyOf(edges);
    }
}
