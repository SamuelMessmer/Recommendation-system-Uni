package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.RelationshipType;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandAdd implements Command<Graph> {

    private final Node subject;
    private final RelationshipType predicate;
    private final Node object;

    public CommandAdd(DTO dto) {
        this.subject = dto.subject();
        this.predicate = dto.predicate();
        this.object = dto.object();
    }

    @Override
    public Result execute(Graph graph) {
        Set<Edge> edges = graph.getEdges();
        Edge newEdge = new Edge(this.subject, this.object, this.predicate);

        if (edges.contains(newEdge)) {
            return Result.error("already exists");
        }

        graph.addEdge(newEdge);
        graph.addNodes(this.object, this.subject);
        return Result.success();
    }

}
