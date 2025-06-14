package edu.kit.kastel.recommendationsystem.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Represents a graph structure for the recommendation system.
 * The graph consists of nodes (products and categories) and edges
 * (relationships between nodes).
 * I have chosen a Set with a polymorphic design to follow the SOLID criteria
 * and provide maximum expandability, even though Maps could theoretically be
 * more performant (with larger graphs).
 * 
 * @author urrwg
 */
public class Graph {

    private final Set<Node> nodes;
    private final Set<Edge> edges;

    /**
     * Constructs a new graph with the specified nodes and edges.
     *
     * @param nodes the set of nodes in the graph
     * @param edges the set of edges in the graph
     */
    public Graph(Set<Node> nodes, Set<Edge> edges) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
    }

    /**
     * Returns an unmodifiable view of the edges in the graph.
     *
     * @return the set of edges
     */
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    /**
     * Returns an unmodifiable view of the nodes in the graph.
     *
     * @return the set of nodes
     */
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(this.nodes);
    }

    /**
     * Finds a product node by its unique identification number (id).
     *
     * @param productId the id of the product to find
     * @return the product node, or {@code null} if no such node exists
     */
    public Node findProductById(int productId) {
        for (Node node : this.nodes) {
            if (node.isOfType(NodeType.PRODUCT) && ((Product) node).getId() == productId) {
                return node;
            }
        }
        return null;
    }

    /**
     * Adds a new Node to the graph.
     * 
     * @param newNode the node to be added
     */
    public void addNode(Node newNode) {
        this.nodes.add(newNode);
    }

    /**
     * Removes an edge (and its reverse) from the graph.
     *
     * @param relationship the relationship descriptor representing the edge to
     *                     remove
     * @return {@code true} if the edge was removed successfully, {@code false}
     *         otherwise
     */
    public boolean removeEdge(RelationshipDTO relationship) {
        if (relationship == null || !edgeIsPresent(relationship)) {
            return false;
        }

        this.edges.remove(relationship.edge());
        this.edges.remove(relationship.reverseEdge());
        cleanupNodes(relationship);
        return true;
    }

    /**
     * Adds a new relationship (and its reverse) to the graph.
     *
     * @param relationship the relationship data transfer object representing the
     *                     edge to add
     * @return {@code true} if the relationship was added successfully,
     *         {@code false} otherwise
     */
    public boolean addRelationship(RelationshipDTO relationship) {
        if (relationship == null || !canAddRelationship(relationship)) {
            return false;
        }

        this.edges.add(relationship.edge());
        this.edges.add(relationship.reverseEdge());
        relationship.subject().addEdge(relationship.edge());
        relationship.object().addEdge(relationship.reverseEdge());
        return true;
    }

    private boolean edgeIsPresent(RelationshipDTO relationship) {
        return this.edges.contains(relationship.edge())
                || this.edges.contains(relationship.reverseEdge());
    }

    private void cleanupNodes(RelationshipDTO relationship) {
        Node subjectNode = relationship.subject();
        Node objectNode = relationship.object();

        cleanupNode(subjectNode, relationship.edge(), relationship.reverseEdge());
        cleanupNode(objectNode, relationship.edge(), relationship.reverseEdge());
    }

    private void cleanupNode(Node node, Edge primaryEdge, Edge reversedEdge) {
        node.removeEdge(primaryEdge);
        node.removeEdge(reversedEdge);

        if (node.getEdges().isEmpty()) {
            this.nodes.remove(node);
        }
    }

    private boolean canAddRelationship(RelationshipDTO relationship) {
        return this.nodes.contains(relationship.subject())
                && this.nodes.contains(relationship.object())
                && RelationshipType.isAllowedRelationship(relationship);
    }
}