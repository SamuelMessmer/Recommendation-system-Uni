package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public class DatabaseParser {

    private static final List<RelationshipType> RELATIONSHIPS_ONLY_FOR_PRODUCTS = List.of(
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);

    private DatabaseParser() {
        // Utility class
    }

    public static Graph parse(List<String> lines) throws DataParsException {
        Set<Node> nodes = new HashSet<>();
        Set<Edge> edges = new HashSet<>();

        for (String line : lines) {
            DTO dto = LineParser.parse(line);

            validateSemantics(dto, nodes, edges);

            Node subject = addNode(dto.subject(), nodes);
            Node object = addNode(dto.object(), nodes);
            addEdge(subject, object, dto.predicate(), edges);
        }

        return new Graph(nodes, edges);
    }

    private static boolean validateSemantics(DTO dto, Set<Node> nodes, Set<Edge> edges) {
        if (dto.subject() == dto.object()) {
            return false;
        }

        if (dto.subject().isOfType(NodeType.CATEGORY) && dto.object().isOfType(NodeType.CATEGORY)
                && RELATIONSHIPS_ONLY_FOR_PRODUCTS.contains(dto.predicate())) {
            return false;
        }

        if (edges.contains(new Edge(dto.subject(), dto.object(), dto.predicate()))) {
            return false;
        }

        return true;
    }

    private static Node addNode(Node newNode, Set<Node> nodes) {
        if (!nodes.contains(newNode)) {
            nodes.add(newNode);
            return newNode;
        } else {
            for (Node node : nodes) {
                if (newNode.equals(node)) {
                    return node;
                }
            }
        }
        return null;
    }

    private static void addEdge(Node subject, Node object, RelationshipType predicate, Set<Edge> edges) {
        Edge newEdge = new Edge(subject, object, predicate);
        edges.add(newEdge);
        subject.addEdge(newEdge);

        Edge secondNewEdge = new Edge(object, subject, predicate.getReverse());
        edges.add(secondNewEdge);
        object.addEdge(secondNewEdge);
    }
}
