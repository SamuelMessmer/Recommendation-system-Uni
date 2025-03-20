package edu.kit.kastel.recommendationsystem.model;

import java.util.List;

public record Graph(
        List<Node> nodes,
        List<Edge> edges) {

    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public void addEdge(Edge newEdge) {
        this.edges.add(newEdge);
    }

    public void addNode(Node newNode) {
        this.nodes.add(newNode);
    }

    public boolean removeEdge(Edge edgeToRemove) {
        return true;
    }
}
