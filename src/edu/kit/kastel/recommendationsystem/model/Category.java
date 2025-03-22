package edu.kit.kastel.recommendationsystem.model;

import java.util.Objects;

public class Category extends Node {

    public Category(String name) {
        super(name, NodeType.CATEGORY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        return name.equals(category.name) && this.type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

}