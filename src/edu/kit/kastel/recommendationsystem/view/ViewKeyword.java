package edu.kit.kastel.recommendationsystem.view;

import edu.kit.kastel.recommendationsystem.view.commands.Command;
import edu.kit.kastel.recommendationsystem.view.commands.CommandLoad;
import edu.kit.kastel.recommendationsystem.view.commands.CommandProvider;
import edu.kit.kastel.recommendationsystem.view.commands.CommandQuit;

/**
 * This enum represents all keywords for commands handling an {@link UserInterface}.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public enum ViewKeyword implements Keyword<UserInterface> {

    /**
     * The keyword for the {@link CommandLoad load} command.
     */
    LOAD(arguments -> new CommandLoad(arguments.parseDatabaseFlag(), arguments.parsePath())),
     /**
     * The keyword for the {@link CommandQuit quit} command.
     */
    QUIT(arguments -> new CommandQuit());
    
    private final CommandProvider<UserInterface> provider;

    ViewKeyword(CommandProvider<UserInterface> provider) {
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
