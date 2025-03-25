package edu.kit.kastel.recommendationsystem.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the type of relationship between two nodes in the graph.
 * Each relationship type has a reverse relationship, and some relationships are
 * only allowed between specific node types.
 * 
 * @author urrwg
 */
public enum RelationshipType {
    /**
     * Represents a "contains" relationship where one node contains another.
     */
    CONTAINS("contains"),

    /**
     * Represents a "contained-in" relationship where one node is contained within
     * another.
     */
    CONTAINED_IN("contained-in"),

    /**
     * Represents a "part-of" relationship where one node is a part of another.
     */
    PART_OF("part-of"),

    /**
     * Represents a "has-part" relationship where one node has a part that is
     * another node.
     */
    HAS_PART("has-part"),

    /**
     * Represents a "successor-of" relationship where one node is the successor of
     * another.
     */
    SUCCESSOR_OF("successor-of"),

    /**
     * Represents a "predecessor-of" relationship where one node is the predecessor
     * of another.
     */
    PREDECESSOR_OF("predecessor-of");

    private static final List<RelationshipType> CATEGORY_ALLOWED_RELATIONSHIPS = List.of(
            CONTAINS,
            CONTAINED_IN);

    private static final Map<String, RelationshipType> ENUM_REPRESENTATIONS = new HashMap<>();
    static {
        for (RelationshipType type : values()) {
            ENUM_REPRESENTATIONS.put(type.representation, type);
        }
    }

    private final String representation;

    RelationshipType(String representation) {
        this.representation = representation;
    }

    /**
     * Returns the reverse relationship type for this relationship.
     *
     * @return the reverse relationship type
     */
    public RelationshipType getReverse() {
        return switch (this) {
            case CONTAINS -> CONTAINED_IN;
            case CONTAINED_IN -> CONTAINS;
            case PART_OF -> HAS_PART;
            case HAS_PART -> PART_OF;
            case SUCCESSOR_OF -> PREDECESSOR_OF;
            case PREDECESSOR_OF -> SUCCESSOR_OF;
        };
    }

    /**
     * Converts a string representation of a relationship type to the corresponding
     * enum value.
     *
     * @param representationString the string representation of the relationship
     *                             type
     * @return the corresponding {@link RelationshipType}
     */
    public static RelationshipType fromString(String representationString) {
        return ENUM_REPRESENTATIONS.get(representationString.toLowerCase());
    }

    /**
     * Checks if the relationship is allowed between the specified nodes.
     *
     * @param relationship the relationship to check
     * @return {@code true} if the relationship is allowed, {@code false} otherwise
     */
    public static boolean isAllowedBetween(RelationshipDTO relationship) {
        Node startNode = relationship.subject();
        Node endNode = relationship.object();
        RelationshipType relationshipType = relationship.predicate();

        if (startNode.isOfType(NodeType.CATEGORY) || endNode.isOfType(NodeType.CATEGORY)) {
            return CATEGORY_ALLOWED_RELATIONSHIPS.contains(relationshipType);
        }

        if (startNode.isOfType(NodeType.PRODUCT) && endNode.isOfType(NodeType.PRODUCT)) {
            return !CATEGORY_ALLOWED_RELATIONSHIPS.contains(relationshipType);
        }
        
        return true;
    }

    @Override
    public String toString() {
        return this.representation;
    }
}
