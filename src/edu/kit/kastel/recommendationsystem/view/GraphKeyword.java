package edu.kit.kastel.recommendationsystem.view;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.view.commands.Command;
import edu.kit.kastel.recommendationsystem.view.commands.CommandAdd;
import edu.kit.kastel.recommendationsystem.view.commands.CommandEdges;
import edu.kit.kastel.recommendationsystem.view.commands.CommandExport;
import edu.kit.kastel.recommendationsystem.view.commands.CommandNodes;
import edu.kit.kastel.recommendationsystem.view.commands.CommandProvider;
import edu.kit.kastel.recommendationsystem.view.commands.CommandRecommend;
import edu.kit.kastel.recommendationsystem.view.commands.CommandRemove;

/**
 * This enum represents all keywords for commands handling a
 * {@link Graph}.
 *
 * @author Programmieren-Team
 */
public enum GraphKeyword implements Keyword<Graph> {

    /**
     * The keyword for the {@link CommandAdd add} command.
     */
    ADD(arguments -> new CommandAdd(arguments.parseRelationship())),
    /**
     * The keyword for the {@link CommandEdges edges} command.
     */
    EDGES(arguments -> new CommandEdges()),
    /**
     * The keyword for the {@link CommandExport export} command.
     */
    EXPORT(arguments -> new CommandExport()),
    /**
     * The keyword for the {@link CommandNodes nodes} command.
     */
    NODES(arguments -> new CommandNodes()),
    /**
     * The keyword for the {@link CommandRecommend recommend} command.
     */
    RECOMMEND(arguments -> new CommandRecommend()),
    /**
     * The keyword for the {@link CommandRemove remove} command.
     */
    REMOVE(arguments -> new CommandRemove());

    private final CommandProvider<Graph> provider;

    GraphKeyword(CommandProvider<Graph> provider) {
        this.provider = provider;
    }

    @Override
    public Command<Graph> provide(Arguments arguments) throws InvalidArgumentException {
        return provider.provide(arguments);
    }

    @Override
    public boolean matches(String command) {
        return name().toLowerCase().equals(command);
    }
}
