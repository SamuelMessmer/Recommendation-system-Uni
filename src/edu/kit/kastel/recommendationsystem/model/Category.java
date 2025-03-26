package edu.kit.kastel.recommendationsystem.model;

/**
 * Represents a category node in the recommendation system graph.
 * Categories are used to group products and other categories hierarchically.
 * 
 * @author urrwg
 */
public class Category extends Node {

    /**
     * Constructs a new category with the specified name.
     *
     * @param name the name of the category
     */
    public Category(String name) {
        super(name, NodeType.CATEGORY);
    }
}