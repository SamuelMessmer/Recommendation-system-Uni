package edu.kit.kastel.recommendationsystem.model;

public class Product extends Node {

    private final String name;
    private final int id;

    public Product(String lowerCase, int id) {
        this.name = lowerCase;
        this.id = id;

        //TODO Auto-generated constructor stub
    }

    public String toString() {
        return "Penis";
    }
}
