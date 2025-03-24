package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.RecommendationStrategy;
import edu.kit.kastel.recommendationsystem.util.SortUtils;
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

    private static final String WHITESPACE_NORMALIZATION_REGEX = "\\s*([(),])\\s*";
    private static final String MULTIPLE_WHITESPACE_REGEX = "\\s+";
    private static final String STRATEGY_PREFIX = "S";
    private static final String UNION_OPERATOR = "UNION";
    private static final String INTERSECTION_OPERATOR = "INTERSECTION";
    private static final String ERROR_UNKNOWN_OPERATOR = "Unknown operator: ";
    private static final String ERROR_UNEXPECTED_CHARACTERS = "Unexpected characters at end of input";
    private static final String ERROR_INVALID_STRATEGY_NUMBER = "Invalid strategy number";
    private static final String ERROR_MISSING_PRODUCT_ID = "Expected product ID";
    private static final String ERROR_PRODUCT_NOT_FOUND = "Product not found: ";
    private static final String ERROR_INVALID_STRATEGY = "Invalid strategy: ";
    private static final String OUTPUT_SEPARATOR = " ";
    private static final char PAREN_OPEN = '(';
    private static final char PAREN_CLOSE = ')';
    private static final char COMMA = ',';
    private static final char STRATEGY_ID_PREFIX = 'S';

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
                .replaceAll(WHITESPACE_NORMALIZATION_REGEX, "$1")
                .replaceAll(MULTIPLE_WHITESPACE_REGEX, " ");
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
                case UNION_OPERATOR -> parseUnion();
                case INTERSECTION_OPERATOR -> parseIntersection();
                default -> throw error(ERROR_UNKNOWN_OPERATOR + operator);
            };
        }
        return parseFinal();
    }

    private UnionTerm parseUnion() throws ParseException {
        match(PAREN_OPEN);
        Term left = parseTerm();
        match(COMMA);
        Term right = parseTerm();
        match(PAREN_CLOSE);
        return new UnionTerm(left, right);
    }

    private IntersectionTerm parseIntersection() throws ParseException {
        match(PAREN_OPEN);
        Term left = parseTerm();
        match(COMMA);
        Term right = parseTerm();
        match(PAREN_CLOSE);
        return new IntersectionTerm(left, right);
    }

    private FinalTerm parseFinal() throws ParseException {
        String strategy = parseStrategy();
        int productId = parseProductId();
        return new FinalTerm(strategy, productId, graph);
    }

    private String parseStrategy() throws ParseException {
        match(STRATEGY_ID_PREFIX);
        char num = peek();
        if (num < '1' || num > '3') {
            throw error(ERROR_INVALID_STRATEGY_NUMBER);
        }
        position++;
        return STRATEGY_PREFIX + num;
    }

    private int parseProductId() throws ParseException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isDigit(peek())) {
            position++;
        }
        if (start == position) {
            throw error(ERROR_MISSING_PRODUCT_ID);
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
        SortUtils.sortNodes(sorted);

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
        private static final String STRATEGY_S1 = "S1";
        private static final String STRATEGY_S2 = "S2";
        private static final String STRATEGY_S3 = "S3";

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
                throw new ParseException(ERROR_PRODUCT_NOT_FOUND + productId);
            }

            Set<Node> result = switch (strategy) {
                case STRATEGY_S1 -> RecommendationStrategy.findSiblingProducts(productNode, graph);
                case STRATEGY_S2 -> RecommendationStrategy.findSuccessorProducts(productNode, graph);
                case STRATEGY_S3 -> RecommendationStrategy.findPredecessorProducts(productNode, graph);
                default -> throw new ParseException(ERROR_INVALID_STRATEGY + strategy);
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