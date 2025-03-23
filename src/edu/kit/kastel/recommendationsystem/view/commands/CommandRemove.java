package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.RelationshipDTO;
import edu.kit.kastel.recommendationsystem.view.Result;

/**
 * Command implementation for removing relationships from the graph.
 * Handles reverse relationship cleanup and orphan node removal.
 * 
 * @author urrwg
 */
public class CommandRemove implements Command<Graph> {

    private static final String ERROR_EDGE_NOT_REMOVEABLE = "Edge can not be removed";

    private final RelationshipDTO relationship;

    /**
     * Constructs a new CommandRemove instance.
     * 
     * @param relationship the relationship to be removed from the graph
     */
    public CommandRemove(RelationshipDTO relationship) {
        this.relationship = relationship;
    }

    @Override
    public Result execute(Graph handle) {
        return handle.removeEdge(this.relationship) ? Result.success() : Result.error(ERROR_EDGE_NOT_REMOVEABLE);
    }
}
