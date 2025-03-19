package edu.kit.kastel.recommendationsystem.model;

import java.util.List;

public record Graph(
    List<Node> nodes,
    List<Edge> edges
) {
    
}
