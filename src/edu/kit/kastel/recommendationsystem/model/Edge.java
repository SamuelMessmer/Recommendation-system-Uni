package edu.kit.kastel.recommendationsystem.model;

public class Edge {

    private final Node startNode;
    private final Node endNode;
    private final Relationship relationship;

    public Edge(Node startNode, Node endNode, Relationship relationship) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.relationship = relationship;
    }
}
