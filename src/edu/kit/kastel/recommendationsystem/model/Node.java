package edu.kit.kastel.recommendationsystem.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Collections;

/**
 * Represents a node in the recommendation system graph.
 * A node can be either a product or a category.
 * 
 * @author urrwg
 */
public abstract class Node {

    protected final String name;
    protected final NodeType type;

    private Set<Edge> edges;

    /**
     * Constructs a new node with the specified name and type.
     *
     * @param name the name of the node
     * @param type the type of the node (product or category)
     */
    public Node(String name, NodeType type) {
        this.name = name.toLowerCase();
        this.type = type;
        this.edges = new HashSet<>();
    }

    /**
     * Returns the name of the node.
     *
     * @return the name of the node
     */
    public String getName() {
        return this.name;
    }

    /**
     * Checks if the node is of the specified type.
     *
     * @param type the type to check
     * @return {@code true} if the node is of the specified type, {@code false}
     *         otherwise
     */
    public boolean isOfType(NodeType type) {
        return this.type.equals(type);
    }

    /**
     * Returns an unmodifiable view of the edges connected to this node.
     *
     * @return the set of edges
     */
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    /**
     * Adds a new edge to this node.
     *
     * @param newEdge the edge to add
     */
    public void addEdge(Edge newEdge) {
        this.edges.add(newEdge);
    }

    /**
     * Removes an edge from this node.
     *
     * @param removedEdge the edge to remove
     */
    public void removeEdge(Edge removedEdge) {
        this.edges.remove(removedEdge);
    }

    /**
     * necessary to avoid duplication in the set of nodes used in the graph class.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Node node)) {
            return false;
        }
        return name.equals(node.name) && this.type.equals(node.type);
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