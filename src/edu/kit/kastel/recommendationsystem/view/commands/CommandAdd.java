package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.List;

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
    public Result execute(Graph handle) {
        List<Edge> edges = handle.edges();

        for (Edge edge : edges) {
            String subjectName = edge.getStartNode().getName();
            String objectName = edge.getEndNode().getName();

            if (subjectName.equals(this.subject.getName())
                    && objectName.equals(this.object.getName())
                    && edge.getRelationship() == this.predicate) {
                return Result.error("Edge is already existing");
            }
        }

        Edge newEdge = new Edge(this.subject, this.object, this.predicate);
        edges.add(newEdge);
        return Result.success();
    }

}
