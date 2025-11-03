package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;

public class RecipeStep {
    private int order;
    private String description;

    public RecipeStep() {
    }

    public RecipeStep(int order, String description) {
        this.order = order;
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

