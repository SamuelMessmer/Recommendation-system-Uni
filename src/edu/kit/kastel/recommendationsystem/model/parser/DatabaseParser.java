package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Edge;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;

public class DatabaseParser {

    private DatabaseParser() {
        // Utility class
    }

    public static Graph parse(List<String> lines) throws DataParsException {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for (String line : lines) {
            DTO dto = LineParser.parse(line);

            nodes.add(dto.subject());
            nodes.add(dto.object());
            edges.add(new Edge(dto.subject(), dto.object(), dto.predicate()));
            edges.add(new Edge(dto.object(), dto.subject(), dto.predicate().getReverse()));
        }

        validateSemantics();

        return new Graph(nodes, edges);
    }

    private static boolean validateSemantics() {
        return false; 
    }

}