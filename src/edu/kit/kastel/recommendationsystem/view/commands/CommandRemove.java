package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandRemove implements Command<Graph> {

    private final Node subject;
    private final RelationshipType predicate;
    private final Node object;

    public CommandRemove(DTO dto) {
        this.subject = dto.subject();
        this.predicate = dto.predicate();
        this.object = dto.object();
    }

    @Override
    public Result execute(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Edge comperatorEdge = new Edge(this.subject, this.object, this.predicate);

        if (!edges.contains(comperatorEdge)) {
            return Result.error("Edge was not found");
        }

        graph.removeEdge();
        return Result.success();
    }
}
