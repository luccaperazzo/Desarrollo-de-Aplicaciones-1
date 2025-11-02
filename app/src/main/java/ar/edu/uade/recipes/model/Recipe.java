package ar.edu.uade.recipes.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey
    @NonNull
    private String id;

    private String title;

    @ColumnInfo(name = "author_name")
    @SerializedName("author_name")
    private String authorName;

    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    private String imageUrl;

    private String recipeType; // "public", "my_recipe", "favorite"
    private long lastUpdated;

    public Recipe() {
    }

    @Ignore
    public Recipe(String id, String title, String authorName, String imageUrl) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
