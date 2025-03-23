package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.recommendationstrategy.RecommendationStrategy;
import edu.kit.kastel.recommendationsystem.util.SortNodes;
import edu.kit.kastel.recommendationsystem.view.Result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements the recommendation command with recursive descent parsing.
 * Supports union/intersection operations between recommendation strategies.
 * Validates input syntax and handles strategy execution.
 * 
 * @author urrwg
 */
public class CommandRecommend implements Command<Graph> {

    private final String input;
    private int position;
    private Graph graph;

    /**
     * Constructs a new CommandRecommend instance.
     * 
     * @param input the input string representing the recommendation query,
     *              which will be parsed to determine the recommendation strategy
     */
    public CommandRecommend(String input) {
        this.input = input.trim()
                .replaceAll("\\s*([(),])\\s*", "$1") // Normalize whitespace
                .replaceAll("\\s+", " "); // Collapse multiple spaces
        this.position = 0;
    }

    @Override
    public Result execute(Graph handle) {
        this.graph = handle;
        try {
            Term term = parseTerm();
            validateEndOfInput();
            Set<Node> recommendations = term.evaluate();
            return Result.success(formatOutput(recommendations));
        } catch (ParseException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private Term parseTerm() throws ParseException {
        skipWhitespace();
        if (peek() == 'U' || peek() == 'I') {
            String operator = parseIdentifier();
            return switch (operator.toUpperCase()) {
                case "UNION" -> parseUnion();
                case "INTERSECTION" -> parseIntersection();
                default -> throw error("Unknown operator: " + operator);
            };
        }
        return parseFinal();
    }

    private UnionTerm parseUnion() throws ParseException {
        match('(');
        Term left = parseTerm();
        match(',');
        Term right = parseTerm();
        match(')');
        return new UnionTerm(left, right);
    }

    private IntersectionTerm parseIntersection() throws ParseException {
        match('(');
        Term left = parseTerm();
        match(',');
        Term right = parseTerm();
        match(')');
        return new IntersectionTerm(left, right);
    }

    private FinalTerm parseFinal() throws ParseException {
        String strategy = parseStrategy();
        int productId = parseProductId();
        return new FinalTerm(strategy, productId, graph);
    }

    private String parseStrategy() throws ParseException {
        match('S');
        char num = peek();
        if (num < '1' || num > '3') {
            throw error("Invalid strategy number");
        }
        position++;
        return "S" + num;
    }

    private int parseProductId() throws ParseException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isDigit(peek())) {
            position++;
        }
        if (start == position) {
            throw error("Expected product ID");
        }
        return Integer.parseInt(input.substring(start, position));
    }

    private String parseIdentifier() throws ParseException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isLetter(peek())) {
            position++;
        }
        return input.substring(start, position);
    }

    private void match(char expected) throws ParseException {
        skipWhitespace();
        if (peek() != expected) {
            throw error("Expected '" + expected + "'");
        }
        position++;
    }

    private char peek() {
        return position < input.length() ? input.charAt(position) : 0;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    private void validateEndOfInput() throws ParseException {
        skipWhitespace();
        if (position < input.length()) {
            throw error("Unexpected characters at end of input");
        }
    }

    private ParseException error(String message) {
        return new ParseException(message + " at position " + position + " in: '" + input + "'");
    }

    private String formatOutput(Set<Node> nodes) {
        if (nodes.isEmpty()) {
            return "";
        }

        List<Node> sorted = new ArrayList<>(nodes);
        SortNodes.sort(sorted);

        StringBuilder sb = new StringBuilder();
        for (Node node : sorted) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(node.toString().toLowerCase());
        }
        return sb.toString();
    }

    private interface Term {
        Set<Node> evaluate() throws ParseException;
    }

    private static class FinalTerm implements Term {
        private final String strategy;
        private final int productId;
        private final Graph graph;

        FinalTerm(String strategy, int productId, Graph graph) {
            this.strategy = strategy;
            this.productId = productId;
            this.graph = graph;
        }

        @Override
        public Set<Node> evaluate() throws ParseException {
            Node productNode = graph.findProductById(productId);
            if (productNode == null) {
                throw new ParseException("Product not found: " + productId);
            }

            Set<Node> result = switch (strategy) {
                case "S1" -> RecommendationStrategy.findSiblingProducts(productNode, graph);
                case "S2" -> RecommendationStrategy.findSuccessorProducts(productNode, graph);
                case "S3" -> RecommendationStrategy.findPredecessorProducts(productNode, graph);
                default -> throw new ParseException("Invalid strategy: " + strategy);
            };

            return result;
        }
    }

    private static class UnionTerm implements Term {
        private final Term left;
        private final Term right;

        UnionTerm(Term left, Term right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Set<Node> evaluate() throws ParseException {
            Set<Node> union = new HashSet<>(left.evaluate());
            union.addAll(right.evaluate());
            return union;
        }
    }

    private static class IntersectionTerm implements Term {
        private final Term left;
        private final Term right;

        IntersectionTerm(Term left, Term right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Set<Node> evaluate() throws ParseException {
            Set<Node> intersection = new HashSet<>(left.evaluate());
            intersection.retainAll(right.evaluate());
            return intersection;
        }
    }

    private static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }
}