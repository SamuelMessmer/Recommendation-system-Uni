package edu.kit.kastel.recommendationsystem.util;

import java.util.List;
import java.util.Comparator;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public final class SortEdges {

    private static final List<RelationshipType> PREDICATE_ORDER = List.of(
            RelationshipType.CONTAINS,
            RelationshipType.CONTAINED_IN,
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);

    public static void sort(List<Edge> edges) {
        edges.sort(new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                int sourceCompare = compareNodes(e1.getStartNode(), e2.getStartNode());
                if (sourceCompare != 0) {
                    return sourceCompare;
                }

                int targetCompare = compareNodes(e1.getEndNode(), e2.getEndNode());
                if (targetCompare != 0) {
                    return targetCompare;
                }

                return Integer.compare(
                        PREDICATE_ORDER.indexOf(e1.getRelationship()),
                        PREDICATE_ORDER.indexOf(e2.getRelationship()));
            }

            private int compareNodes(Node n1, Node n2) {
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
    }
}
