package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

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
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        Map<String, Node> mentionedNodes = new LinkedHashMap<>();

        for (String line : this.lines) {
            this.currentLine = line;
            Node subject = parseSubject();
            RelationshipType predicate = parsePredicate();
            Node object = parseObject();
            validateEndOfInput();
            validateSemantics(subject, predicate, object);

            nodes.add(subject);
            edges.add(new Edge(subject, object, predicate));
            edges.add(new Edge(object, subject, predicate));
            
            mentionedNodes.put(object.toString(), object);
        }

        for (Node node : nodes) {
            if (mentionedNodes.get(node.toString()) == null) {
                throw new DataParsException("semantic Error");
            }
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
        if (currentTokenStart == position) {
            throw new DataParsException("Expected name");
        }
        return currentLine.substring(currentTokenStart, position);
    }

    private int consumeNumber() throws DataParsException {
        currentTokenStart = position;
        while (position < currentLine.length() && Character.isDigit(this.currentLine.charAt(position))) {
            position++;
        }
        if (currentTokenStart == position) {
            throw new DataParsException("Expected number");
        }
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
            throw new DataParsException("Unexpected characters at the end of a line");
        }
    }

    private void validateSemantics(Node subject, RelationshipType predicate, Node object) throws DataParsException {
        // if (predicate.requiresProducts() && (!(subject instanceof Product) ||
        // !(object instanceof Product))) {
        // throw new DataParsException(predicate + " requires products");
        // }
    }

    private static boolean isValidNameChar(char c) {
        return Character.isLetterOrDigit(c) || c == '-';
    }
}