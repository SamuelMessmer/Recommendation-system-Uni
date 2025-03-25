package edu.kit.kastel.recommendationsystem.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Category)) {
            return false;
        }
        Category category = (Category) object;
        return name.equals(category.name) && this.type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

}