package ar.edu.uade.recipes;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import ar.edu.uade.recipes.model.RatingRequest;
import ar.edu.uade.recipes.model.RecipeDetail;
import ar.edu.uade.recipes.model.RecipeIngredient;
import ar.edu.uade.recipes.model.RecipeStep;
import ar.edu.uade.recipes.service.RecipeService;
import ar.edu.uade.recipes.service.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_RECIPE_TITLE = "recipe_title";

    private MaterialToolbar toolbar;
    private ImageView ivRecipeImage;
    private TextView tvRecipeTitle;
    private TextView tvRecipeAuthor;
    private TextView tvRecipeDescription;
    private TextView tvAvgRating;
    private ImageButton btnFavorite;
    private LinearLayout ingredientsContainer;
    private LinearLayout stepsContainer;
    private MaterialButton btnAddToCart;
    private View loadingOverlay;
    private View contentContainer;

    // Estrellas de rating promedio
    private ImageView avgStar1, avgStar2, avgStar3, avgStar4, avgStar5;

    // Estrellas de rating del usuario
    private ImageView userStar1, userStar2, userStar3, userStar4, userStar5;

    // Contenedores de rating
    private LinearLayout avgRatingContainer;
    private LinearLayout userRatingContainer;

    private RecipeService recipeService;
    private String recipeId;
    private RecipeDetail currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        toolbar = findViewById(R.id.toolbar);
        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        tvRecipeTitle = findViewById(R.id.tvRecipeTitle);
        tvRecipeAuthor = findViewById(R.id.tvRecipeAuthor);
        tvRecipeDescription = findViewById(R.id.tvRecipeDescription);
        tvAvgRating = findViewById(R.id.tvAvgRating);
        btnFavorite = findViewById(R.id.btnFavorite);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        stepsContainer = findViewById(R.id.stepsContainer);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        contentContainer = findViewById(R.id.contentContainer);

        // Inicializar estrellas de rating promedio
        avgStar1 = findViewById(R.id.avgStar1);
        avgStar2 = findViewById(R.id.avgStar2);
        avgStar3 = findViewById(R.id.avgStar3);
        avgStar4 = findViewById(R.id.avgStar4);
        avgStar5 = findViewById(R.id.avgStar5);

        // Inicializar estrellas de rating del usuario
        userStar1 = findViewById(R.id.userStar1);
        userStar2 = findViewById(R.id.userStar2);
        userStar3 = findViewById(R.id.userStar3);
        userStar4 = findViewById(R.id.userStar4);
        userStar5 = findViewById(R.id.userStar5);

        // Inicializar contenedores de rating
        avgRatingContainer = findViewById(R.id.avgRatingStarsContainer).getParent() instanceof LinearLayout
            ? (LinearLayout) ((LinearLayout) findViewById(R.id.avgRatingStarsContainer).getParent()).getParent()
            : null;
        userRatingContainer = findViewById(R.id.userRatingStarsContainer).getParent() instanceof LinearLayout
            ? (LinearLayout) findViewById(R.id.userRatingStarsContainer).getParent()
            : null;

        // Configurar toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Obtener datos del intent
        recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        String recipeTitle = getIntent().getStringExtra(EXTRA_RECIPE_TITLE);

        // Mostrar título inmediatamente si está disponible
        if (recipeTitle != null && !recipeTitle.isEmpty()) {
            tvRecipeTitle.setText(recipeTitle);
            toolbar.setTitle(recipeTitle);
        }

        // Inicializar servicio
        recipeService = RetrofitClient.getRetrofitInstance(this).create(RecipeService.class);

        // Configurar botón de carrito (mock)
        btnAddToCart.setOnClickListener(v -> {
            // TODO: implementar agregar al carrito
            Toast.makeText(this, R.string.recipe_detail_cart_mock, Toast.LENGTH_SHORT).show();
        });

        // Configurar botón de favoritos
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Cargar datos de la receta
        loadRecipeDetail();
    }

    private void loadRecipeDetail() {
        showLoading(true);

        recipeService.getRecipeDetail(recipeId).enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentRecipe = response.body();
                    displayRecipeDetail(currentRecipe);
                } else {
                    showErrorDialog();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                showLoading(false);
                showErrorDialog();
            }
        });
    }

    private void displayRecipeDetail(RecipeDetail recipe) {
        // Título
        tvRecipeTitle.setText(recipe.getTitle());
        toolbar.setTitle(recipe.getTitle());

        // Autor
        tvRecipeAuthor.setText(getString(R.string.recipe_author_prefix, recipe.getAuthorName()));

        // Descripción
        tvRecipeDescription.setText(recipe.getDescription());

        // Imagen
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(ivRecipeImage);
        }

        // Si es receta propia, ocultar favorito y rating del usuario
        if (recipe.isOwner()) {
            btnFavorite.setVisibility(View.GONE);
            if (userRatingContainer != null) {
                userRatingContainer.setVisibility(View.GONE);
            }
        } else {
            btnFavorite.setVisibility(View.VISIBLE);
            if (userRatingContainer != null) {
                userRatingContainer.setVisibility(View.VISIBLE);
            }
            // Estado de favorito
            updateFavoriteButton(recipe.isFavorite());

            // Rating del usuario (con click listeners)
            displayUserRating(recipe.getUserRating());
            setupUserRatingClickListeners();
        }

        // Rating promedio (siempre visible)
        displayAverageRating(recipe.getAvgRating());

        // Ingredientes
        displayIngredients(recipe.getIngredients());

        // Pasos
        displaySteps(recipe.getSteps());
    }

    private void displayAverageRating(double avgRating) {
        // Mostrar el número
        tvAvgRating.setText(String.format(getString(R.string.recipe_detail_rating_format), avgRating));

        // Actualizar estrellas
        ImageView[] stars = {avgStar1, avgStar2, avgStar3, avgStar4, avgStar5};
        updateStars(stars, avgRating);
    }

    private void displayUserRating(int userRating) {
        // Actualizar estrellas
        ImageView[] stars = {userStar1, userStar2, userStar3, userStar4, userStar5};
        updateStars(stars, userRating);
    }

    private void setupUserRatingClickListeners() {
        ImageView[] stars = {userStar1, userStar2, userStar3, userStar4, userStar5};

        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            stars[i].setOnClickListener(v -> onUserRatingClick(rating));
        }
    }

    private void onUserRatingClick(int newRating) {
        if (currentRecipe == null) return;

        int currentUserRating = currentRecipe.getUserRating();

        // Determinar si es POST (nuevo rating) o PUT (actualizar rating)
        Call<Void> call;
        RatingRequest ratingRequest = new RatingRequest(newRating);

        if (currentUserRating == 0) {
            // POST - Nuevo rating
            call = recipeService.addRating(recipeId, ratingRequest);
        } else {
            // PUT - Actualizar rating
            call = recipeService.updateRating(recipeId, ratingRequest);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Actualizar el rating localmente
                    currentRecipe.setUserRating(newRating);
                    displayUserRating(newRating);

                    // Mostrar mensaje
                    String message = currentUserRating == 0
                        ? getString(R.string.recipe_detail_rating_added)
                        : getString(R.string.recipe_detail_rating_updated);
                    Toast.makeText(RecipeDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Recargar la receta para obtener el nuevo avg_rating
                    loadRecipeDetail();
                } else {
                    Toast.makeText(RecipeDetailActivity.this,
                        R.string.recipe_detail_rating_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this,
                    R.string.recipe_detail_rating_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStars(ImageView[] stars, double rating) {
        int fullStars = (int) rating;

        for (int i = 0; i < stars.length; i++) {
            if (i < fullStars) {
                // Estrella llena
                stars[i].setImageResource(R.drawable.ic_star_filled_24);
            } else {
                // Estrella vacía
                stars[i].setImageResource(R.drawable.ic_star_border_24);
            }
        }
    }

    private void displayIngredients(java.util.List<RecipeIngredient> ingredients) {
        ingredientsContainer.removeAllViews();

        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        for (RecipeIngredient ingredient : ingredients) {
            TextView tvIngredient = new TextView(this);
            String ingredientText = getString(
                    R.string.recipe_detail_ingredient_format,
                    ingredient.getQuantity(),
                    ingredient.getUnitOfMeasure(),
                    ingredient.getName()
            );
            tvIngredient.setText("• " + ingredientText);
            tvIngredient.setTextSize(14);
            tvIngredient.setTextColor(getColor(android.R.color.black));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 8);
            tvIngredient.setLayoutParams(params);

            ingredientsContainer.addView(tvIngredient);
        }
    }

    private void displaySteps(java.util.List<RecipeStep> steps) {
        stepsContainer.removeAllViews();

        if (steps == null || steps.isEmpty()) {
            return;
        }

        for (RecipeStep step : steps) {
            // Título del paso
            TextView tvStepTitle = new TextView(this);
            tvStepTitle.setText(getString(R.string.recipe_detail_step_prefix, step.getOrder()));
            tvStepTitle.setTextSize(16);
            tvStepTitle.setTextColor(getColor(android.R.color.black));
            tvStepTitle.setTypeface(null, android.graphics.Typeface.BOLD);

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 16, 0, 8);
            tvStepTitle.setLayoutParams(titleParams);

            // Descripción del paso
            TextView tvStepDescription = new TextView(this);
            tvStepDescription.setText(step.getDescription());
            tvStepDescription.setTextSize(14);
            tvStepDescription.setTextColor(getColor(android.R.color.black));

            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            descParams.setMargins(0, 0, 0, 8);
            tvStepDescription.setLayoutParams(descParams);

            stepsContainer.addView(tvStepTitle);
            stepsContainer.addView(tvStepDescription);
        }
    }

    private void toggleFavorite() {
        if (currentRecipe == null) {
            return;
        }

        boolean isFavorite = currentRecipe.isFavorite();
        Call<Void> call = isFavorite
                ? recipeService.removeFavorite(recipeId)
                : recipeService.addFavorite(recipeId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    currentRecipe.setFavorite(!isFavorite);
                    updateFavoriteButton(!isFavorite);

                    int messageId = !isFavorite
                            ? R.string.recipe_detail_favorite_added
                            : R.string.recipe_detail_favorite_removed;
                    Toast.makeText(RecipeDetailActivity.this, messageId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecipeDetailActivity.this,
                            "Error al actualizar favoritos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFavoriteButton(boolean isFavorite) {
        btnFavorite.setImageResource(isFavorite
                ? R.drawable.ic_favorite_filled_24
                : R.drawable.ic_favorite_border_24);
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.recipe_detail_error_title)
                .setMessage(R.string.recipe_detail_error_message)
                .setPositiveButton(R.string.recipe_detail_error_retry, (dialog, which) -> loadRecipeDetail())
                .setNegativeButton(R.string.recipe_detail_error_back, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}

