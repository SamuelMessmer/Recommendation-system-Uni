package edu.kit.kastel.recommendationsystem.model;

public class Product extends Node {

    private final int id;
    private final String name;

    public Product(String lowerCase, int id) {
        this.id = id;
        this.name = lowerCase;
    }

    public String toString() {
        return this.name + ", " + this.id;
    }
}
