package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.NodeType;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.util.SortEdges;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandExport implements Command<Graph> {

    private static final String DIGRAPH_START_SYMBOL = "digraph {";
    private static final String DIGRAPH_END_SYMBOL = "}";
    private static final String CATEGORY_IDENTIFIER_STRING = " [shape=box]";
    private static final String EDGE_ARROW = " -> ";
    private static final String LABEL_START_SYMBOL = " [label=";
    private static final String LABEL_END_SYMBOL = "]";
    private static final String PREDICATE_SEPERATOR = "-";
    private static final String PREDICATE_SEPERATOR_REPLACEMENT_STRING = "";

    @Override
    public Result execute(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        SortEdges.sort(edges);
        
        StringBuilder output = new StringBuilder();
        
        output.append(DIGRAPH_START_SYMBOL).append(System.lineSeparator());
        createOutputString(edges, output);
        output.append(DIGRAPH_END_SYMBOL);

        return Result.success(output.toString());
    }

    private static void createOutputString(List<Edge> edges, StringBuilder output) {
        Set<String> categories = new LinkedHashSet<>();

        for (Edge edge : edges) {
            processEdge(edge, output, categories);

            Node startNode = edge.getStartNode();
            if (startNode.isOfType(NodeType.CATEGORY)) {
                categories.add(startNode.getName());
            }
        }

        for (String categoryName : categories) {
            output.append(categoryName)
                    .append(CATEGORY_IDENTIFIER_STRING)
                    .append(System.lineSeparator());

        }
    }

    private static void processEdge(Edge edge, StringBuilder output, Set<String> categories) {
        String source = edge.getStartNode().getName().toLowerCase();
        String target = edge.getEndNode().getName().toLowerCase();
        String label = formatRelationship(edge.getRelationship());

        output.append(source)
                .append(EDGE_ARROW)
                .append(target)
                .append(LABEL_START_SYMBOL)
                .append(label)
                .append(LABEL_END_SYMBOL)
                .append(System.lineSeparator());
    }

    private static String formatRelationship(RelationshipType relationship) {
        return relationship.toString().toLowerCase().replace(PREDICATE_SEPERATOR,
                PREDICATE_SEPERATOR_REPLACEMENT_STRING);
    }
}