package ar.edu.uade.recipes.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.edu.uade.recipes.database.AppDatabase;
import ar.edu.uade.recipes.database.RecipeDao;
import ar.edu.uade.recipes.model.Recipe;
import ar.edu.uade.recipes.service.RecipeService;
import ar.edu.uade.recipes.service.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepository {

    private final RecipeDao recipeDao;
    private final RecipeService recipeService;
    private final Context context;
    private final Executor executor; // para operaciones de Room en background thread

    public RecipeRepository(Context context) {
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(context);
        this.recipeDao = database.recipeDao();
        this.recipeService = RetrofitClient.getRetrofitInstance(context).create(RecipeService.class);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public interface RecipeCallback {
        void onSuccess(List<Recipe> recipes, boolean fromCache);
        void onError(String message, boolean hasCache);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getPublicRecipes(String search, int skip, int limit, RecipeCallback callback) {
        if (isNetworkAvailable()) {
            // Llamada al backend
            recipeService.getPublicRecipes(search, skip, limit)
                    .enqueue(new Callback<List<Recipe>>() {
                        @Override
                        public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Recipe> recipes = response.body();
                                // NO guardamos recetas públicas
                                callback.onSuccess(recipes, false);
                            } else {
                                // Error del servidor
                                callback.onError("Error del servidor", false);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Recipe>> call, Throwable t) {
                            // Error de red
                            callback.onError("Error de conexión", false);
                        }
                    });
        } else {
            // Sin conexión, no hay cache para públicas
            callback.onError("Sin conexión", false);
        }
    }

    public void getMyRecipes(String search, int skip, int limit, RecipeCallback callback) {
        if (isNetworkAvailable()) {
            recipeService.getMyRecipes(search, skip, limit)
                    .enqueue(new Callback<List<Recipe>>() {
                        @Override
                        public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Recipe> recipes = response.body();

                                executor.execute(() -> {
                                    long timestamp = System.currentTimeMillis();
                                    for (Recipe recipe : recipes) {
                                        recipe.setRecipeType("my_recipe");
                                        recipe.setLastUpdated(timestamp);
                                    }
                                    if (skip == 0) {
                                        recipeDao.deleteRecipesByType("my_recipe");
                                    }
                                    recipeDao.insertRecipes(recipes);
                                });

                                callback.onSuccess(recipes, false);
                            } else {
                                loadFromDatabase("my_recipe", search, callback, "Error del servidor");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Recipe>> call, Throwable t) {
                            loadFromDatabase("my_recipe", search, callback, "Error de conexión");
                        }
                    });
        } else {
            loadFromDatabase("my_recipe", search, callback, "Sin conexión");
        }
    }

    public void getFavoriteRecipes(String search, int skip, int limit, RecipeCallback callback) {
        if (isNetworkAvailable()) {
            recipeService.getFavoriteRecipes(search, skip, limit)
                    .enqueue(new Callback<List<Recipe>>() {
                        @Override
                        public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Recipe> recipes = response.body();

                                executor.execute(() -> {
                                    long timestamp = System.currentTimeMillis();
                                    for (Recipe recipe : recipes) {
                                        recipe.setRecipeType("favorite");
                                        recipe.setLastUpdated(timestamp);
                                    }
                                    if (skip == 0) {
                                        recipeDao.deleteRecipesByType("favorite");
                                    }
                                    recipeDao.insertRecipes(recipes);
                                });

                                callback.onSuccess(recipes, false);
                            } else {
                                loadFromDatabase("favorite", search, callback, "Error del servidor");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Recipe>> call, Throwable t) {
                            loadFromDatabase("favorite", search, callback, "Error de conexión");
                        }
                    });
        } else {
            loadFromDatabase("favorite", search, callback, "Sin conexión");
        }
    }

    private void loadFromDatabase(String type, String search, RecipeCallback callback, String errorMessage) {
        executor.execute(() -> {
            List<Recipe> recipes;
            if (search == null || search.isEmpty()) {
                recipes = recipeDao.getRecipesByType(type);
            } else {
                recipes = recipeDao.searchRecipesByType(type, search);
            }

            if (recipes != null && !recipes.isEmpty()) {
                // Hay datos en cache, mostrarlos
                callback.onSuccess(recipes, true);
            } else {
                // No hay datos en cache
                callback.onError(errorMessage, false);
            }
        });
    }
}
