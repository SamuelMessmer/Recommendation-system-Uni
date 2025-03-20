package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandEdges implements Command<Graph> {

    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph handle) {
        StringBuilder builder = new StringBuilder();

        for (Edge edge : handle.edges()) {
            builder.append(edge.toString())
                    .append(System.lineSeparator());
        }

        return builder.toString().trim();
    }

}
