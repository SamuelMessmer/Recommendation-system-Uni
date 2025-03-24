package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.List;
import java.util.ArrayList;
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

    private static final String ERROR_INVALID_RELATIONSHIP_PARTNER = "Self-reference not allowed";
    private static final String ERROR_ALREADY_EXISTING_EDGE = "Duplicate edge detected";

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
    public static ParseResult parse(List<String> lines) throws DataParsException {
        Set<Node> nodes = new HashSet<>();
        Set<Edge> edges = new HashSet<>();
        List<String> processedLines = new ArrayList<>();

        for (String line : lines) {
            RelationshipDTO relationship = LineParser.parse(line);

            ValidationUtils.validateDTO(relationship, nodes);
            processDTO(relationship, nodes, edges);
            processedLines.add(line);
        }

        return new ParseResult(new Graph(nodes, edges), processedLines);
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

    private final class ValidationUtils {

        private static void validateDTO(RelationshipDTO relationship, Set<Node> existingNodes)
                throws DataParsException {
            try {
                validateNoSelfReference(relationship);
                validateUniqueNode(relationship, existingNodes);
                RelationshipType.isAllowedBetween(relationship);
                validateExistingEdges(relationship, existingNodes);
            } catch (DataParsException exception) {
                throw new DataParsException(exception.getMessage());
            }
        }

        private static void validateNoSelfReference(RelationshipDTO relationship) throws DataParsException {
            if (relationship.subject().equals(relationship.object())) {
                throw new DataParsException(ERROR_INVALID_RELATIONSHIP_PARTNER);
            }
        }

        private static void validateUniqueNode(RelationshipDTO relationship, Set<Node> existingNodes)
                throws DataParsException {
            for (Node existingNode : existingNodes) {
                if (relationship.subject() instanceof Product && existingNode instanceof Product
                        && ((Product) relationship.subject()).getId() == ((Product) existingNode).getId()
                        && !relationship.subject().equals(existingNode)) {
                    throw new DataParsException("detacted several products with the same id.");
                }
                if (relationship.object() instanceof Product && existingNode instanceof Product
                        && ((Product) relationship.object()).getId() == ((Product) existingNode).getId()
                        && !relationship.object().equals(existingNode)) {
                    throw new DataParsException("detacted several products with the same id.");
                }
            }
        }

        private static void validateExistingEdges(RelationshipDTO relationship, Set<Node> nodes)
                throws DataParsException {
            if (edgeExists(relationship, nodes)) {
                throw new DataParsException(ERROR_ALREADY_EXISTING_EDGE);
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