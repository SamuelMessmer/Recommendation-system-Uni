package edu.kit.kastel.recommendationsystem.util;

import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Node;
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

                if (n1.isProduct() && n2.isProduct()) {
                    return Integer.compare(
                            ((Product) n1).getId(),
                            ((Product) n2).getId());
                }
                // Produkte kommen vor Kategorien bei gleichem Namen
                return n1.isProduct() ? -1 : 1;
            }
        });
    }
}
