package edu.kit.kastel.recommendationsystem.model;

import java.util.List;

public record DataSet(
    List<Node> nodes,
    List<Edge> edges
) {
    
}
