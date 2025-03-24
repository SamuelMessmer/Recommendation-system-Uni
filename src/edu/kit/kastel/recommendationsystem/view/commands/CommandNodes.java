package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.List;
import java.util.ArrayList;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.util.SortUtils;
import edu.kit.kastel.recommendationsystem.view.Result;

/**
 * Command implementation for listing all nodes in the graph.
 * Outputs nodes sorted alphabetically by name, with products formatted as
 * name:id.
 * 
 * @author urrwg
 */
public class CommandNodes implements Command<Graph> {

    private static final String EMPTY_NODES_OUTPUT = " ";
    private static final String NODE_SEPERATOR = " ";

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph graph) {
        List<Node> nodes = new ArrayList<>(graph.getNodes());
        SortUtils.SortNodes(nodes);

        StringBuilder output = new StringBuilder();

        for (Node node : nodes) {
            output.append(node.toString())
                    .append(NODE_SEPERATOR);
        }

        return output.isEmpty() ? EMPTY_NODES_OUTPUT : output.toString().trim();
    }
}
