package edu.kit.kastel.recommendationsystem.model;

/**
 * Represents the type of a node in the recommendation system graph.
 * A node can be either a product or a category.
 * 
 * @author urrwg
 */
public enum NodeType {
    /**
     * Represents a product.
     */
    PRODUCT,
    /**
     * Represents a category.
     */
    CATEGORY;
}