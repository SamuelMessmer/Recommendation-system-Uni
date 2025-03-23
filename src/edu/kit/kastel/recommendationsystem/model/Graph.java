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

    public boolean removeEdge(DTO dto) {
        if (dto == null || !this.edges.contains(dto.edge()) || !this.edges.contains(dto.reverseEdge())) {
            return false;
        }

        edges.remove(dto.edge());
        edges.remove(dto.reverseEdge());
        cleanupOrphanedNodes(dto.subject(), dto.object(), dto.edge(), dto.reverseEdge());
        return true;
    }

    public boolean addRelationship(DTO dto) {
        if (dto == null || !canAddRelationship(dto)) {
            return false;
        }

        this.edges.add(dto.edge());
        this.edges.add(dto.reverseEdge());
        dto.subject().addEdge(dto.edge());
        dto.object().addEdge(dto.reverseEdge());
        return true;
    }

    private boolean canAddRelationship(DTO dto) {
        return !this.edges.contains(dto.edge())
                && this.nodes.contains(dto.subject())
                && this.nodes.contains(dto.object())
                && RelationshipType.isAllowedBetween(dto);
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
