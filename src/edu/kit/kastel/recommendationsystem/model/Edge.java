package edu.kit.kastel.recommendationsystem.model;

import java.util.Objects;

public class Edge {

    private final Node startNode;
    private final Node endNode;
    private final RelationshipType relationship;

    public Edge(Node startNode, Node endNode, RelationshipType relationship) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.relationship = relationship;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public RelationshipType getRelationship() {
        return relationship;
    }

    @Override
    public String toString() {
        return startNode.toString() + "-[" + relationship.toString() + "]->" + endNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        Edge edge = (Edge) o;
        return this.startNode.equals(edge.startNode) && this.relationship == edge.relationship
                && this.endNode.equals(edge.endNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.startNode, this.relationship, this.endNode);
    }
}
