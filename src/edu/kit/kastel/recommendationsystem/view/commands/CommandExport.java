package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.util.SortEdges;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandExport implements Command<Graph> {

    private static final String DIGRAPH_START_SYMBOL = "digraph {";
    private static final String DIGRAPH_END_SYMBOL = "}";
    private static final String CATEGORY_IDENTIFIER_STRING = " [shape=box]";

    @Override
    public Result execute(Graph graph) {
        StringBuilder output = new StringBuilder();
        output.append(DIGRAPH_START_SYMBOL).append(System.lineSeparator());

        List<Edge> edges = new ArrayList<>(graph.edges());
        Set<String> categories = new HashSet<>();

        SortEdges.sort(edges);

        for (Edge edge : edges) {
            processEdge(edge, output, categories);
        }

        for (String categoryName : categories) {
            output.append(categoryName)
                    .append(CATEGORY_IDENTIFIER_STRING)
                    .append(System.lineSeparator());
        }

        output.append(DIGRAPH_END_SYMBOL);
        return Result.success(output.toString());
    }

    private void processEdge(Edge edge, StringBuilder output, Set<String> categories) {
        String source = edge.getStartNode().getName().toLowerCase();
        String target = edge.getEndNode().getName().toLowerCase();
        String label = formatRelationship(edge.getRelationship());

        if (edge.getStartNode().isCategory()) {
            categories.add(edge.getStartNode().getName());
            return;
        }

        output.append(source)
                .append(" -> ")
                .append(target)
                .append(" [label=")
                .append(label)
                .append("]")
                .append(System.lineSeparator());
    }

    private String formatRelationship(RelationshipType relationship) {
        return relationship.toString().toLowerCase().replace("-", "");
    }
}