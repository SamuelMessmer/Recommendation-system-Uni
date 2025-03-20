package edu.kit.kastel.recommendationsystem.model;

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

    public String toString() {
        return startNode.toString() + "-[" + relationship.toString() + "]->" + endNode;
    }
}
