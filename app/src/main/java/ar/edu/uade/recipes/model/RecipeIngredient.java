package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;

public class RecipeIngredient {
    private String name;
    private double quantity;

    @SerializedName("unit")
    private String unitOfMeasure;

    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, double quantity, String unitOfMeasure) {
        this.name = name;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}

