package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Edge;
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
        List<Node> nodes = new ArrayList<>(graph.getNodes());
        SortNodes.sort(nodes);

        StringBuilder output = new StringBuilder();

        for (Node node : nodes) {
            output.append(node.toString()).append(NODE_SEPERATOR);
 
            for (Edge edge : node.getEdges()) {
                System.out.println(edge.toString());
            }
            System.out.println("------------------------------------------");
        }

        return output.length() == 0 ? EMPTY_NODES_OUTPUT : output.toString().trim();
    }
}
