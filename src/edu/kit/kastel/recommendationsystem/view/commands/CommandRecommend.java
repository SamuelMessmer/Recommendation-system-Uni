package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.recommendationstrategy.RecommendationStrategy;
import edu.kit.kastel.recommendationsystem.util.SortNodes;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandRecommend implements Command<Graph> {

    private final String input;
    private int position;
    private Graph graph;

    public CommandRecommend(String input) {
        this.input = input.trim()
                .replaceAll("\\s*([(),])\\s*", "$1")
                .replaceAll("\\s+", " ");
        this.position = 0;
    }

    @Override
    public Result execute(Graph handle) {
        this.graph = handle;
        try {
            Term term = parseRecommendCommand();
            Set<Node> recommendations = term.evaluate();
            return Result.success(formatOutput(recommendations));
        } catch (ParseException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private Term parseRecommendCommand() throws ParseException {
        match("recommend");
        Term term = parseTerm();
        validateEndOfInput();
        return term;
    }

    private Term parseTerm() throws ParseException {
        skipWhitespace();
        if (peek() == 'I') {
            String identifier = parseIdentifier();
            return switch (identifier.toUpperCase()) {
                case "UNION" -> parseUnion();
                case "INTERSECTION" -> parseIntersection();
                default -> throw error("Unknown operator: " + identifier);
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
        int productId = parseNumber();
        return new FinalTerm(strategy, productId, graph);
    }

    private String parseStrategy() throws ParseException {
        skipWhitespace();
        match('S');
        char num = peek();
        if (num < '1' || num > '3')
            throw error("Invalid strategy number");
        position++;
        return "S" + num;
    }

    private int parseNumber() throws ParseException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        if (start == position)
            throw error("Expected product ID");
        return Integer.parseInt(input.substring(start, position));
    }

    private String parseIdentifier() throws ParseException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isLetter(input.charAt(position))) {
            position++;
        }
        return input.substring(start, position);
    }

    private void match(String expected) throws ParseException {
        skipWhitespace();
        if (!input.startsWith(expected, position)) {
            throw error("Expected '" + expected + "'");
        }
        position += expected.length();
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
        for (Node p : sorted) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(p.toString()).append(" ");
        }
        return sb.toString();
    }

    // Term Interface und Implementierungen
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
            Node node = graph.findProductById(productId);
            if (node == null) {
                throw new ParseException("Product not found: " + productId);
            }

            return switch (strategy) {
                case "S1" -> RecommendationStrategy.findSiblingProducts(node, graph);
                case "S2" -> RecommendationStrategy.findSuccessorProducts(node, graph);
                case "S3" -> RecommendationStrategy.findPredecessorProducts(node, graph);
                default -> throw new ParseException("Unknown strategy: " + strategy);
            };
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
            Set<Node> result = new HashSet<>(left.evaluate());
            result.addAll(right.evaluate());
            return result;
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
            Set<Node> leftResult = left.evaluate();
            leftResult.retainAll(right.evaluate());
            return leftResult;
        }
    }

    private static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}