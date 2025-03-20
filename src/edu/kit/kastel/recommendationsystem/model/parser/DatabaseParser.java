package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.kastel.recommendationsystem.model.Category;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public class DatabaseParser {

    private final List<String> lines;
    private int position;
    private int currentTokenStart;
    private String currentLine;

    public DatabaseParser(List<String> lines) {
        this.lines = lines;
        this.position = 0;
    }

    public Graph parse() throws DataParsException {
        Map<String, Node> nodes = new LinkedHashMap<>();
        Map<String, Edge> edges = new LinkedHashMap<>();

        for (String line : this.lines) {
            this.currentLine = line;
            Node subject = parseSubject();
            RelationshipType predicate = parsePredicate();
            Node object = parseObject();
            validateEndOfInput();
            validateSemantics(subject, predicate, object);

            System.out.println("Erste Line geparsed..");
        }

        return new Graph(nodes, edges);
    }

    private Node parseSubject() throws DataParsException {
        return parseNode();
    }

    private Node parseObject() throws DataParsException {
        return parseNode();
    }

    private Node parseNode() throws DataParsException {
        skipWhitespace();
        if (peek() == '(') {
            throw new DataParsException("Unexpected '('");
        }

        String name = consumeName();
        if (peek() == '(') {
            return parseProduct(name);
        }
        return new Category(name.toLowerCase());
    }

    private Product parseProduct(String name) throws DataParsException {
        consume('(');
        consumeIdKeyword();
        consume('=');
        int id = consumeNumber();
        consume(')');
        return new Product(name.toLowerCase(), id);
    }

    private RelationshipType parsePredicate() throws DataParsException {
        skipWhitespace();
        String predicate = consumePredicate();
        return RelationshipType.fromString(predicate);
    }

    private String consumeName() throws DataParsException {
        currentTokenStart = position;
        while (position < currentLine.length() && isValidNameChar(currentLine.charAt(position))) {
            position++;
        }
        if (currentTokenStart == position)
            throw new DataParsException("Expected name");
        return currentLine.substring(currentTokenStart, position);
    }

    private int consumeNumber() throws DataParsException {
        currentTokenStart = position;
        while (position < currentLine.length() && Character.isDigit(this.currentLine.charAt(position))) {
            position++;
        }
        if (currentTokenStart == position)
            throw new DataParsException("Expected number");
        return Integer.parseInt(this.currentLine.substring(currentTokenStart, position));
    }

    private void consumeIdKeyword() throws DataParsException {
        skipWhitespace();
        if (!this.currentLine.regionMatches(position, "id", 0, 2)) {
            throw new DataParsException("Expected 'id'");
        }
        position += 2;
        skipWhitespace();
    }

    private String consumePredicate() throws DataParsException {
        currentTokenStart = position;
        while (position < this.currentLine.length() && isValidNameChar(this.currentLine.charAt(position))) {
            position++;
        }
        if (currentTokenStart == position) {
            throw new DataParsException("Expected Predicate");
        }
        return this.currentLine.substring(currentTokenStart, position);
    }

    private void consume(char expected) throws DataParsException {
        skipWhitespace();
        if (peek() != expected) {
            throw new DataParsException("Expected '" + expected + "'");
        }
        position++;
    }

    private char peek() {
        return position < this.currentLine.length() ? this.currentLine.charAt(position) : 0;
    }

    private void skipWhitespace() {
        while (position < this.currentLine.length() && Character.isWhitespace(this.currentLine.charAt(position))) {
            position++;
        }
    }

    private void validateEndOfInput() throws DataParsException {
        skipWhitespace();
        if (position < this.currentLine.length()) {
            throw new DataParsException("Unexpected characters at end of input");
        }
    }

    private void validateSemantics(Node subject, RelationshipType predicate, Node object) throws DataParsException {
        // if (predicate.requiresProducts() && (!(subject instanceof Product) || !(object instanceof Product))) {
        //     throw new DataParsException(predicate + " requires products");
        // }
    }

    private static boolean isValidNameChar(char c) {
        return Character.isLetterOrDigit(c) || c == '-';
    }
}