package edu.kit.kastel.recommendationsystem.model;

public class Product extends Node {

    private final int id;

    public Product(String name, int id) {
        super(name, true, false);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name + ":" + this.id;
    }
}
