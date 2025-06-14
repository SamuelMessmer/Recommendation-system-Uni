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
import edu.kit.kastel.recommendationsystem.util.SortUtils;
import edu.kit.kastel.recommendationsystem.view.Result;

/**
 * Handles graph export functionality to DOT notation format.
 * Produces output compliant with Graphviz DOT language specifications,
 * visualizing nodes as boxes for categories and edges with relationship labels.
 * 
 * @author urrwg
 */
public class CommandExport implements Command<Graph> {

    private static final String DIGRAPH_START_SYMBOL = "digraph {";
    private static final String DIGRAPH_END_SYMBOL = "}";
    private static final String CATEGORY_IDENTIFIER_STRING = " [shape=box]";
    private static final String EDGE_INDICATOR_ARROW = " -> ";
    private static final String LABEL_START_SYMBOL = " [label=";
    private static final String LABEL_END_SYMBOL = "]";
    private static final String PREDICATE_SEPARATOR = "-";
    private static final String PREDICATE_SEPARATOR_REPLACEMENT_STRING = "";

    @Override
    public Result execute(Graph handle) {
        List<Edge> edges = new ArrayList<>(handle.getEdges());
        SortUtils.sortEdges(edges);

        StringBuilder output = new StringBuilder();

        output.append(DIGRAPH_START_SYMBOL).append(System.lineSeparator());
        buildOutputString(edges, output);
        output.append(DIGRAPH_END_SYMBOL);

        return Result.success(output.toString());
    }

    private static void buildOutputString(List<Edge> edges, StringBuilder output) {
        Set<Node> categories = new LinkedHashSet<>();

        for (Edge edge : edges) {
            processEdge(edge, output);

            if (edge.getStartNode().isOfType(NodeType.CATEGORY)) {
                categories.add(edge.getStartNode());
            }
        }

        processCategory(categories, output);
    }

    private static void processEdge(Edge edge, StringBuilder output) {
        String source = edge.getStartNode().getName();
        String target = edge.getEndNode().getName();
        String label = formatRelationship(edge.getRelationship());

        output.append(source)
                .append(EDGE_INDICATOR_ARROW)
                .append(target)
                .append(LABEL_START_SYMBOL)
                .append(label)
                .append(LABEL_END_SYMBOL)
                .append(System.lineSeparator());
    }

    private static void processCategory(Set<Node> categories, StringBuilder output) {
        for (Node category : categories) {
            output.append(category.getName())
                    .append(CATEGORY_IDENTIFIER_STRING)
                    .append(System.lineSeparator());
        }
    }

    private static String formatRelationship(RelationshipType relationship) {
        return relationship.toString().toLowerCase().replace(PREDICATE_SEPARATOR,
                PREDICATE_SEPARATOR_REPLACEMENT_STRING);
    }
}