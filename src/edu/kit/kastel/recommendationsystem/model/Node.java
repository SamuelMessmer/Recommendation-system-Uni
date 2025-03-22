package edu.kit.kastel.recommendationsystem.model;

import java.util.List;
import java.util.Objects;

public abstract class Node {

    protected final String name;
    protected final NodeType type;

    private List<Edge> edges;

    public Node(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }

    public Node(String name, NodeType type, List<Edge> edges) {
        this.name = name;
        this.type = type;
        this.edges = edges;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOfType(NodeType type) {
        return this.type == type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node node)) {
            return false;
        }
        return name.equals(node.name) && type == node.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
