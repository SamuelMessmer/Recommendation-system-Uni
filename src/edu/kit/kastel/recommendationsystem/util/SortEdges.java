package edu.kit.kastel.recommendationsystem.util;

import java.util.List;
import java.util.Comparator;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public final class SortEdges {

    private static final List<RelationshipType> RELATIONSHIP_ORDER = List.of(
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
                // Compare start nodes using compareNodes (name, type, ID)
                int sourceCompare = compareNodes(firstEdge.getStartNode(), secondEdge.getStartNode());
                if (sourceCompare != 0) {
                    return sourceCompare;
                }

                // Compare end nodes strictly by name (case-insensitive)
                int targetCompare = firstEdge.getEndNode().getName()
                        .compareToIgnoreCase(secondEdge.getEndNode().getName());
                if (targetCompare != 0) {
                    return targetCompare;
                }

                // If end nodes are the same, compare relationship types
                int typeCompare = Integer.compare(
                        RELATIONSHIP_ORDER.indexOf(firstEdge.getRelationship()),
                        RELATIONSHIP_ORDER.indexOf(secondEdge.getRelationship()));

                return typeCompare;
            }

            private int compareNodes(Node firstNode, Node secondNode) {
                // Existing logic for start nodes (name, type, ID)
                int nameCompare = firstNode.getName().compareToIgnoreCase(secondNode.getName());
                if (nameCompare != 0) {
                    return nameCompare;
                }

                if (firstNode.isCategory() && secondNode.isCategory()) {
                    return 0;
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