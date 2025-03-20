package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandEdges implements Command<Graph> {

    // Reihenfolge der Prädikate gemäß Aufgabenstellung
    private static final List<RelationshipType> PREDICATE_ORDER = List.of(
            RelationshipType.CONTAINS,
            RelationshipType.CONTAINED_IN,
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.edges());

        edges.sort(new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                // 1. Vergleich nach Quellknoten
                int sourceCompare = compareNodes(e1.getStartNode(), e2.getStartNode());
                if (sourceCompare != 0) {
                    return sourceCompare;
                }

                // 2. Vergleich nach Zielknoten
                int targetCompare = compareNodes(e1.getEndNode(), e2.getEndNode());
                if (targetCompare != 0) {
                    return targetCompare;
                }

                // 3. Vergleich nach Prädikat-Reihenfolge
                return Integer.compare(
                        PREDICATE_ORDER.indexOf(e1.getRelationship()),
                        PREDICATE_ORDER.indexOf(e2.getRelationship()));
            }

            private int compareNodes(Node n1, Node n2) {
                // Vergleich nach Name dann ID wie oben
                int nameCompare = n1.getName().compareToIgnoreCase(n2.getName());
                if (nameCompare != 0) {
                    return nameCompare;
                }

                if (n1.isProduct() && n2.isProduct()) {
                    return Integer.compare(
                            ((Product) n1).getId(),
                            ((Product) n2).getId());
                }
                return n1.isProduct() ? -1 : 1;
            }
        });

        StringBuilder sb = new StringBuilder();
        for (Edge edge : edges) {
            sb.append(edge.toString()).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}