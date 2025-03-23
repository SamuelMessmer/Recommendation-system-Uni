package edu.kit.kastel.recommendationsystem.model;

import java.util.List;

import edu.kit.kastel.recommendationsystem.model.parser.DataParsException;

public enum RelationshipType {
    CONTAINS("contains"),
    CONTAINED_IN("contained-in"),
    PART_OF("part-of"),
    HAS_PART("has-part"),
    SUCCESSOR_OF("successor-of"),
    PREDECESSOR_OF("predecessor-of");

    private static final List<RelationshipType> CATEGORY_ALLOWED_RELATIONSHIPS = List.of(
            CONTAINS,
            CONTAINED_IN);

    private final String representation;

    RelationshipType(String representation) {
        this.representation = representation;
    }

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

    public static RelationshipType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "contains" -> CONTAINS;
            case "contained-in" -> CONTAINED_IN;
            case "part-of" -> PART_OF;
            case "has-part" -> HAS_PART;
            case "successor-of" -> SUCCESSOR_OF;
            case "predecessor-of" -> PREDECESSOR_OF;
            default -> throw new IllegalArgumentException();
        };
    }

    public static boolean isAllowedBetween(DTO dto) {
        if (dto.subject().isOfType(NodeType.CATEGORY) && dto.object().isOfType(NodeType.CATEGORY)) {
            return CATEGORY_ALLOWED_RELATIONSHIPS.contains(dto.predicate());
        }
        return true;
    }

    @Override
    public String toString() {
        return this.representation;
    }
}
