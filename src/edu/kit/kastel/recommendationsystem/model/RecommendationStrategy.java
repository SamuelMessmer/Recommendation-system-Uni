package edu.kit.kastel.recommendationsystem.model;

import java.util.Set;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Provides recommendation strategies for finding related products in a graph.
 * This class implements three strategies: finding sibling products, successor
 * products, and predecessor products.
 * 
 * @author urrwg
 */
public final class RecommendationStrategy {

    private RecommendationStrategy() {
        // This is a utility class
    }

    /**
     * Finds all sibling products of the given node.
     * Sibling products are products that share at least one common category with
     * the reference product.
     *
     * @param node  the reference product node
     * @param graph the graph containing the nodes and edges
     * @return a set of sibling products, excluding the reference product
     */
    public static Set<Node> findSiblingProducts(Node node, Graph graph) {
        Set<Node> siblings = new HashSet<>();

        Set<Node> categories = new HashSet<>();
        for (Edge edge : node.getEdges()) {
            if (edge.getRelationship() == RelationshipType.CONTAINED_IN) {
                categories.add(edge.getEndNode());
            }
        }

        for (Node category : categories) {
            for (Edge edge : graph.getEdges()) {
                if (edge.getRelationship() == RelationshipType.CONTAINS && edge.getStartNode().equals(category)) {
                    Node endNode = edge.getEndNode();
                    if (endNode instanceof Product) {
                        siblings.add(endNode);
                    }
                }
            }
        }

        siblings.remove(node);
        return Collections.unmodifiableSet(siblings);
    }

    /**
     * Finds all successor products of the given node.
     * Successor products are products that are directly or indirectly connected via
     * the PREDECESSOR_OF relationship.
     *
     * @param node  the reference product node
     * @param graph the graph containing the nodes and edges
     * @return a set of successor products
     */
    public static Set<Node> findSuccessorProducts(Node node, Graph graph) {
        return traverseRelationship(node, RelationshipType.PREDECESSOR_OF);
    }

    /**
     * Finds all predecessor products of the given node.
     * Predecessor products are products that are directly or indirectly connected
     * via the SUCCESSOR_OF relationship.
     *
     * @param node  the reference product node
     * @param graph the graph containing the nodes and edges
     * @return a set of predecessor products
     */
    public static Set<Node> findPredecessorProducts(Node node, Graph graph) {
        return traverseRelationship(node, RelationshipType.SUCCESSOR_OF);
    }

    /**
     * Traverses the graph to find products connected to the start node via the
     * specified relationship.Uses a breadth-first search (BFS) to explore the
     * graph.
     *
     * @param startNode    the starting node for the traversal
     * @param graph        the graph containing the nodes and edges
     * @param relationship the relationship type to traverse
     * @return a set of products connected to the startNode node via the specified
     *         relationship
     */
    private static Set<Node> traverseRelationship(Node startNode, RelationshipType relationship) {
        Set<Node> result = new HashSet<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            for (Edge edge : currentNode.getEdges()) {
                if (edge.getRelationship() == relationship) {
                    Node neighbor = edge.getEndNode();
                    if (neighbor instanceof Product && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                        result.add(neighbor);
                    }
                }
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
