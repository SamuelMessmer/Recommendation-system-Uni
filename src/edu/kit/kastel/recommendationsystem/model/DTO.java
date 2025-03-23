package edu.kit.kastel.recommendationsystem.model;

public record DTO(
        Node subject,
        RelationshipType predicate,
        Node object) {

    Edge edge() {
        return new Edge(subject, object, predicate);
    }

    Edge reverseEdge() {
        return new Edge(object, subject, predicate.getReverse());
    }
}
