package edu.kit.kastel.recommendationsystem.util.parser;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.RecommendationStrategy;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

/**
 * A recursive descent parser for processing recommendation queries.
 * Translates input strings into sets of recommended product nodes using
 * a combination of recommendation strategies and set operations.
 * 
 * <p>
 * The parser supports the following operations:
 * <ul>
 * <li>Basic strategies: S1 (Siblings), S2 (Successors), S3 (Predecessors)</li>
 * <li>Set operations: UNION and INTERSECTION</li>
 * </ul>
 * 
 * <p>
 * Example query: {@code "UNION(INTERSECTION(S1 100, S2 200), S3 300)"}
 *
 * @author urrwg
 */

public final class RecursiveDescentParser {

    // Region: Constants
    private static final String WHITESPACE_NORMALIZATION_REGEX = "\\s*([(),])\\s*";
    private static final String MULTIPLE_WHITESPACE_REGEX = "\\s+";
    private static final String STRATEGY_PREFIX = "S";
    private static final String INPUT_SEPERATOR = " ";
    private static final String UNION_OPERATOR = "UNION";
    private static final String INTERSECTION_OPERATOR = "INTERSECTION";

    private static final String ERROR_UNKNOWN_OPERATOR = "unknown operator: %s";
    private static final String ERROR_INVALID_STRATEGY_NUMBER = "invalid strategy number";
    private static final String ERROR_MISSING_PRODUCT_ID = "expected product ID";
    private static final String ERROR_PRODUCT_NOT_FOUND = "product not found: %s";
    private static final String ERROR_INVALID_STRATEGY = "invalid strategy: %s";
    private static final String ERROR_EXPECTED_CHARACTER = "expected: '%s'";
    private static final String ERROR_UNEXPECTED_END_OF_INPUT = "unexpected characters at end of input";

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

    private RecursiveDescentParser() {
        // This is a utility class
    }

    /**
     * Parses a input string and evaluates it against the provided graph.
     * 
     * @param input The recommendation query string to parse. Expected format:
     *              {@code "[OPERATION(][STRATEGY ID, ]*)"} where:
     *              <ul>
     *              <li>OPERATION: UNION | INTERSECTION</li>
     *              <li>STRATEGY: S1 | S2 | S3</li>
     *              <li>ID: Numeric product identifier</li>
     *              </ul>
     * @param graph The graph data structure containing products and relationships
     * @return A set of recommended nodes matching the query criteria
     * @throws DataParsException If any of these occur:
     *                           <ul>
     *                           <li>Syntax errors in the input string</li>
     *                           <li>Invalid strategy specifications</li>
     *                           <li>References to non-existent products</li>
     *                           <li>Type mismatches in operations</li>
     *                           </ul>
     */
    public static Set<Node> parse(String input, Graph graph) throws DataParsException {
        ParserState state = new ParserState(input, graph);
        Term term = parseTerm(state);
        validateEndOfInput(state);
        return term.evaluate();
    }

    private static Term parseTerm(ParserState state) throws DataParsException {
        skipWhitespace(state);
        if (peek(state) == UNION_START_SYMBOL || peek(state) == INTERSECTION_START_SYMBOL) {
            String operator = parseIdentifier(state);
            return switch (operator.toUpperCase()) {
                case UNION_OPERATOR -> parseUnion(state);
                case INTERSECTION_OPERATOR -> parseIntersection(state);
                default -> throw new DataParsException(String.format(ERROR_UNKNOWN_OPERATOR, operator));
            };
        }
        return parseFinal(state);
    }

    private static UnionTerm parseUnion(ParserState state) throws DataParsException {
        match(state, PAREN_OPEN);
        Term left = parseTerm(state);
        match(state, COMMA);
        Term right = parseTerm(state);
        match(state, PAREN_CLOSE);
        return new UnionTerm(left, right);
    }

    private static IntersectionTerm parseIntersection(ParserState state) throws DataParsException {
        match(state, PAREN_OPEN);
        Term left = parseTerm(state);
        match(state, COMMA);
        Term right = parseTerm(state);
        match(state, PAREN_CLOSE);
        return new IntersectionTerm(left, right);
    }

    private static FinalTerm parseFinal(ParserState state) throws DataParsException {
        String strategy = parseStrategy(state);
        int productId = parseProductId(state);
        return new FinalTerm(strategy, productId, state.graph);
    }

    private static String parseStrategy(ParserState state) throws DataParsException {
        match(state, STRATEGY_ID_PREFIX);
        char number = peek(state);
        if (!STRATEGY_RECOGNITION_NUMBERS.contains(number)) {
            throw new DataParsException(ERROR_INVALID_STRATEGY_NUMBER);
        }
        state.position++;
        return STRATEGY_PREFIX + number;
    }

    private static int parseProductId(ParserState state) throws DataParsException {
        skipWhitespace(state);
        int start = state.position;
        while (state.position < state.input.length() && Character.isDigit(peek(state))) {
            state.position++;
        }
        if (start == state.position) {
            throw new DataParsException(ERROR_MISSING_PRODUCT_ID);
        }
        return Integer.parseInt(state.input.substring(start, state.position));
    }

    private static String parseIdentifier(ParserState state) throws DataParsException {
        skipWhitespace(state);
        int start = state.position;
        while (state.position < state.input.length() && Character.isLetter(peek(state))) {
            state.position++;
        }
        return state.input.substring(start, state.position);
    }

    private static void match(ParserState state, char expected) throws DataParsException {
        skipWhitespace(state);
        if (peek(state) != expected) {
            throw new DataParsException(String.format(ERROR_EXPECTED_CHARACTER, expected));
        }
        state.position++;
    }

    private static char peek(ParserState state) {
        return state.position < state.input.length() ? state.input.charAt(state.position) : 0;
    }

    private static void skipWhitespace(ParserState state) {
        while (state.position < state.input.length() && Character.isWhitespace(state.input.charAt(state.position))) {
            state.position++;
        }
    }

    private static void validateEndOfInput(ParserState state) throws DataParsException {
        skipWhitespace(state);
        if (state.position < state.input.length()) {
            throw new DataParsException(ERROR_UNEXPECTED_END_OF_INPUT);
        }
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

            return switch (strategy) {
                case STRATEGY_S1 -> RecommendationStrategy.findSiblingProducts(productNode, graph);
                case STRATEGY_S2 -> RecommendationStrategy.findSuccessorProducts(productNode, graph);
                case STRATEGY_S3 -> RecommendationStrategy.findPredecessorProducts(productNode, graph);
                default -> throw new DataParsException(String.format(ERROR_INVALID_STRATEGY, strategy));
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

    private static class ParserState {
        final String input;
        final Graph graph;
        int position;

        ParserState(String input, Graph graph) {
            this.input = input.trim()
                    .replaceAll(MULTIPLE_WHITESPACE_REGEX, INPUT_SEPERATOR);
            this.graph = graph;
            this.position = 0;
        }
    }
}