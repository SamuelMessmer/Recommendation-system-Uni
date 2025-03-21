package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandNodes implements Command<Graph> {

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.nodes());

        // Sortierung: zuerst Name (case-insensitive), dann ID
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

        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            if (sb.length() == 0) {
                sb.append(" ");
            }
            sb.append(node.toString()).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
