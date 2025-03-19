package edu.kit.kastel.recommendationsystem.view;

import edu.kit.kastel.recommendationsystem.model.DataSet;
import edu.kit.kastel.recommendationsystem.view.commands.Command;
import edu.kit.kastel.recommendationsystem.view.commands.CommandNodes;
import edu.kit.kastel.recommendationsystem.view.commands.CommandProvider;

/**
 * This enum represents all keywords for commands handling a
 * {@link DataSet}.
 *
 * @author Programmieren-Team
 */
public enum GameKeyword implements Keyword<DataSet> {

    /**
     * The keyword for the {@link CommandMove move} command.
     */
    NODES(arguments -> new CommandNodes()); 

    private final CommandProvider<DataSet> provider;

    GameKeyword(CommandProvider<DataSet> provider) {
        this.provider = provider;
    }

    @Override
    public Command<DataSet> provide(Arguments arguments) throws InvalidArgumentException {
        return provider.provide(arguments);
    }

    @Override
    public boolean matches(String command) {
        return name().toLowerCase().equals(command);
    }
}
