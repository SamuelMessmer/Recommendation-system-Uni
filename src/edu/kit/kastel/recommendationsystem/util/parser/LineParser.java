package edu.kit.kastel.recommendationsystem.util.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.kastel.recommendationsystem.model.Category;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipDTO;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;

/**
 * A utility class for parsing lines from a database file syntax into
 * {@link RelationshipDTO} objects.
 * This class uses regular expressions to validate and extract information from
 * input lines, ensuring they conform to the expected format for relationships
 * between nodes.
 * Possible input looks like: centos7 ( id = 107 ) contained-in operatingsystem
 * 
 * @author urrwg
 */
public final class LineParser {

    private static final String REGEX_GROUP_SUBJECT_PRODUCT = "subjectProduct";
    private static final String REGEX_GROUP_SUBJECT_ID = "subjectId";
    private static final String REGEX_GROUP_SUBJECT_CATEGORY = "subjectCategory";
    private static final String REGEX_GROUP_PREDICATE = "predicate";
    private static final String REGEX_GROUP_OBJECT_PRODUCT = "objectProduct";
    private static final String REGEX_GROUP_OBJECT_ID = "objectId";
    private static final String REGEX_GROUP_OBJECT_CATEGORY = "objectCategory";
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

    private static final String ERROR_INVALID_STRING_PATTERN = "the given string does not match the provided pattern. line: %s";
    private static final String ERROR_INVALID_PREDICATE = "the given predicate is incorrect: %s";
    private static final String ERROR_INVALID_ID_NUMBER = "please provide a valid number instead of: %s";

    private LineParser() {
        // Utility class
    }

    /**
     * Parses a line from the database file into a {@link RelationshipDTO} object.
     * The line must conform to the expected format for relationships between nodes,
     * which is stated in the BNF-grammatic.
     * 
     * @param line the input line to parse
     * @return a {@link RelationshipDTO} representing the parsed relationship
     * @throws DataParsException if the line does not match the expected format or
     *                           contains invalid data
     */
    public static RelationshipDTO parse(String line) throws DataParsException {
        Matcher matcher = REGEX_LINE_PATTERN.matcher(line);

        if (!matcher.matches()) {
            throw new DataParsException(String.format(ERROR_INVALID_STRING_PATTERN, line));
        }

        Node subject = parseNode(
                matcher.group(REGEX_GROUP_SUBJECT_PRODUCT),
                matcher.group(REGEX_GROUP_SUBJECT_ID),
                matcher.group(REGEX_GROUP_SUBJECT_CATEGORY));

        RelationshipType predicate = parsePredicate(
                matcher.group(REGEX_GROUP_PREDICATE));

        Node object = parseNode(
                matcher.group(REGEX_GROUP_OBJECT_PRODUCT),
                matcher.group(REGEX_GROUP_OBJECT_ID),
                matcher.group(REGEX_GROUP_OBJECT_CATEGORY));

        return new RelationshipDTO(subject, predicate, object);
    }

    private static Node parseNode(String productName, String productId, String categoryName) throws DataParsException {
        if (productName == null) {
            return new Category(categoryName.toLowerCase());
        }
        return new Product(productName.toLowerCase(), parseInt(productId));
    }

    private static RelationshipType parsePredicate(String type) throws DataParsException {
        if (RelationshipType.fromString(type) == null) {
            throw new DataParsException(String.format(ERROR_INVALID_PREDICATE, type));
        }
        return RelationshipType.fromString(type);
    }

    private static int parseInt(String numberString) throws DataParsException {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException exception) {
            throw new DataParsException(String.format(ERROR_INVALID_ID_NUMBER, numberString));
        }
    }
}