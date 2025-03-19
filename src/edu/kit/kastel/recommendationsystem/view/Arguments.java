package edu.kit.kastel.recommendationsystem.view;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.view.commands.Command;

/**
 * This class represents the arguments of a {@link Command}.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public class Arguments {

    private final Graph graph;
    private String[] arguments;
    private int argumentIndex;

    /**
     * Constructs a new instance.
     * 
     * @param game      the game to retrieve tokens from
     * @param arguments the arguments to parse
     */
    public Arguments(Graph graph, String[] arguments) {
        this.graph = graph;
        this.arguments = arguments.clone();
    }

    private String retrieveArgument() {
        return arguments[argumentIndex++];
    }

    /**
     * Returns whether all provided arguments have been consumed.
     * 
     * @return {@code true} if all arguments have been consumed, {@code false}
     *         otherwise
     */
    public boolean isExhausted() {
        return argumentIndex >= arguments.length;
    }
}