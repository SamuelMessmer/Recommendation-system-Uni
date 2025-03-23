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

    public boolean removeEdge(RelationshipDTO relationship) {
        if (relationship == null || !this.edges.contains(relationship.edge()) || !this.edges.contains(relationship.reverseEdge())) {
            return false;
        }

        edges.remove(relationship.edge());
        edges.remove(relationship.reverseEdge());
        cleanupOrphanedNodes(relationship.subject(), relationship.object(), relationship.edge(), relationship.reverseEdge());
        return true;
    }

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

    private boolean canAddRelationship(RelationshipDTO relationship) {
        return !this.edges.contains(relationship.edge())
                && this.nodes.contains(relationship.subject())
                && this.nodes.contains(relationship.object())
                && RelationshipType.isAllowedBetween(relationship);
    }

    private void cleanupOrphanedNodes(Node firstNode, Node secondNode, Edge removedEdge, Edge reversedEdge) {
        firstNode.removeEdge(removedEdge);
        firstNode.removeEdge(reversedEdge);
        if (firstNode.getEdges().isEmpty()) {
            this.nodes.remove(firstNode);
        }

        secondNode.removeEdge(removedEdge);
        secondNode.removeEdge(reversedEdge);
        if (secondNode.getEdges().isEmpty()) {
            this.nodes.remove(secondNode);
        }
    }

    public Node findProductById(int productId) {
        for (Node node : this.nodes) {
            if (node instanceof Product && ((Product) node).getId() == productId) {
                return node;
            }
        }
        return null;
    }
}
