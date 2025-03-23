package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.RelationshipDTO;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public final class DatabaseParser {

    private DatabaseParser() {
        // Utility class
    }

    public static Graph parse(List<String> lines) throws DataParsException {
        final Set<Node> nodes = new HashSet<>();
        final Set<Edge> edges = new HashSet<>();

        for (String line : lines) {
            RelationshipDTO relationship = LineParser.parse(line);

            ValidationUtils.validateDTO(relationship, nodes);
            processDTO(relationship, nodes, edges);
        }

        return new Graph(nodes, edges);
    }

    private static void processDTO(RelationshipDTO relationship, Set<Node> nodes, Set<Edge> edges) {
        final Node subject = getOrRegisterNode(relationship.subject(), nodes);
        final Node object = getOrRegisterNode(relationship.object(), nodes);

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
            validateNoSelfReference(relationship);
            RelationshipType.isAllowedBetween(relationship);
            validateExistingEdges(relationship, existingNodes);
        }

        private static void validateNoSelfReference(RelationshipDTO relationship) throws DataParsException {
            if (relationship.subject().equals(relationship.object())) {
                throw new DataParsException("Self-reference not allowed");
            }
        }

        private static void validateExistingEdges(RelationshipDTO relationship, Set<Node> nodes)
                throws DataParsException {
            if (edgeExists(relationship, nodes)) {
                throw new DataParsException("Duplicate edge detected");
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