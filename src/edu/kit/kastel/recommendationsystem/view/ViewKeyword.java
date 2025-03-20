package edu.kit.kastel.recommendationsystem.view;

import edu.kit.kastel.recommendationsystem.view.commands.Command;
import edu.kit.kastel.recommendationsystem.view.commands.CommandLoad;
import edu.kit.kastel.recommendationsystem.view.commands.CommandProvider;
import edu.kit.kastel.recommendationsystem.view.commands.CommandQuit;

/**
 * This enum represents all keywords for commands handling an {@link Communication}.
 * 
 * @author Programmieren-Team
 */
public enum ViewKeyword implements Keyword<Communication> {

    /**
     * The keyword for the {@link CommandLoad load} command.
     */
    LOAD(argumetns -> new CommandLoad(argumetns.parsePath())),
     /**
     * The keyword for the {@link CommandQuit quit} command.
     */
    QUIT(arguments -> new CommandQuit());
    
    private final CommandProvider<Communication> provider;

    ViewKeyword(CommandProvider<Communication> provider) {
        this.provider = provider;
    }
    
    @Override
    public Command<Communication> provide(Arguments arguments) throws InvalidArgumentException {
        return provider.provide(arguments);
    }

    @Override
    public boolean matches(String command) {
        return name().toLowerCase().equals(command);
    }
}
