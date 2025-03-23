package edu.kit.kastel.recommendationsystem.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Node {

    protected final String name;
    protected final NodeType type;

    private Set<Edge> edges;

    public Node(String name, NodeType type) {
        this.name = name;
        this.type = type;
        this.edges = new HashSet<>();
    }

    public String getName() {
        return this.name;
    }

    public boolean isOfType(NodeType type) {
        return this.type == type;
    }

    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    public void addEdge(Edge newEdge) {
        this.edges.add(newEdge);
    }

    public void removeEdge(Edge removedEdge) {
        this.edges.remove(removedEdge);
    } 

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node node)) {
            return false;
        }
        return name.equals(node.name) && this.type == node.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
