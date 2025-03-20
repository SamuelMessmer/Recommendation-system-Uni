package edu.kit.kastel.recommendationsystem.model;

import java.util.Map;

public record Graph(
        Map<String, Node> nodes,
        Map<String, Edge> edges) {

    public Graph(Map<String, Node> nodes, Map<String, Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public void addEdge(Edge newEdge) {
        this.edges.put(null, newEdge);
    }

    public void addNode(Node newNode) {
        this.nodes.put(null, newNode);
    }
}
