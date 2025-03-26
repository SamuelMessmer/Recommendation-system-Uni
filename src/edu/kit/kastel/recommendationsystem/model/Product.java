package edu.kit.kastel.recommendationsystem.model;

import java.util.Objects;

/**
 * Represents a product node in the graph.
 * A product has a unique ID and a name, and it extends the base {@link Node}
 * class.
 * 
 * @author urrwg
 */
public class Product extends Node {

    private static final String PRODUCT_OUTPUT_FORMAT = "%s:%s";

    private final int id;

    /**
     * Constructs a new product with the specified name and ID.
     *
     * @param name the name of the product
     * @param id   the unique identification number (id) of the product
     */
    public Product(String name, int id) {
        super(name, NodeType.PRODUCT);
        this.id = id;
    }

    /**
     * Returns the unique identification number (id) of the product.
     *
     * @return the product ID
     */
    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format(PRODUCT_OUTPUT_FORMAT, this.name, this.id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Product)) {
            return false;
        }
        Product product = (Product) object;
        return this.id == product.id && name.equals(product.name) && this.type == product.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type, this.id);
    }
}