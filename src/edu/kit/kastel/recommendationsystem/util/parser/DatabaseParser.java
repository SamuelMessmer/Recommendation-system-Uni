package edu.kit.kastel.recommendationsystem.util.parser;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipDTO;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

/**
 * Utility class for parsing database input and constructing a graph
 * representation.
 * This class processes lines of input, validates relationships, and creates
 * nodes and edges.
 * 
 * @author urrwg
 */
public final class DatabaseParser {

    private static final String ERROR_SELF_REFERENCE = "self-reference not allowed";
    private static final String ERROR_DUPLICATE_EDGE = "duplicate edge detected";
    private static final String ERROR_DUPLICATE_PRODUCT_ID = "duplicate product ID detected: %d";
    private static final String ERROR_DUPLICATE_NODE_NAME = "duplicate node name detected: %s";
    private static final String ERROR_INVALID_RELATIONSHIP = "the relationship: %s, is not aplicable for the given node types";

    private DatabaseParser() {
        // This is a utitlity class
    }

    /**
     * Parses a list of input lines and constructs a graph representation.
     * 
     * @param lines the list of input lines representing relationships
     * @return the constructed graph
     * @throws DataParsException if parsing or validation fails
     */
    public static Graph parse(List<String> lines) throws DataParsException {
        Set<Node> nodes = new HashSet<>();
        Set<Edge> edges = new HashSet<>();

        for (String line : lines) {
            RelationshipDTO relationship = LineParser.parse(line);

            ValidationUtils.validateDTO(relationship, nodes);
            processDTO(relationship, nodes, edges);
        }

        return new Graph(nodes, edges);
    }

    private static void processDTO(RelationshipDTO relationship, Set<Node> nodes, Set<Edge> edges) {
        Node subject = getOrRegisterNode(relationship.subject(), nodes);
        Node object = getOrRegisterNode(relationship.object(), nodes);
        createEdgeIfAbsent(subject, object, relationship.predicate(), edges);
    }

    private static Node getOrRegisterNode(Node node, Set<Node> nodes) {
        for (Node existingNode : nodes) {
            if (existingNode.equals(node)) {
                return existingNode;
            }
        }
        nodes.add(node);
        return node;
    }

    private static void createEdgeIfAbsent(Node from, Node to, RelationshipType type, Set<Edge> edges) {
        Edge edge = new Edge(from, to, type);
        if (!edges.contains(edge)) {
            edges.add(edge);
            from.addEdge(edge);
        }

        Edge reverseEdge = new Edge(to, from, type.getReverse());
        if (!edges.contains(reverseEdge)) {
            edges.add(reverseEdge);
            to.addEdge(reverseEdge);
        }
    }

    /**
     * Validation util class, solely used to validate the Input retrieved from the
     * {@link LineParser} to prevent semantic errors.
     */
    private final class ValidationUtils {

        private static void validateDTO(RelationshipDTO relationship, Set<Node> existingNodes)
                throws DataParsException {
            validateRelationship(relationship);
            validateNoSelfReference(relationship);
            validateUniqueIdentifiers(relationship, existingNodes);
            validateEdgeUniqueness(relationship, existingNodes);
        }

        private static void validateRelationship(RelationshipDTO relationship) throws DataParsException {
            if (!RelationshipType.isAllowedBetween(relationship)) {
                throw new DataParsException(String.format(ERROR_INVALID_RELATIONSHIP, relationship.predicate()));
            }
        }

        private static void validateNoSelfReference(RelationshipDTO relationship) throws DataParsException {
            if (relationship.subject().equals(relationship.object())) {
                throw new DataParsException(ERROR_SELF_REFERENCE);
            }
        }

        private static void validateUniqueIdentifiers(RelationshipDTO relationship, Set<Node> nodes)
                throws DataParsException {
            for (Node existingNode : nodes) {
                checkProductIdUniqueness(relationship.subject(), existingNode);
                checkProductIdUniqueness(relationship.object(), existingNode);
                checkNodeNameUniqueness(relationship.subject(), existingNode);
                checkNodeNameUniqueness(relationship.object(), existingNode);
            }
        }

        private static void checkProductIdUniqueness(Node currentNode, Node existingNode) throws DataParsException {
            if (currentNode instanceof Product && existingNode instanceof Product) {
                Product currentProduct = (Product) currentNode;
                Product existingProduct = (Product) existingNode;

                if (currentProduct.getId() == existingProduct.getId() && !currentNode.equals(existingNode)) {
                    throw new DataParsException(String.format(ERROR_DUPLICATE_PRODUCT_ID, currentProduct.getId()));
                }
            }
        }

        private static void checkNodeNameUniqueness(Node currentNode, Node existingNode) throws DataParsException {
            if (currentNode.getName().equalsIgnoreCase(existingNode.getName())
                    && !currentNode.equals(existingNode)) {
                throw new DataParsException(String.format(ERROR_DUPLICATE_NODE_NAME, existingNode.getName()));
            }
        }

        private static void validateEdgeUniqueness(RelationshipDTO relationship, Set<Node> nodes)
                throws DataParsException {
            if (edgeExists(relationship, nodes)) {
                throw new DataParsException(ERROR_DUPLICATE_EDGE);
            }
        }

        private static boolean edgeExists(RelationshipDTO relationship, Set<Node> nodes) {
            for (Node node : nodes) {
                for (Edge edge : node.getEdges()) {
                    if (edge.equals(
                            new Edge(relationship.subject(), relationship.object(), relationship.predicate()))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}