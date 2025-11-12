package ar.edu.uade.recipes.service;

import java.util.List;

import ar.edu.uade.recipes.model.AudioStepsResponse;
import ar.edu.uade.recipes.model.CreateRecipeRequest;
import ar.edu.uade.recipes.model.RatingRequest;
import ar.edu.uade.recipes.model.Recipe;
import ar.edu.uade.recipes.model.RecipeDetail;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
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

    @GET("/api/recipes/{recipe_id}")
    Call<RecipeDetail> getRecipeDetail(@Path("recipe_id") String recipeId);

    @POST("/api/favorites/{recipe_id}")
    Call<Void> addFavorite(@Path("recipe_id") String recipeId);

    @DELETE("/api/favorites/{recipe_id}")
    Call<Void> removeFavorite(@Path("recipe_id") String recipeId);

    @POST("/api/ratings/{recipe_id}")
    Call<Void> addRating(@Path("recipe_id") String recipeId, @Body RatingRequest ratingRequest);

    @PUT("/api/ratings/{recipe_id}")
    Call<Void> updateRating(@Path("recipe_id") String recipeId, @Body RatingRequest ratingRequest);

    @POST("/api/recipes/")
    Call<RecipeDetail> createRecipe(@Body CreateRecipeRequest createRecipeRequest);

    @PUT("/api/recipes/{recipe_id}")
    Call<RecipeDetail> updateRecipe(@Path("recipe_id") String recipeId, @Body CreateRecipeRequest createRecipeRequest);

    @DELETE("/api/recipes/{recipe_id}")
    Call<Void> deleteRecipe(@Path("recipe_id") String recipeId);

    @Multipart
    @POST("/api/recipes/transcribe-audio")
    Call<AudioStepsResponse> transcribeAudio(@Part MultipartBody.Part audioFile);
}

