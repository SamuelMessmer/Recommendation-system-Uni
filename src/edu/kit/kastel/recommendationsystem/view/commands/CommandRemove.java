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
    public Result execute(Graph handle) {
        Edge comperatorEdge = new Edge(subject, object, predicate);

        List<Edge> edges = new ArrayList<>();
        edges.addAll(handle.getEdges());

        List<Node> nodes = new ArrayList<>();
        nodes.addAll(handle.getNodes());

        for (Edge edge : edges) {
            String subjectName = edge.getStartNode().getName();
            String objectName = edge.getEndNode().getName();

            if (subjectName.equals(this.subject.getName())
                    && objectName.equals(this.object.getName())
                    && edge.getRelationship() == this.predicate) {
                handle.getEdges().remove(edge);
                removeNode(nodes);
                return Result.success();
            }
        }

        return Result.error("Edge was not found");
    }

    private void removeNode(List<Node> nodes) {
        for (Node node : nodes) {
            String nodeName = node.getName();

            if (nodeName.equals(this.subject.getName())) {
                int nodeIndex = nodes.indexOf(node);
                nodes.remove(nodeIndex);
            }
        }
    }
}
