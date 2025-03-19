package edu.kit.kastel.recommendationsystem.view;

import edu.kit.kastel.recommendationsystem.view.commands.Command;
import edu.kit.kastel.recommendationsystem.view.commands.CommandLoad;
import edu.kit.kastel.recommendationsystem.view.commands.CommandNodes;
import edu.kit.kastel.recommendationsystem.view.commands.CommandProvider;

/**
 * This enum represents all keywords for commands handling a
 * {@link UserInterface}.
 *
 * @author Programmieren-Team
 */
public enum GameKeyword implements Keyword<UserInterface> {

    /**
     * The keyword for the {@link CommandPlace place} command.
     */
    LOAD(argumetns -> new CommandLoad()),
    /**
     * The keyword for the {@link CommandMove move} command.
     */
    NODES(arguments -> new CommandNodes());

    private final CommandProvider<UserInterface> provider;

    GameKeyword(CommandProvider<UserInterface> provider) {
        this.provider = provider;
    }

    @Override
    public Command<UserInterface> provide(Arguments arguments) throws InvalidArgumentException {
        return provider.provide(arguments);
    }

    @Override
    public boolean matches(String command) {
        return name().toLowerCase().equals(command);
    }
}
