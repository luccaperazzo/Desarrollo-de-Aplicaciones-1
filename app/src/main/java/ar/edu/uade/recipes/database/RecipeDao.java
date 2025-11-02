package ar.edu.uade.recipes.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ar.edu.uade.recipes.model.Recipe;

@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipes(List<Recipe> recipes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Query("SELECT * FROM recipes WHERE recipeType = :type ORDER BY lastUpdated DESC")
    List<Recipe> getRecipesByType(String type);

    @Query("SELECT * FROM recipes WHERE recipeType = :type AND (title LIKE '%' || :search || '%' OR author_name LIKE '%' || :search || '%') ORDER BY lastUpdated DESC")
    List<Recipe> searchRecipesByType(String type, String search);

    @Query("DELETE FROM recipes WHERE recipeType = :type")
    void deleteRecipesByType(String type);

    @Query("DELETE FROM recipes")
    void deleteAllRecipes();
}

