package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public final class DatabaseParser {
    private static final List<RelationshipType> PRODUCT_ONLY_RELATIONSHIPS = List.of(
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);

    private DatabaseParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Graph parse(List<String> lines) throws DataParsException {
        final Set<Node> nodes = new HashSet<>();
        final Set<Edge> edges = new HashSet<>();

        for (String line : lines) {
            final DTO dto = LineParser.parse(line);

            ValidationUtils.validateDTO(dto, nodes);
            processDTO(dto, nodes, edges);
        }

        return new Graph(nodes, edges);
    }

    private static void processDTO(DTO dto, Set<Node> nodes, Set<Edge> edges) {
        final Node subject = getOrRegisterNode(dto.subject(), nodes);
        final Node object = getOrRegisterNode(dto.object(), nodes);

        createEdgeIfAbsent(subject, object, dto.predicate(), edges);
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
        final Edge edge = new Edge(from, to, type);
        if (!edges.contains(edge)) {
            edges.add(edge);
            from.addEdge(edge);
        }

        final Edge reverseEdge = new Edge(to, from, type.getReverse());
        if (!edges.contains(reverseEdge)) {
            edges.add(reverseEdge);
            from.addEdge(reverseEdge);
        }
    }

    private final class ValidationUtils {

        private static void validateDTO(DTO dto, Set<Node> existingNodes) throws DataParsException {
            validateNoSelfReference(dto);
            validateNodeTypes(dto);
            validateExistingEdges(dto, existingNodes);
        }

        private static void validateNoSelfReference(DTO dto) throws DataParsException {
            if (dto.subject().equals(dto.object())) {
                throw new DataParsException("Self-reference not allowed");
            }
        }

        private static void validateNodeTypes(DTO dto) throws DataParsException {
            if (bothAreCategories(dto) && hasProductOnlyRelationship(dto)) {
                throw new DataParsException("Invalid relationship between categories");
            }
        }

        private static boolean bothAreCategories(DTO dto) {
            return dto.subject().isOfType(NodeType.CATEGORY)
                    && dto.object().isOfType(NodeType.CATEGORY);
        }

        private static boolean hasProductOnlyRelationship(DTO dto) {
            return PRODUCT_ONLY_RELATIONSHIPS.contains(dto.predicate());
        }

        private static void validateExistingEdges(DTO dto, Set<Node> nodes) throws DataParsException {
            if (edgeExists(dto, nodes)) {
                throw new DataParsException("Duplicate edge detected");
            }
        }

        private static boolean edgeExists(DTO dto, Set<Node> nodes) {
            for (Node node : nodes) {
                for (Edge edge : node.getEdges()) {
                    if (edge.equals(new Edge(dto.subject(), dto.object(), dto.predicate()))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}