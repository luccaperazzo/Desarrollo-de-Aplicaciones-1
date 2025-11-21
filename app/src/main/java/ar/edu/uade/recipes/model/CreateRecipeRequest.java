package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateRecipeRequest {
    private String title;
    private String description;
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("image_url")
    private String imageUrl;

    public CreateRecipeRequest(String title, String description, List<RecipeIngredient> ingredients,
                               List<RecipeStep> steps, String imageUrl) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.isPublic = true; // Siempre true
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

