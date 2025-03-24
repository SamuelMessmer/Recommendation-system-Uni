package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.RecommendationStrategy;
import edu.kit.kastel.recommendationsystem.model.parser.DataParsException;
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
    private static final String INPUT_SEPARATOR = " ";
    private static final String OUTPUT_SEPARATOR = " ";
    private static final String EMPTY_OUTPUT = " ";

    private static final String ERROR_UNKNOWN_OPERATOR = "Unknown operator: %s";
    private static final String ERROR_INVALID_STRATEGY_NUMBER = "Invalid strategy number";
    private static final String ERROR_MISSING_PRODUCT_ID = "Expected product ID";
    private static final String ERROR_PRODUCT_NOT_FOUND = "Product not found: %s";
    private static final String ERROR_INVALID_STRATEGY = "Invalid strategy: %s";
    private static final String ERROR_EXPECTED_CHARACTER = "Expected: '%s'";
    private static final String ERROR_UNEXPECTED_END_OF_INPUT = "Unexpected characters at end of input";

    private static final char UNION_START_SYMBOL = 'U';
    private static final char INTERSECTION_START_SYMBOL = 'I';
    private static final char PAREN_OPEN = '(';
    private static final char PAREN_CLOSE = ')';
    private static final char COMMA = ',';
    private static final char STRATEGY_ID_PREFIX = 'S';
    private static final char SIBLING_STRATEGY_NUMBER = '1';
    private static final char SUCCESOR_STRATEGY_NUMBER = '2';
    private static final char PREDECESSOR_STRATEGY_NUMBER = '3';
    private static final List<Character> STRATEGY_RECOGNITION_NUMBERS = List.of(SIBLING_STRATEGY_NUMBER,
            SUCCESOR_STRATEGY_NUMBER, PREDECESSOR_STRATEGY_NUMBER);

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
                .replaceAll(MULTIPLE_WHITESPACE_REGEX, INPUT_SEPARATOR);
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
        } catch (DataParsException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private Term parseTerm() throws DataParsException {
        skipWhitespace();
        if (peek() == UNION_START_SYMBOL || peek() == INTERSECTION_START_SYMBOL) {
            String operator = parseIdentifier();
            return switch (operator.toUpperCase()) {
                case UNION_OPERATOR -> parseUnion();
                case INTERSECTION_OPERATOR -> parseIntersection();
                default -> throw new DataParsException(String.format(ERROR_UNKNOWN_OPERATOR, operator));
            };
        }
        return parseFinal();
    }

    private UnionTerm parseUnion() throws DataParsException {
        match(PAREN_OPEN);
        Term left = parseTerm();
        match(COMMA);
        Term right = parseTerm();
        match(PAREN_CLOSE);
        return new UnionTerm(left, right);
    }

    private IntersectionTerm parseIntersection() throws DataParsException {
        match(PAREN_OPEN);
        Term left = parseTerm();
        match(COMMA);
        Term right = parseTerm();
        match(PAREN_CLOSE);
        return new IntersectionTerm(left, right);
    }

    private FinalTerm parseFinal() throws DataParsException {
        String strategy = parseStrategy();
        int productId = parseProductId();
        return new FinalTerm(strategy, productId, graph);
    }

    private String parseStrategy() throws DataParsException {
        match(STRATEGY_ID_PREFIX);
        char number = peek();
        if (!STRATEGY_RECOGNITION_NUMBERS.contains(number)) {
            throw new DataParsException(ERROR_INVALID_STRATEGY_NUMBER);
        }
        position++;
        return STRATEGY_PREFIX + number;
    }

    private int parseProductId() throws DataParsException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isDigit(peek())) {
            position++;
        }
        if (start == position) {
            throw new DataParsException(ERROR_MISSING_PRODUCT_ID);
        }
        return Integer.parseInt(input.substring(start, position));
    }

    private String parseIdentifier() throws DataParsException {
        skipWhitespace();
        int start = position;
        while (position < input.length() && Character.isLetter(peek())) {
            position++;
        }
        return input.substring(start, position);
    }

    private void match(char expected) throws DataParsException {
        skipWhitespace();
        if (peek() != expected) {
            throw new DataParsException(String.format(ERROR_EXPECTED_CHARACTER, expected));
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

    private void validateEndOfInput() throws DataParsException {
        skipWhitespace();
        if (position < input.length()) {
            throw new DataParsException(ERROR_UNEXPECTED_END_OF_INPUT);
        }
    }

    private String formatOutput(Set<Node> nodes) {
        if (nodes.isEmpty()) {
            return EMPTY_OUTPUT;
        }

        List<Node> sorted = new ArrayList<>(nodes);
        SortUtils.sortNodes(sorted);

        StringBuilder sb = new StringBuilder();
        for (Node node : sorted) {
            if (sb.length() > 0) {
                sb.append(OUTPUT_SEPARATOR);
            }
            sb.append(node.toString().toLowerCase());
        }
        return sb.toString();
    }

    private interface Term {
        Set<Node> evaluate() throws DataParsException;
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
        public Set<Node> evaluate() throws DataParsException {
            Node productNode = graph.findProductById(productId);
            if (productNode == null) {
                throw new DataParsException(String.format(ERROR_PRODUCT_NOT_FOUND, productId));
            }

            Set<Node> result = switch (strategy) {
                case STRATEGY_S1 -> RecommendationStrategy.findSiblingProducts(productNode, graph);
                case STRATEGY_S2 -> RecommendationStrategy.findSuccessorProducts(productNode, graph);
                case STRATEGY_S3 -> RecommendationStrategy.findPredecessorProducts(productNode, graph);
                default -> throw new DataParsException(String.format(ERROR_INVALID_STRATEGY, strategy));
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
        public Set<Node> evaluate() throws DataParsException {
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
        public Set<Node> evaluate() throws DataParsException {
            Set<Node> intersection = new HashSet<>(left.evaluate());
            intersection.retainAll(right.evaluate());
            return intersection;
        }
    }
}