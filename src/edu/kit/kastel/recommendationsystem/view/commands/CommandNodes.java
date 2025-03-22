package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.util.SortNodes;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandNodes implements Command<Graph> {

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.nodes());

        SortNodes.sort(nodes);

        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            if (sb.length() == 0) {
                sb.append(" ");
            }
            sb.append(node.toString());
        }
        return sb.toString().trim();
    }
}
