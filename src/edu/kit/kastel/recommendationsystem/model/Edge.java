package edu.kit.kastel.recommendationsystem.model;

import java.util.Objects;

/**
 * Represents a directed edge between two nodes in the recommendation system
 * graph.
 * An edge connects a start node to an end node with a specific relationship
 * type.
 * 
 * @author urrwg
 */
public class Edge {

    private static final String EDGE_OUTPUT_FORMAT = "%s-[%s]->%s";

    private final Node startNode;
    private final Node endNode;
    private final RelationshipType type;

    /**
     * Constructs a new edge with the specified start node, end node, and
     * relationship type.
     *
     * @param startNode the starting node of the edge
     * @param endNode   the ending node of the edge
     * @param type      the type of relationship between the nodes
     */
    public Edge(Node startNode, Node endNode, RelationshipType type) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.type = type;
    }

    /**
     * Returns the start node of the edge.
     *
     * @return the start node
     */
    public Node getStartNode() {
        return this.startNode;
    }

    /**
     * Returns the end node of the edge.
     *
     * @return the end node
     */
    public Node getEndNode() {
        return this.endNode;
    }

    /**
     * Returns the relationship type of the edge.
     *
     * @return the relationship type
     */
    public RelationshipType getRelationship() {
        return this.type;
    }

    @Override
    public String toString() {
        return String.format(EDGE_OUTPUT_FORMAT, startNode.toString(), type.toString(), endNode.toString());
    }

    /**
     * necessary to avoid duplication in the set of edges used in the graph class.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        Edge edge = (Edge) object;
        return this.startNode.equals(edge.startNode) && this.type == edge.type
                && this.endNode.equals(edge.endNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.startNode, this.type, this.endNode);
    }
}
