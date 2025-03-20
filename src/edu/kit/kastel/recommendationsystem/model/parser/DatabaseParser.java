package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.kastel.recommendationsystem.model.Category;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

public class DatabaseParser {

    private List<Node> nodes;
    private List<Edge> edges;

    private List<Token> tokens;
    private int pos = 0;

    public DatabaseParser() {
    }

    public Graph parse(List<String> lines) throws DataParsException {
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();

        for (String line : lines) {
            parseLine(line);
        }

        return new Graph(this.nodes, this.edges);
    }

    public void parseLine(String line) throws DataParsException {
        this.tokens = tokenize(line);
        this.pos = 0;

        Node subject = parseSubject();
        RelationshipType predicate = parsePredicate();
        Node object = parseObject();

        validateEndOfInput();
        validateSemantics(subject, predicate, object);

        nodes.add(object);
        nodes.add(subject);
        edges.add(new Edge(subject, object, predicate));
        // edges.add(new Edge(object, subject, predicate.getReverse()));
    }

    // Token-Typen
    private enum TokenType {
        PRODUCT_NAME, CATEGORY_NAME, PREDICATE,
        NUMBER, LPAREN, RPAREN, ID_KEY, EQUALS, EOF
    }

    // Token-Klasse
    private static class Token {
        final TokenType type;
        final String value;
        final int position;

        Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
    }

    // Lexer
    private List<Token> tokenize(String line) throws DataParsException {
        List<Token> tokens = new ArrayList<>();
        int position = 0;
        Pattern pattern = Pattern.compile(
                "(?<PREDICATE>contains|contained\\-in|part\\-of|has\\-part|successor\\-of|predecessor\\-of)|"
                        + "(?<PRODUCT>[a-zA-Z0-9]+\\s*\\(.*?\\))|"
                        + "(?<CATEGORY>[a-zA-Z0-9]+)|"
                        + "(?<LPAREN>\\()|(?<RPAREN>\\))|"
                        + "(?<EQUALS>=)|"
                        + "(?<ID>id)|"
                        + "(?<NUMBER>\\d+)|"
                        + "(?<WS>\\s+)");

        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            if (matcher.group("WS") != null) {
                continue;
            }

            for (String group : new String[] { "PRODUCT", "PREDICATE", "CATEGORY", "LPAREN", "RPAREN", "EQUALS", "ID",
                    "NUMBER" }) {
                if (matcher.group(group) != null) {
                    TokenType type = mapGroupToTokenType(group);
                    tokens.add(new Token(type, matcher.group(group).trim(), position));
                    position += matcher.end() - matcher.start();
                    break;
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, "", position));
        return tokens;
    }

    private TokenType mapGroupToTokenType(String group) {
        switch (group) {
            case "PRODUCT":
                return TokenType.PRODUCT_NAME;
            case "PREDICATE":
                return TokenType.PREDICATE;
            case "CATEGORY":
                return TokenType.CATEGORY_NAME;
            case "LPAREN":
                return TokenType.LPAREN;
            case "RPAREN":
                return TokenType.RPAREN;
            case "EQUALS":
                return TokenType.EQUALS;
            case "ID":
                return TokenType.ID_KEY;
            case "NUMBER":
                return TokenType.NUMBER;
            default:
                throw new IllegalArgumentException("Unknown token group");
        }
    }

    // Parser-Methoden
    private Node parseSubject() throws DataParsException {
        return parseNode();
    }

    private Node parseObject() throws DataParsException {
        return parseNode();
    }

    private Node parseNode() throws DataParsException {
        if (peek(TokenType.PRODUCT_NAME)) {
            return parseProduct();
        }
        if (peek(TokenType.CATEGORY_NAME)) {
            return parseCategory();
        }
        throw error("Expected product or category");
    }

    private Product parseProduct() throws DataParsException {
        String productPart = consume(TokenType.PRODUCT_NAME).value;
        Matcher m = Pattern.compile("([a-zA-Z0-9]+)\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\)")
                .matcher(productPart);
        if (!m.matches()) {
            throw error("Invalid product format");
        }
        return new Product(m.group(1).toLowerCase(), Integer.parseInt(m.group(2)));
    }

    private Category parseCategory() throws DataParsException {
        return new Category(consume(TokenType.CATEGORY_NAME).value.toLowerCase());
    }

    private RelationshipType parsePredicate() throws DataParsException {
        try {
            Token predicateToken = consume(TokenType.PREDICATE);
            return RelationshipType.fromString(predicateToken.value);
        } catch (DataParsException e) {
            throw new DataParsException("Invalid predicate: " + e.getMessage());
        }
    }

    // Hilfsmethoden
    private Token consume(TokenType expected) throws DataParsException {
        if (pos >= tokens.size()) {
            throw error("Unexpected end of input");
        }
        Token t = tokens.get(pos);
        if (t.type != expected) {
            throw error("Expected " + expected + " but found " + t.type);
        }
        pos++;
        return t;
    }

    private boolean peek(TokenType type) {
        return pos < tokens.size() && tokens.get(pos).type == type;
    }

    private void validateEndOfInput() throws DataParsException {
        if (pos < tokens.size() - 1) { // EOF-Token ist immer vorhanden
            throw error("Unexpected tokens after valid input");
        }
    }

    private void validateSemantics(Node subject, RelationshipType type, Node object) throws DataParsException {
        // Beispiel: successor-of nur zwischen Produkten
        if (type == RelationshipType.SUCCESSOR_OF
                && (!(subject instanceof Product) || !(object instanceof Product))) {
            throw error("Successor relationship requires products");
        }
    }

    private DataParsException error(String msg) {
        int position = pos < tokens.size() ? tokens.get(pos).position : 0;
        return new DataParsException("Error at position " + position + ": " + msg);
    }
}

// package edu.kit.kastel.recommendationsystem.model.parser;

// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;

// import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

// import edu.kit.kastel.recommendationsystem.model.Category;
// import edu.kit.kastel.recommendationsystem.model.Edge;
// import edu.kit.kastel.recommendationsystem.model.Graph;
// import edu.kit.kastel.recommendationsystem.model.Node;
// import edu.kit.kastel.recommendationsystem.model.Product;
// import edu.kit.kastel.recommendationsystem.model.RelationshipType;

// public class DatabaseParser {

// private final List<String> lines;
// private int position;
// private int currentTokenStart;
// private String currentLine;

// public DatabaseParser(List<String> lines) {
// this.lines = lines;
// this.position = 0;
// }

// public Graph parse() throws DataParsException {
// List<Node> nodes = new ArrayList<>();
// List<Edge> edges = new ArrayList<>();

// Map<String, Node> mentionedNodes = new LinkedHashMap<>();

// for (String line : this.lines) {
// this.currentLine = line;
// Node subject = parseSubject();
// RelationshipType predicate = parsePredicate();
// Node object = parseObject();
// validateEndOfInput();
// validateSemantics(subject, predicate, object);

// nodes.add(subject);
// edges.add(new Edge(subject, object, predicate));
// edges.add(new Edge(object, subject, predicate));

// mentionedNodes.put(object.toString(), object);
// }

// for (Node node : nodes) {
// if (mentionedNodes.get(node.toString()) == null) {
// throw new DataParsException("semantic Error");
// }
// }

// return new Graph(nodes, edges);
// }

// private Node parseSubject() throws DataParsException {
// return parseNode();
// }

// private Node parseObject() throws DataParsException {
// return parseNode();
// }

// private Node parseNode() throws DataParsException {
// skipWhitespace();
// if (peek() == '(') {
// throw new DataParsException("Unexpected '('");
// }

// String name = consumeName();
// if (peek() == '(') {
// return parseProduct(name);
// }
// return new Category(name.toLowerCase());
// }

// private Product parseProduct(String name) throws DataParsException {
// consume('(');
// consumeIdKeyword();
// consume('=');
// int id = consumeNumber();
// consume(')');
// return new Product(name.toLowerCase(), id);
// }

// private RelationshipType parsePredicate() throws DataParsException {
// skipWhitespace();
// String predicate = consumePredicate();
// return RelationshipType.fromString(predicate);
// }

// private String consumeName() throws DataParsException {
// currentTokenStart = position;
// while (position < currentLine.length() &&
// isValidNameChar(currentLine.charAt(position))) {
// position++;
// }
// if (currentTokenStart == position) {
// throw new DataParsException("Expected name");
// }
// return currentLine.substring(currentTokenStart, position);
// }

// private int consumeNumber() throws DataParsException {
// currentTokenStart = position;
// while (position < currentLine.length() &&
// Character.isDigit(this.currentLine.charAt(position))) {
// position++;
// }
// if (currentTokenStart == position) {
// throw new DataParsException("Expected number");
// }
// return Integer.parseInt(this.currentLine.substring(currentTokenStart,
// position));
// }

// private void consumeIdKeyword() throws DataParsException {
// skipWhitespace();
// if (!this.currentLine.regionMatches(position, "id", 0, 2)) {
// throw new DataParsException("Expected 'id'");
// }
// position += 2;
// skipWhitespace();
// }

// private String consumePredicate() throws DataParsException {
// currentTokenStart = position;
// while (position < this.currentLine.length() &&
// isValidNameChar(this.currentLine.charAt(position))) {
// position++;
// }
// if (currentTokenStart == position) {
// throw new DataParsException("Expected Predicate at: " + this.position +
// this.currentLine);
// }
// return this.currentLine.substring(currentTokenStart, position);
// }

// private void consume(char expected) throws DataParsException {
// skipWhitespace();
// if (peek() != expected) {
// throw new DataParsException("Expected '" + expected + "'");
// }
// position++;
// }

// private char peek() {
// return position < this.currentLine.length() ?
// this.currentLine.charAt(position) : 0;
// }

// private void skipWhitespace() {
// while (position < this.currentLine.length() &&
// Character.isWhitespace(this.currentLine.charAt(position))) {
// position++;
// }
// }

// private void validateEndOfInput() throws DataParsException {
// skipWhitespace();
// if (position < this.currentLine.length()) {
// throw new DataParsException("Unexpected characters at the end of a line");
// }
// }

// private void validateSemantics(Node subject, RelationshipType predicate, Node
// object) throws DataParsException {
// // if (predicate.requiresProducts() && (!(subject instanceof Product) ||
// // !(object instanceof Product))) {
// // throw new DataParsException(predicate + " requires products");
// // }
// }

// private static boolean isValidNameChar(char c) {
// return Character.isLetterOrDigit(c) || c == '-';
// }
// }