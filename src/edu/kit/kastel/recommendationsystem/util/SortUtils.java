package edu.kit.kastel.recommendationsystem.util;

import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

/**
 * Provides utility methods for sorting nodes and edges in a graph.
 * 
 * @author urrwg
 */
public final class SortUtils {

    private static final int EQUAL = 0;
    private static final int PRODUCT_FIRST = -1;
    private static final int CATEGORY_FIRST = 1;
    private static final List<RelationshipType> RELATIONSHIP_ORDER = List.of(
            RelationshipType.CONTAINS,
            RelationshipType.CONTAINED_IN,
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);

    private SortUtils() {
        // This is a Utility class
    }

    /**
     * Sorts a list of edges based on the name of their start node, end node, and
     * relationship type in alphabetical order.
     * 
     * @param edges the list of edges to be sorted
     */
    public static void sortEdges(List<Edge> edges) {
        edges.sort(new Comparator<Edge>() {
            @Override
            public int compare(Edge firstEdge, Edge secondEdge) {
                int sourceCompare = compareNodes(firstEdge.getStartNode(), secondEdge.getStartNode());
                if (sourceCompare != EQUAL) {
                    return sourceCompare;
                }

                int targetCompare = compareNodes(firstEdge.getEndNode(), secondEdge.getEndNode());
                if (targetCompare != EQUAL) {
                    return targetCompare;
                }

                int typeCompare = Integer.compare(
                        RELATIONSHIP_ORDER.indexOf(firstEdge.getRelationship()),
                        RELATIONSHIP_ORDER.indexOf(secondEdge.getRelationship()));

                return typeCompare;
            }

            private int compareNodes(Node firstNode, Node secondNode) {
                return firstNode.getName().compareToIgnoreCase(secondNode.getName());
            }
        });
    }

    /**
     * Sorts a list of nodes alphabetically by name.
     * If two nodes have the same name, products are sorted by their ID, and
     * categories are sorted after products.
     *
     * @param nodes the list of nodes to sort
     */
    public static void sortNodes(List<Node> nodes) {
        nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node firstNode, Node secondNode) {
                int nameCompare = firstNode.getName().compareToIgnoreCase(secondNode.getName());
                if (nameCompare != EQUAL) {
                    return nameCompare;
                }

                if (firstNode.isOfType(NodeType.PRODUCT) && secondNode.isOfType(NodeType.PRODUCT)) {
                    return Integer.compare(
                            ((Product) firstNode).getId(),
                            ((Product) secondNode).getId());
                }
                return firstNode.isOfType(NodeType.PRODUCT) ? PRODUCT_FIRST : CATEGORY_FIRST;
            }
        });
    }
}
