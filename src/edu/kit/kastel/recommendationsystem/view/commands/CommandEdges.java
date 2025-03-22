package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.util.SortEdges;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandEdges implements Command<Graph> {


    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph handle) {
        List<Edge> edges = new ArrayList<>(handle.getEdges());
        SortEdges.sort(edges); 

        StringBuilder output = new StringBuilder();
        for (Edge edge : edges) {
            output.append(edge.toString()).append(System.lineSeparator());
        }
        return output.toString().trim();
    }
}