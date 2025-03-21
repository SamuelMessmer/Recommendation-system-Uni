package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.util.SortEdges;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandEdges implements Command<Graph> {


    @Override
    public Result execute(Graph handle) {
        return Result.success(createOutputString(handle));
    }

    private String createOutputString(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.edges());

        SortEdges.sort(edges); 

        StringBuilder sb = new StringBuilder();
        for (Edge edge : edges) {
            sb.append(edge.toString()).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}