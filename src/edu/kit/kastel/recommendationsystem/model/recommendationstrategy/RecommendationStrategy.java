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

public class RecommendationStrategy {

    public static Set<Node> findSiblingProducts(Node node, Graph graph) {
        Set<Node> siblings = new HashSet<>();

        // 1. Find all categories containing the product
        Set<Node> categories = new HashSet<>();
        for (Edge e : node.getEdges()) {
            if (e.getRelationship() == RelationshipType.CONTAINED_IN) {
                categories.add(e.getEndNode());
            }
        }

        // 2. Find all products in these categories
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

        // 3. Remove reference product and return
        siblings.remove(node);
        return Collections.unmodifiableSet(siblings);
    }

    public static Set<Node> findSuccessorProducts(Node node, Graph graph) {
        return traverseRelationship(node, graph, RelationshipType.PREDECESSOR_OF);
    }

    public static Set<Node> findPredecessorProducts(Node node, Graph graph) {
        return traverseRelationship(node, graph, RelationshipType.SUCCESSOR_OF);
    }

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
