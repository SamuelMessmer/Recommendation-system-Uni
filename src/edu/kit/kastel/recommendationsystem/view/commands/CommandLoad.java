package edu.kit.kastel.recommendationsystem.view.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        List<String> processedLines = new ArrayList<>();
        try {
            ParseResult result = DatabaseParser.parse(parseStringArray(this.dataBasePath));
            processedLines = result.getProcessedLines();
            handle.setGraph(result.getGraph());

            return Result.success(createOutputString(processedLines));
        } catch (DataParsException exception) {
            handle.print(createOutputString(processedLines));

            return Result.error(exception.getMessage());
        }
    }

    private static List<String> parseStringArray(Path pathToDataBaseFile) throws DataParsException {
        try {
            return Files.readAllLines(pathToDataBaseFile);
        } catch (IOException | SecurityException exception) {
            throw new DataParsException(String.format(ERROR_READING_FILE, pathToDataBaseFile));
        }
    }

    private String createOutputString(List<String> processedLines) {
        StringBuilder output = new StringBuilder();

        for (String line : processedLines) {
            output.append(line).append(System.lineSeparator());
        }

        return output.toString().trim();
    }
}