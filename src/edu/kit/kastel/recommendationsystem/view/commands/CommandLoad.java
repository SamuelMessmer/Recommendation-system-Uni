package edu.kit.kastel.recommendationsystem.view.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.util.parser.DataParsException;
import edu.kit.kastel.recommendationsystem.util.parser.DatabaseParser;
import edu.kit.kastel.recommendationsystem.view.UserInterface;

/**
 * Command for loading a database file and initializing the graph structure.
 * Validates file existence and readability before parsing operations, then
 * updates the current Graph, only if the parsing was successful.
 * 
 * @author urrwg
 */
public class CommandLoad implements Command<UserInterface> {

    private static final String ERROR_READING_FILE = "could not read database file";

    private final Path databasePath;

    /**
     * Constructs a new CommandLoad instance.
     * 
     * @param databaseFlag a flag indicating database-related operations (not used
     *                     internally)
     * @param databasePath the path to the database file that should be loaded
     */
    public CommandLoad(String databaseFlag, Path databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public Result execute(UserInterface handle) {
        try {
            handle.print(createOutputString());
            Graph graph = DatabaseParser.parse(parseFileToStringArray());
            handle.setGraph(graph);

            return Result.success();
        } catch (DataParsException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private List<String> parseFileToStringArray() throws DataParsException {
        try {
            return Files.readAllLines(this.databasePath);
        } catch (IOException | SecurityException exception) {
            throw new DataParsException(ERROR_READING_FILE);
        }
    }

    private String createOutputString() throws DataParsException {
        try {
            return Files.readString(this.databasePath);
        } catch (IOException | OutOfMemoryError | SecurityException exception) {
            throw new DataParsException(ERROR_READING_FILE);
        }
    }
}