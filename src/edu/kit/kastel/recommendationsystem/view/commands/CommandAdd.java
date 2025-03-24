package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.RelationshipDTO;
import edu.kit.kastel.recommendationsystem.view.Result;

/**
 * Represents a command to add a relationship to the graph.
 * This command uses a Relationship to specify the relationship and updates the
 * graph accordingly.
 * 
 * @author urrwg
 */
public class CommandAdd implements Command<Graph> {

    private static final String ERROR_RELATIONSHIP_NOT_ADDABLE = "relationship could not be added to the graph";

    private final RelationshipDTO relationship;

    /**
     * Constructs a new CommandAdd instance.
     * 
     * @param relationship the relationship to be added to the graph
     */
    public CommandAdd(RelationshipDTO relationship) {
        this.relationship = relationship;
    }

    @Override
    public Result execute(Graph handle) {
        return handle.addRelationship(this.relationship) ? Result.success()
                : Result.error(ERROR_RELATIONSHIP_NOT_ADDABLE);
    }
}
