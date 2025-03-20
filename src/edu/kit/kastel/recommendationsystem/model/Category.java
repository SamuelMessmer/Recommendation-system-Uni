package edu.kit.kastel.recommendationsystem.model;

public class Category extends Node {

    private final String name;

    public Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    } 
}