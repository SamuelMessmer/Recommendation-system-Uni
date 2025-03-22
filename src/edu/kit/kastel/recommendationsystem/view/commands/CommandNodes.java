package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
        List<Node> nodes = new ArrayList<>();
        nodes.addAll(graph.getNodes());
        Set<String> nodeNames = new LinkedHashSet<>();

        SortNodes.sort(nodes);

        for (Node node : graph.getNodes()) {
            nodeNames.add(node.toString());
        }

        StringBuilder output = new StringBuilder();
        for (String nodeString : nodeNames) {
            if (output.length() == 0) {
                output.append(EMPTY_NODES_OUTPUT);
            }
            output.append(nodeString).append(NODE_SEPERATOR);
        }
        return output.toString().trim();
    }
}
