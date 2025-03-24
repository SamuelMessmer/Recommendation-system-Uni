package edu.kit.kastel.recommendationsystem.view.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.util.parser.DataParsException;
import edu.kit.kastel.recommendationsystem.util.parser.DatabaseParser;
import edu.kit.kastel.recommendationsystem.view.Communication;

/**
 * Command for loading a database file and initializing the graph structure.
 * Validates file existence and readability before parsing operations, then
 * updates the current Graph, only if the parsing was successfull.
 * 
 * @author urrwg
 */
public class CommandLoad implements Command<Communication> {

    private static final String ERROR_READING_FILE = "Could not read config file: %s";

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
    public Result execute(Communication handle) {
        try {
            handle.print(createOutputString());
            Graph graph = DatabaseParser.parse(parseFiletoStringArray());
            handle.setGraph(graph);

            return Result.success();
        } catch (DataParsException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private List<String> parseFiletoStringArray() throws DataParsException {
        try {
            return Files.readAllLines(this.databasePath);
        } catch (IOException | SecurityException exception) {
            throw new DataParsException(String.format(ERROR_READING_FILE, this.databasePath));
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