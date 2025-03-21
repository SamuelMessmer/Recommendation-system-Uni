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
            public int compare(Edge firstEdge, Edge secondEdge) {
                int sourceCompare = compareNodes(firstEdge.getStartNode(), secondEdge.getStartNode());
                if (sourceCompare != 0) {
                    return sourceCompare;
                }

                int targetCompare = compareNodes(firstEdge.getEndNode(), secondEdge.getEndNode());
                if (targetCompare != 0) {
                    return targetCompare;
                }

                return Integer.compare(
                        PREDICATE_ORDER.indexOf(firstEdge.getRelationship()),
                        PREDICATE_ORDER.indexOf(secondEdge.getRelationship()));
            }

            private int compareNodes(Node firstNode, Node secondNode) {
                int nameCompare = firstNode.getName().compareToIgnoreCase(secondNode.getName());
                if (nameCompare != 0) {
                    return nameCompare;
                }

                if (firstNode.isProduct() && secondNode.isProduct()) {
                    return Integer.compare(
                            ((Product) firstNode).getId(),
                            ((Product) secondNode).getId());
                }
                return firstNode.isProduct() ? -1 : 1;
            }
        });
    }
}
