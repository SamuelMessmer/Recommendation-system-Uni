package edu.kit.kastel.recommendationsystem.util;

import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.Product;

public final class SortNodes {

    public static void sort(List<Node> nodes) {
        nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                int nameCompare = n1.getName().compareToIgnoreCase(n2.getName());
                if (nameCompare != 0) {
                    return nameCompare;
                }

                if (n1.isOfType(NodeType.PRODUCT) && n2.isOfType(NodeType.PRODUCT)) {
                    return Integer.compare(
                            ((Product) n1).getId(),
                            ((Product) n2).getId());
                }
                // Produkte kommen vor Kategorien bei gleichem Namen
                return n1.isOfType(NodeType.PRODUCT) ? -1 : 1;
            }
        });
    }
}
