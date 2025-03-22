package edu.kit.kastel.recommendationsystem.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Graph {

    private final Set<Node> nodes;
    private final Set<Edge> edges;

    public Graph(Set<Node> nodes, Set<Edge> edges) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
    }

    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(this.nodes);
    }

    public boolean addEdge(Edge newEdge) {

        return true;
    }

    public boolean addNodes(Node newNode, Node secondNode) {
        if (!nodes.contains(newNode)) {
            nodes.add(newNode);
            return true;
        }
        return true;
    }

    public boolean removeEdge() {

        return true;
    }
}
