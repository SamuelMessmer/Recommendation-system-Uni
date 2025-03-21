package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Category;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.util.SortEdges;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandExport implements Command<Graph> {

    private static final String DIGRAPH_START_SYMBOL = "digraph {";
    private static final String DIGRAPH_END_SYMBOL = "}";
    private static final String CATEGORY_IDENTIFIER_STRING = " [shape=box]";
    private static final List<RelationshipType> PREDICATE_ORDER = Arrays.asList(
            RelationshipType.CONTAINS,
            RelationshipType.CONTAINED_IN,
            RelationshipType.PART_OF,
            RelationshipType.HAS_PART,
            RelationshipType.SUCCESSOR_OF,
            RelationshipType.PREDECESSOR_OF);

    @Override
    public Result execute(Graph graph) {
        StringBuilder output = new StringBuilder();
        output.append(DIGRAPH_START_SYMBOL).append(System.lineSeparator());

        List<Edge> edges = new ArrayList<>(graph.edges());
        Set<Category> categories = new HashSet<>();

        SortEdges.sort(edges);

        for (Edge edge : edges) {
            processEdge(edge, output, categories);
        }

        // Ist das überhaupt notwendig
        List<Category> sortedCategories = new ArrayList<>(categories);
        sortedCategories.sort(new CategoryComparator());

        for (Category category : sortedCategories) {
            output.append(category.getName())
                    .append(CATEGORY_IDENTIFIER_STRING)
                    .append(System.lineSeparator());
        }

        output.append(DIGRAPH_END_SYMBOL);
        return Result.success(output.toString());
    }

    private void processEdge(Edge edge, StringBuilder output, Set<Category> categories) {
        String source = edge.getStartNode().getName().toLowerCase();
        String target = edge.getEndNode().getName().toLowerCase();
        String label = formatRelationship(edge.getRelationship());

        output.append(source)
                .append(" -> ")
                .append(target)
                .append(" [label=")
                .append(label)
                .append("]\n");

        // Collect categories
        if (edge.getStartNode().isCategory()) {
            categories.add((Category) edge.getStartNode());
        }
        if (edge.getEndNode().isCategory()) {
            categories.add((Category) edge.getEndNode());
        }
    }

    private String formatRelationship(RelationshipType relationship) {
        return relationship.toString().toLowerCase().replace("-", "");
    }

    // Comparator implementations
    private static class EdgeComparator implements Comparator<Edge> {
        @Override
        public int compare(Edge e1, Edge e2) {
            // Compare source nodes
            int sourceCompare = compareNodes(e1.getStartNode(), e2.getStartNode());
            if (sourceCompare != 0) {
                return sourceCompare;
            }

            // Compare target nodes
            int targetCompare = compareNodes(e1.getEndNode(), e2.getEndNode());
            if (targetCompare != 0) {
                return targetCompare;
            }

            // Compare predicates
            return Integer.compare(
                    PREDICATE_ORDER.indexOf(e1.getRelationship()),
                    PREDICATE_ORDER.indexOf(e2.getRelationship()));
        }

        private int compareNodes(Node n1, Node n2) {
            String name1 = n1.getName().toLowerCase();
            String name2 = n2.getName().toLowerCase();
            int nameCompare = name1.compareTo(name2);

            if (nameCompare != 0) {
                return nameCompare;
            }

            if (n1.isProduct() && n2.isProduct()) {
                return Integer.compare(
                        ((Product) n1).getId(),
                        ((Product) n2).getId());
            }
            return 0;
        }
    }

    private static class CategoryComparator implements Comparator<Category> {
        @Override
        public int compare(Category c1, Category c2) {
            return c1.getName().compareToIgnoreCase(c2.getName());
        }
    }
}