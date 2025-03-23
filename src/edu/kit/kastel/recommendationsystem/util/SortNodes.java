package edu.kit.kastel.recommendationsystem.util;

import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.Product;

/**
 * Provides utility methods for sorting nodes in a graph.
 * Nodes are sorted primarily by name, and secondarily by ID if they are
 * products.
 * 
 * @author urrwg
 */
public final class SortNodes {

    private SortNodes() {
        // This is a utility class
    }

    /**
     * Sorts a list of nodes alphabetically by name.
     * If two nodes have the same name, products are sorted by their ID, and
     * categories are sorted after products.
     *
     * @param nodes the list of nodes to sort
     */
    public static void sort(List<Node> nodes) {
        nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node firstNode, Node secondNode) {
                int nameCompare = firstNode.getName().compareToIgnoreCase(secondNode.getName());
                if (nameCompare != 0) {
                    return nameCompare;
                }

                if (firstNode.isOfType(NodeType.PRODUCT) && secondNode.isOfType(NodeType.PRODUCT)) {
                    return Integer.compare(
                            ((Product) firstNode).getId(),
                            ((Product) secondNode).getId());
                }
                return firstNode.isOfType(NodeType.PRODUCT) ? -1 : 1;
            }
        });
    }
}
