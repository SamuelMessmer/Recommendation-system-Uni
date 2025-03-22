package edu.kit.kastel.recommendationsystem.model;

public record DTO(
                Node subject,
                RelationshipType predicate,
                Node object) {

}
