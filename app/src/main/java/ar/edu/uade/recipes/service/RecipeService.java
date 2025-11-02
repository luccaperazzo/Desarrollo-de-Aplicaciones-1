package ar.edu.uade.recipes.service;

import java.util.List;

import ar.edu.uade.recipes.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeService {

    @GET("/api/recipes/public")
    Call<List<Recipe>> getPublicRecipes(
            @Query("search") String search,
            @Query("skip") int skip,
            @Query("limit") int limit
    );

    @GET("/api/recipes/my-recipes")
    Call<List<Recipe>> getMyRecipes(
            @Query("search") String search,
            @Query("skip") int skip,
            @Query("limit") int limit
    );

    @GET("/api/recipes/favorites")
    Call<List<Recipe>> getFavoriteRecipes(
            @Query("search") String search,
            @Query("skip") int skip,
            @Query("limit") int limit
    );
}

