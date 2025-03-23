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
        Edge removedEdge = new Edge(dto.subject(), dto.object(), dto.predicate());

        if (!this.edges.contains(removedEdge)) {
            return false;
        }

        edges.remove(removedEdge);
        updateNodes(dto.subject(), dto.object(), removedEdge);

        return true;
    }

    public boolean addRelationship(DTO dto) {
        Edge newEdge = new Edge(dto.subject(), dto.object(), dto.predicate());

        if (this.edges.contains(newEdge) || !this.nodes.contains(dto.subject()) || !this.nodes.contains(dto.object())) {
            return false;
        }

        this.edges.add(newEdge);
        this.edges.add(new Edge(dto.object(), dto.subject(), dto.predicate().getReverse()));
        this.nodes.add(dto.object());
        this.nodes.add(dto.subject());
        return true;
    }

    private void updateNodes(Node firstNode, Node secondNode, Edge removedEdge) {
        firstNode.removeEdge(removedEdge);
        secondNode.removeEdge(removedEdge);

        Edge reversedEdge = new Edge(secondNode, firstNode, removedEdge.getRelationship().getReverse());
        firstNode.removeEdge(reversedEdge);
        secondNode.removeEdge(reversedEdge);

        if (firstNode.getEdges().isEmpty()) {
            this.nodes.remove(firstNode);
        }

        if (secondNode.getEdges().isEmpty()) {
            this.nodes.remove(secondNode);
        }
    }
}
