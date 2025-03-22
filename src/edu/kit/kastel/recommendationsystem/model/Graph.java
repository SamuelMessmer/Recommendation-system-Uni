package edu.kit.kastel.recommendationsystem.model;

import java.util.List;

public record Graph(
        List<Node> nodes,
        List<Edge> edges) {

    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
}
