package edu.kit.kastel.recommendationsystem.model.recommendationstrategy;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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
        for (Edge e : node.getEdges()) {
            if (e.getRelationship() == RelationshipType.CONTAINED_IN) {
                categories.add(e.getEndNode());
            }
        }

        for (Node category : categories) {
            for (Edge e : graph.getEdges()) {
                if (e.getRelationship() == RelationshipType.CONTAINS && e.getStartNode().equals(category)) {
                    Node endNode = e.getEndNode();
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
        return traverseRelationship(node, graph, RelationshipType.PREDECESSOR_OF);
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
        return traverseRelationship(node, graph, RelationshipType.SUCCESSOR_OF);
    }

    /**
     * Traverses the graph to find products connected to the start node via the
     * specified relationship.Uses a breadth-first search (BFS) to explore the
     * graph.
     *
     * @param start        the starting node for the traversal
     * @param graph        the graph containing the nodes and edges
     * @param relationship the relationship type to traverse
     * @return a set of products connected to the start node via the specified
     *         relationship
     */
    private static Set<Node> traverseRelationship(Node start, Graph graph, RelationshipType relationship) {
        Set<Node> result = new HashSet<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Edge e : current.getEdges()) {
                if (e.getRelationship() == relationship) {
                    Node neighbor = e.getEndNode();
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
