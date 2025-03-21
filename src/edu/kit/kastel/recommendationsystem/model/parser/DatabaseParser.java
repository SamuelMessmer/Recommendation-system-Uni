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

    private static final String ERROR_INVALID_STRING_PATTERN = "the given string does not match the provided pattern. line: %s";
    private static final String INVALID_PREDICATE = "the given predicate is incorrect %s";

    private static final Pattern REGEX_LINE_PATTERN = Pattern.compile(
            "^("
                    + "((?<subjectProduct>[a-zA-Z0-9]+)\\s*\\(\\s*id\\s*=\\s*(?<subjectId>[0-9]+)\\s*\\))"
                    + "|"
                    + "(?<subjectCategory>[a-zA-Z0-9]+)"
                    + ")\\s+"
                    + "(?<predicate>contains|contained-in|part-of|has-part|successor-of|predecessor-of)\\s+"
                    + "("
                    + "(?<objectProduct>[a-zA-Z0-9]+)\\s*\\(\\s*id\\s*=\\s*(?<objectId>[0-9]+)\\s*\\)"
                    + "|"
                    + "(?<objectCategory>[a-zA-Z0-9]+)"
                    + ")\\s*$");

    private DatabaseParser() {
        // Utility class
    }

    public static Graph parse(List<String> lines) throws DataParsException {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for (String line : lines) {
            parseLineAndFillList(line, nodes, edges);
        }

        return new Graph(nodes, edges);
    }

    private static void parseLineAndFillList(String line, List<Node> nodes, List<Edge> edges) throws DataParsException {
        Matcher matcher = REGEX_LINE_PATTERN.matcher(line);

        if (!matcher.matches()) {
            throw new DataParsException(String.format(ERROR_INVALID_STRING_PATTERN,
                    line));
        }

        Node subject = parseNode(
                matcher.group("subjectProduct"),
                matcher.group("subjectId"),
                matcher.group("subjectCategory"));

        RelationshipType predicate = parsePredicate(
                matcher.group("predicate"));

        Node object = parseNode(
                matcher.group("objectProduct"),
                matcher.group("objectId"),
                matcher.group("objectCategory"));

        nodes.add(object);
        nodes.add(subject);
        edges.add(new Edge(subject, object, predicate));
        edges.add(new Edge(object, subject, predicate.getReverse()));
    }

    private static Node parseNode(String productName, String productId, String categoryName) throws DataParsException {
        if (productName == null) {
            return new Category(categoryName);
        }
        return new Product(productName, parseInt(productId));
    }

    private static RelationshipType parsePredicate(String relationship) throws DataParsException {
        if (RelationshipType.fromString(relationship) == null) {
            throw new DataParsException(String.format(INVALID_PREDICATE, relationship));
        }
        return RelationshipType.fromString(relationship);
    }

    private static int parseInt(String numberRepresentation) throws DataParsException {
        try {
            return Integer.parseInt(numberRepresentation);
        } catch (NumberFormatException exception) {
            throw new DataParsException(exception.getMessage());
        }
    }
}