package edu.kit.kastel.recommendationsystem.view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.Product;
import edu.kit.kastel.recommendationsystem.model.RelationshipDTO;
import edu.kit.kastel.recommendationsystem.model.parser.DataParsException;
import edu.kit.kastel.recommendationsystem.model.parser.LineParser;
import edu.kit.kastel.recommendationsystem.view.commands.Command;

/**
 * This class represents the arguments of a {@link Command}.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public class Arguments {

    private static final String CONFIG_FILE_EXTENSION = ".txt";
    private static final String DATABASE_FLAG = "database";
    private static final String INPUT_LINE_SEPERATOR = " ";

    private static final String ERROR_INVALID_PATH_TO_DATABASE_FILE = "the provided path is incorrect";
    private static final String ERROR_TOO_FEW_ARGUMENTS = "too few provided arguments.";
    private static final String ERROR_MISSING_DATABASE_FLAG = "the second is missing. Should be: 'database'";

    private final Graph graph;
    private String[] arguments;
    private int argumentIndex;

    /**
     * Constructs a new instance.
     * 
     * @param graph     the game to retrieve tokens from
     * @param arguments the arguments to parse
     */
    public Arguments(Graph graph, String[] arguments) {
        this.graph = graph;
        this.arguments = arguments.clone();
    }

    private String retrieveArgument() {
        return arguments[argumentIndex++];
    }

    /**
     * Parses a database file path from the arguments.
     * <p>
     * Validates that the path points to a readable .txt file.
     * </p>
     *
     * @return the validated database file path
     * @throws InvalidArgumentException if:
     *                                  <ul>
     *                                  <li>No path is provided</li>
     *                                  <li>The path does not point to a valid .txt
     *                                  file</li>
     *                                  </ul>
     */
    public Path parsePath() throws InvalidArgumentException {
        if (isExhausted()) {
            throw new InvalidArgumentException(ERROR_TOO_FEW_ARGUMENTS);
        }

        String argument = retrieveArgument();
        Path databasePath = Paths.get(argument);

        if (databasePath.endsWith(CONFIG_FILE_EXTENSION)
                || !Files.isRegularFile(databasePath)
                || !Files.isReadable(databasePath)) {
            throw new InvalidArgumentException(ERROR_INVALID_PATH_TO_DATABASE_FILE);
        }

        return databasePath;
    }

    /**
     * Parses the database flag from the arguments.
     * 
     * @return the validated database flag
     * @throws InvalidArgumentException if the flag is missing or incorrect
     */
    public String parseDatabaseFlag() throws InvalidArgumentException {
        if (isExhausted()) {
            throw new InvalidArgumentException(ERROR_TOO_FEW_ARGUMENTS);
        }

        String argument = retrieveArgument();

        if (!argument.equals(DATABASE_FLAG)) {
            throw new InvalidArgumentException(ERROR_MISSING_DATABASE_FLAG);
        }

        return DATABASE_FLAG;
    }

    /**
     * Parses a relationship from a command-line input line.
     * 
     * @return the parsed {@link RelationshipDTO}
     * @throws InvalidArgumentException if the line cannot be parsed or is invalid
     */
    public RelationshipDTO parseLine() throws InvalidArgumentException {
        if (isExhausted()) {
            throw new InvalidArgumentException(ERROR_TOO_FEW_ARGUMENTS);
        }

        String line = retrieveLine();

        try {
            RelationshipDTO relationship = LineParser.parse(line);
            return processDTO(relationship);
        } catch (DataParsException exception) {
            throw new InvalidArgumentException(exception.getMessage());
        }
    }

    /**
     * Constructs a single input line from the provided arguments.
     * 
     * @return the combined input line
     */
    public String retrieveLine() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < arguments.length; i++) {
            String argumentString = retrieveArgument();
            builder.append(argumentString).append(INPUT_LINE_SEPERATOR);
        }

        return builder.toString();
    }

    private RelationshipDTO processDTO(RelationshipDTO relationship) throws DataParsException {
        Set<Node> nodes = graph.getNodes();

        Node subject = getOrRegisterNode(relationship.subject(), nodes);
        Node object = getOrRegisterNode(relationship.object(), nodes);

        return new RelationshipDTO(subject, relationship.predicate(), object);
    }

    private Node getOrRegisterNode(Node node, Set<Node> nodes) throws DataParsException {
        for (Node existingNode : nodes) {
            if (existingNode.equals(node)) {
                return existingNode;
            }
        }
        
        if (validateNodePlacement(node, nodes)) {
            graph.addNode(node);
            return node;
        }
        throw new DataParsException(null, " Im your Grandfather not your commanding Officers");
    }

    private boolean validateNodePlacement(Node node, Set<Node> nodes) {
        for (Node existingNode : nodes) {
            if (existingNode.getName().equals(node.getName())) {
                return false;
            }
            if (existingNode instanceof Product && node instanceof Product) {
                if (((Product) existingNode).getId() == ((Product) node).getId()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns whether all provided arguments have been consumed.
     * 
     * @return {@code true} if all arguments have been consumed, {@code false}
     *         otherwise
     */
    public boolean isExhausted() {
        return argumentIndex >= arguments.length;
    }
}