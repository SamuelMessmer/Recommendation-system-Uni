package edu.kit.kastel.recommendationsystem.model;

public enum RelationshipType {
    CONTAINS(false),
    CONTAINED_IN(false),
    PART_OF(true),
    HAS_PART(true),
    SUCCESSOR_OF(true),
    PREDECESSOR_OF(true);

    private final boolean productsOnly;

    RelationshipType(boolean productsOnly) {
        this.productsOnly = productsOnly;
    }

    public boolean requiresProducts() {
        return productsOnly;
    }

    public static RelationshipType fromString(String representation) {
        for (RelationshipType type : values()) {
            if(type.equals(representation)){
                return type;
            }
        }
        
        return null;
    }
}
