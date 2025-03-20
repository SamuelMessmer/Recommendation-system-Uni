package edu.kit.kastel.recommendationsystem.view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.view.commands.Command;

/**
 * This class represents the arguments of a {@link Command}.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public class Arguments {

    private static final String CONFIG_FILE_EXTENSION = ".txt";

    private static final String ERROR_INVALID_PATH_TO_DATABASE_FILE = "the provided path is not correct. Should end with .txt";
    private static final String ERROR_TOO_FEW_ARGUMENTS_PATH = "too few provided arguments. Please provide the path";

    private final Graph graph;
    private String[] arguments;
    private int argumentIndex;

    /**
     * Constructs a new instance.
     * 
     * @param graph      the game to retrieve tokens from
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
            throw new InvalidArgumentException(ERROR_TOO_FEW_ARGUMENTS_PATH);
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
     * Returns whether all provided arguments have been consumed.
     * 
     * @return {@code true} if all arguments have been consumed, {@code false}
     *         otherwise
     */
    public boolean isExhausted() {
        return argumentIndex >= arguments.length;
    }
}