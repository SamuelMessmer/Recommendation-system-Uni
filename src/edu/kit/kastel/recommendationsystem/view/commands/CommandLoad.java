package edu.kit.kastel.recommendationsystem.view.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.model.parser.DataParsException;
import edu.kit.kastel.recommendationsystem.model.parser.DatabaseParser;
import edu.kit.kastel.recommendationsystem.model.parser.ParseResult;
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

    private final Path dataBasePath;

    /**
     * Constructs a new CommandLoad instance.
     * 
     * @param databaseFlag a flag indicating database-related operations (not used
     *                     internally)
     * @param dataBasePath the path to the database file that should be loaded
     */
    public CommandLoad(String databaseFlag, Path dataBasePath) {
        this.dataBasePath = dataBasePath;
    }

    @Override
    public Result execute(Communication handle) {
        String outputString = null;
        try {
            outputString = createOutputString();
            ParseResult result = DatabaseParser.parse(parseStringArray(this.dataBasePath));
            handle.setGraph(result.getGraph());

            return Result.success(createOutputString());
        } catch (DataParsException exception) {
            handle.print(outputString);

            return Result.error(exception.getMessage());
        }
    }

    private static List<String> parseStringArray(Path pathToDataBaseFile) throws DataParsException {
        try {
            return Files.readAllLines(pathToDataBaseFile);
        } catch (IOException | SecurityException exception) {
            throw new DataParsException(null, String.format(ERROR_READING_FILE, pathToDataBaseFile));
        }
    }

    private String createOutputString() throws DataParsException {
        // StringBuilder output = new StringBuilder();

        // for (String line : processedLines) {
        // output.append(line).append(System.lineSeparator());
        // }

        // return output.toString().trim();

        try {
            return Files.readString(this.dataBasePath);
        } catch (IOException exception) {
            throw new DataParsException(null, ERROR_READING_FILE);
        }
    }
}