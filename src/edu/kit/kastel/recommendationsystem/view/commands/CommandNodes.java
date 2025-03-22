package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.util.SortNodes;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandNodes implements Command<Graph> {

    private static final String EMPTY_NODES_OUTPUT = " ";
    private static final String NODE_SEPERATOR = " ";

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph graph) {
        Set<String> nodes = new LinkedHashSet<>();

        SortNodes.sort(graph.nodes());

        for (Node node : graph.nodes()) {
            nodes.add(node.toString());
        }

        StringBuilder output = new StringBuilder();
        for (String nodeString : nodes) {
            if (output.length() == 0) {
                output.append(EMPTY_NODES_OUTPUT);
            }
            output.append(nodeString).append(NODE_SEPERATOR);
        }
        return output.toString().trim();
    }
}
