package edu.kit.kastel.recommendationsystem.model;

/**
 * Represents a relationship between two nodes (subject and object) with a
 * specific predicate.
 * This class serves as a data transfer object (DTO) to encapsulate the
 * relationship information and provides methods to create both the primary edge
 * and its reverse edge.
 * 
 * @param subject
 * @param predicate
 * @param object
 * @author urrwg
 */
public record RelationshipDTO(
        Node subject,
        RelationshipType predicate,
        Node object) {

    /**
     * Creates and returns the primary edge representing the relationship.
     * The edge is directed from the subject to the object with the specified
     * predicate.
     *
     * @return the primary edge representing the relationship
     */
    public Edge edge() {
        return new Edge(subject, object, predicate);
    }

    /**
     * Creates and returns the reverse edge of the relationship.
     * The edge is directed from the object to the subject with the reverse
     * predicate.
     *
     * @return the reverse edge of the relationship
     */
    public Edge reverseEdge() {
        return new Edge(object, subject, predicate.getReverse());
    }
}