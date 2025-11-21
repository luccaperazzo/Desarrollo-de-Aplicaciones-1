package ar.edu.uade.recipes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.model.RecipeDetail;
import ar.edu.uade.recipes.model.RatingRequest;
import ar.edu.uade.recipes.service.RecipeService;
import ar.edu.uade.recipes.service.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para manejar la lógica del detalle de una receta
 */
public class RecipeDetailViewModel extends AndroidViewModel {

    private final RecipeService recipeService;
    private final MutableLiveData<RecipeDetail> recipeDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> favoriteUpdated = new MutableLiveData<>();
    private final MutableLiveData<String> favoriteMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ratingUpdated = new MutableLiveData<>();
    private final MutableLiveData<String> ratingMessage = new MutableLiveData<>();

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
        recipeService = RetrofitClient.getRetrofitInstance(application.getApplicationContext())
                .create(RecipeService.class);
    }

    // Getters para LiveData
    public LiveData<RecipeDetail> getRecipeDetail() {
        return recipeDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getFavoriteUpdated() {
        return favoriteUpdated;
    }

    public LiveData<String> getFavoriteMessage() {
        return favoriteMessage;
    }

    public LiveData<Boolean> getRatingUpdated() {
        return ratingUpdated;
    }

    public LiveData<String> getRatingMessage() {
        return ratingMessage;
    }

    public void loadRecipeDetail(String recipeId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        recipeService.getRecipeDetail(recipeId).enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                isLoading.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    recipeDetail.postValue(response.body());
                } else {
                    errorMessage.postValue(getApplication().getString(R.string.recipe_detail_error_load));
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(getApplication().getString(R.string.recipe_detail_error_connection));
            }
        });
    }

    public void toggleFavorite(String recipeId, boolean isCurrentlyFavorite) {
        Call<Void> call = isCurrentlyFavorite
                ? recipeService.removeFavorite(recipeId)
                : recipeService.addFavorite(recipeId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    boolean newFavoriteState = !isCurrentlyFavorite;

                    // Actualizar el estado local del RecipeDetail
                    RecipeDetail current = recipeDetail.getValue();
                    if (current != null) {
                        current.setFavorite(newFavoriteState);
                        recipeDetail.postValue(current);
                    }

                    // Setear el mensaje según el NUEVO estado
                    String message = newFavoriteState
                            ? getApplication().getString(R.string.recipe_detail_favorite_added)
                            : getApplication().getString(R.string.recipe_detail_favorite_removed);
                    favoriteMessage.postValue(message);

                    // Notificar que se actualizó después de establecer el mensaje
                    favoriteUpdated.postValue(true);
                } else {
                    favoriteUpdated.postValue(false);
                    favoriteMessage.postValue(getApplication().getString(R.string.error_state_message));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                favoriteUpdated.postValue(false);
                favoriteMessage.postValue(getApplication().getString(R.string.recipe_detail_error_connection));
            }
        });
    }

    public void updateRating(String recipeId, int newRating, int currentRating) {
        RatingRequest ratingRequest = new RatingRequest(newRating);

        Call<Void> call;
        if (currentRating == 0) {
            call = recipeService.addRating(recipeId, ratingRequest);
        } else {
            call = recipeService.updateRating(recipeId, ratingRequest);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Actualizar el rating local
                    RecipeDetail current = recipeDetail.getValue();
                    if (current != null) {
                        current.setUserRating(newRating);
                        recipeDetail.postValue(current);
                    }

                    // Recargar para obtener el nuevo avg_rating
                    loadRecipeDetail(recipeId);

                    // Notificar que se actualizó
                    String ratingMsg = currentRating == 0
                            ? getApplication().getString(R.string.recipe_detail_rating_added)
                            : getApplication().getString(R.string.recipe_detail_rating_updated);
                    ratingMessage.postValue(ratingMsg);
                    ratingUpdated.postValue(true);
                } else {
                    ratingUpdated.postValue(false);
                    ratingMessage.postValue(getApplication().getString(R.string.recipe_detail_rating_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ratingUpdated.postValue(false);
                ratingMessage.postValue(getApplication().getString(R.string.recipe_detail_error_connection));
            }
        });
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public void deleteRecipe(String recipeId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        recipeService.deleteRecipe(recipeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    errorMessage.postValue("DELETE_SUCCESS");
                } else {
                    errorMessage.postValue(getApplication().getString(R.string.delete_recipe_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(getApplication().getString(R.string.recipe_detail_error_connection));
            }
        });
    }

    public void resetUpdateStates() {
        favoriteUpdated.setValue(false);
        ratingUpdated.setValue(false);
    }
}

