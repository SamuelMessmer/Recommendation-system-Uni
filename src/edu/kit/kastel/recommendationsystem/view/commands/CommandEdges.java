package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.util.SortUtils;
import edu.kit.kastel.recommendationsystem.view.Result;

/**
 * Command implementation for listing all edges in the graph.
 * Outputs edges sorted by source node, target node, and relationship type.
 * 
 * @author urrwg
 */
public class CommandEdges implements Command<Graph> {

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph handle) {
        List<Edge> edges = new ArrayList<>(handle.getEdges());
        SortUtils.sortEdges(edges);

        StringBuilder output = new StringBuilder();
        for (Edge edge : edges) {
            output.append(edge.toString()).append(System.lineSeparator());
        }
        return output.toString().trim();
    }
}