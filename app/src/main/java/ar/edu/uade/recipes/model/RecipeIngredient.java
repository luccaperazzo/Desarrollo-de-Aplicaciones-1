package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;

public class RecipeIngredient {
    private String name;
    private String quantity;

    @SerializedName("unit")
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitOfMeasure() {
        return unit;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unit = unitOfMeasure;
    }
}

