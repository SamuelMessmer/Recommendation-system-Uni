package edu.kit.kastel.recommendationsystem.view.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.parser.DataParsException;
import edu.kit.kastel.recommendationsystem.model.parser.DatabaseParser;
import edu.kit.kastel.recommendationsystem.view.Communication;

public class CommandLoad implements Command<Communication> {

    private static final String ERROR_READING_FILE = "Could not read config file: %s";

    private final Path dataBasePath;

    public CommandLoad(Path dataBasePath) {
        this.dataBasePath = dataBasePath;
    }

    @Override
    public Result execute(Communication handle) {
        try {

            Graph graph = new DatabaseParser().parse(parseStringArray(this.dataBasePath));
            handle.setGraph(graph);

            return Result.success();
        } catch (DataParsException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private static List<String> parseStringArray(Path configFilePath) throws DataParsException {
        try {
            return Files.readAllLines(configFilePath);
        } catch (IOException | SecurityException exception) {
            throw new DataParsException(ERROR_READING_FILE);
        }
    }
}