package edu.kit.kastel.recommendationsystem.model;

import java.util.Objects;

public class Product extends Node {

    private final int id;

    public Product(String name, int id) {
        super(name, NodeType.PRODUCT);
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        Product product = (Product) o;
        return this.id == product.id && name.equals(product.name) && this.type == product.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type, this.id);
    }

}
