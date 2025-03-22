package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public class DatabaseParser {

    private DatabaseParser() {
        // Utility class
    }

    public static Graph parse(List<String> lines) throws DataParsException {
        Set<Node> nodes = new HashSet<>();
        Set<Edge> edges = new HashSet<>();

        for (String line : lines) {
            DTO dto = LineParser.parse(line);

            validateSemantics(dto);

            nodes.add(dto.subject());
            nodes.add(dto.object());
            addEdge(dto, edges);
        }

        return new Graph(nodes, edges);
    }

    private static boolean validateSemantics(DTO dto) {
        if (dto.subject() == dto.object()) {
            return false;
        }

        if (dto.subject().isOfType(NodeType.CATEGORY) && dto.object().isOfType(NodeType.CATEGORY)
                && RELATIONSHIPS_ONLY_FOR_PRODUCTS.contains(dto.predicate())) {
            return false;
        }

        return true;
    }

    private static void addEdge(DTO dto, Set<Edge> edges) {
        edges.add(new Edge(dto.subject(), dto.object(), dto.predicate()));
        edges.add(new Edge(dto.object(), dto.subject(), dto.predicate().getReverse()));
    }

    private static final List<RelationshipType> RELATIONSHIPS_ONLY_FOR_PRODUCTS = List.of(
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);
}
