package edu.kit.kastel.recommendationsystem.model;

public abstract class Node {

    protected final String name;
    private boolean isProduct;
    private boolean isCategory;

    public Node(String name) {
        this.name = name;
        this.isProduct = false;
    }

    public Node(String name, boolean isProduct) {
        this.name = name;
        this.isProduct = isProduct;
    }

    public Edge getAllEdges() {
        return null;
    }

    public boolean isProduct() {
        return this.isProduct;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
