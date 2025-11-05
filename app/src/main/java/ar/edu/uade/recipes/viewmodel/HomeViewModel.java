package ar.edu.uade.recipes.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ar.edu.uade.recipes.model.Recipe;
import ar.edu.uade.recipes.repository.RecipeRepository;

/**
 * ViewModel para manejar la lógica de los fragments de recetas (Explore, MyRecipes, Favorites)
 */
public class HomeViewModel extends AndroidViewModel {

    private final RecipeRepository repository;
    private static final int PAGE_SIZE = 20;

    // LiveData para cada tipo de receta
    private final MutableLiveData<List<Recipe>> publicRecipes = new MutableLiveData<>();
    private final MutableLiveData<List<Recipe>> myRecipes = new MutableLiveData<>();
    private final MutableLiveData<List<Recipe>> favoriteRecipes = new MutableLiveData<>();

    // Estados de carga y error
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasMoreData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFromCache = new MutableLiveData<>();

    // Contadores para paginación
    private int currentSkipPublic = 0;
    private int currentSkipMy = 0;
    private int currentSkipFavorites = 0;

    // Búsquedas actuales
    private String currentSearchPublic = "";
    private String currentSearchMy = "";
    private String currentSearchFavorites = "";

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new RecipeRepository(application.getApplicationContext());
    }

    // Getters para LiveData
    public LiveData<List<Recipe>> getPublicRecipes() {
        return publicRecipes;
    }

    public LiveData<List<Recipe>> getMyRecipes() {
        return myRecipes;
    }

    public LiveData<List<Recipe>> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getHasMoreData() {
        return hasMoreData;
    }

    public LiveData<Boolean> getIsFromCache() {
        return isFromCache;
    }

    public void loadPublicRecipes(String search, boolean reset) {
        if (reset) {
            currentSkipPublic = 0;
            currentSearchPublic = search != null ? search : "";
        } else {
            currentSkipPublic += PAGE_SIZE;
        }

        isLoading.setValue(true);

        repository.getPublicRecipes(currentSearchPublic, currentSkipPublic, PAGE_SIZE,
                new RecipeRepository.RecipeCallback() {
                    @Override
                    public void onSuccess(List<Recipe> recipes, boolean fromCache) {
                        isLoading.postValue(false);
                        isFromCache.postValue(fromCache);

                        if (reset) {
                            publicRecipes.postValue(recipes);
                            hasMoreData.postValue(recipes.size() == PAGE_SIZE);
                        } else {
                            List<Recipe> currentRecipes = publicRecipes.getValue();
                            if (currentRecipes != null) {
                                currentRecipes.addAll(recipes);
                                publicRecipes.postValue(currentRecipes);
                            } else {
                                publicRecipes.postValue(recipes);
                            }
                            hasMoreData.postValue(recipes.size() == PAGE_SIZE);
                        }
                    }

                    @Override
                    public void onError(String message, boolean hasCache) {
                        isLoading.postValue(false);
                        errorMessage.postValue(message);
                        hasMoreData.postValue(false);
                    }
                });
    }

    public void loadMyRecipes(String search, boolean reset) {
        if (reset) {
            currentSkipMy = 0;
            currentSearchMy = search != null ? search : "";
        } else {
            currentSkipMy += PAGE_SIZE;
        }

        isLoading.setValue(true);

        repository.getMyRecipes(currentSearchMy, currentSkipMy, PAGE_SIZE,
                new RecipeRepository.RecipeCallback() {
                    @Override
                    public void onSuccess(List<Recipe> recipes, boolean fromCache) {
                        isLoading.postValue(false);
                        isFromCache.postValue(fromCache);

                        if (reset) {
                            myRecipes.postValue(recipes);
                            hasMoreData.postValue(recipes.size() == PAGE_SIZE);
                        } else {
                            List<Recipe> currentRecipes = myRecipes.getValue();
                            if (currentRecipes != null) {
                                currentRecipes.addAll(recipes);
                                myRecipes.postValue(currentRecipes);
                            } else {
                                myRecipes.postValue(recipes);
                            }
                            hasMoreData.postValue(recipes.size() == PAGE_SIZE);
                        }
                    }

                    @Override
                    public void onError(String message, boolean hasCache) {
                        isLoading.postValue(false);
                        errorMessage.postValue(message);
                        hasMoreData.postValue(false);
                    }
                });
    }

    public void loadFavoriteRecipes(String search, boolean reset) {
        if (reset) {
            currentSkipFavorites = 0;
            currentSearchFavorites = search != null ? search : "";
        } else {
            currentSkipFavorites += PAGE_SIZE;
        }

        isLoading.setValue(true);

        repository.getFavoriteRecipes(currentSearchFavorites, currentSkipFavorites, PAGE_SIZE,
                new RecipeRepository.RecipeCallback() {
                    @Override
                    public void onSuccess(List<Recipe> recipes, boolean fromCache) {
                        isLoading.postValue(false);
                        isFromCache.postValue(fromCache);

                        if (reset) {
                            favoriteRecipes.postValue(recipes);
                            hasMoreData.postValue(recipes.size() == PAGE_SIZE);
                        } else {
                            List<Recipe> currentRecipes = favoriteRecipes.getValue();
                            if (currentRecipes != null) {
                                currentRecipes.addAll(recipes);
                                favoriteRecipes.postValue(currentRecipes);
                            } else {
                                favoriteRecipes.postValue(recipes);
                            }
                            hasMoreData.postValue(recipes.size() == PAGE_SIZE);
                        }
                    }

                    @Override
                    public void onError(String message, boolean hasCache) {
                        isLoading.postValue(false);
                        errorMessage.postValue(message);
                        hasMoreData.postValue(false);
                    }
                });
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
}

