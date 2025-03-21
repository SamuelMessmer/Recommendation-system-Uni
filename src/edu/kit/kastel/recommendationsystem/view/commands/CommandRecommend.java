package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.*;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandRecommend implements Command<Graph> {

    private String input;
    private int position;
    private Graph graph;

    @Override
    public Result execute(Graph handle) {
        this.graph = handle;
        try {
            Term term = parseRecommendCommand();
            Set<Product> recommendations = term.evaluate();
            return Result.success(formatOutput(recommendations));
        } catch (ParseException e) {
            return Result.error("Error: " + e.getMessage());
        }
    }

    public void setInput(String input) {
        // Entferne unerwünschte Leerzeichen um Operatoren und Kommas
        this.input = input.trim()
                .replaceAll("\\s*([(),])\\s*", "$1")
                .replaceAll("\\s+", " ");
        this.position = 0;
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

    private String formatOutput(Set<Product> products) {
        if (products.isEmpty()) {
            return "";
        }

        List<Product> sorted = new ArrayList<>(products);
        sorted.sort((p1, p2) -> {
            int nameCompare = p1.getName().compareToIgnoreCase(p2.getName());
            return nameCompare != 0 ? nameCompare : Integer.compare(p1.getId(), p2.getId());
        });

        StringBuilder sb = new StringBuilder();
        for (Product p : sorted) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(p.getName().toLowerCase()).append(":").append(p.getId());
        }
        return sb.toString();
    }

    // Term Interface und Implementierungen
    private interface Term {
        Set<Product> evaluate() throws ParseException;
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
        public Set<Product> evaluate() throws ParseException {
            // Product product = graph.findProductById(productId)
                    // .orElseThrow(() -> new ParseException("Product not found: " + productId));

            return null;

            // return switch (strategy) {
            // case "S1" -> graph.findSiblingProducts(product);
            // case "S2" -> graph.findSuccessorProducts(product);
            // case "S3" -> graph.findPredecessorProducts(product);
            // default -> throw new ParseException("Unknown strategy: " + strategy);
            // };
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
        public Set<Product> evaluate() throws ParseException {
            Set<Product> result = new HashSet<>(left.evaluate());
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
        public Set<Product> evaluate() throws ParseException {
            Set<Product> leftResult = left.evaluate();
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