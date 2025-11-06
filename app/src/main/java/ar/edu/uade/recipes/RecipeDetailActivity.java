package ar.edu.uade.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.edu.uade.recipes.database.AppDatabase;
import ar.edu.uade.recipes.database.CartDao;
import ar.edu.uade.recipes.model.CartItem;
import ar.edu.uade.recipes.model.RecipeDetail;
import ar.edu.uade.recipes.model.RecipeIngredient;
import ar.edu.uade.recipes.model.RecipeStep;
import ar.edu.uade.recipes.util.AnalyticsHelper;
import ar.edu.uade.recipes.viewmodel.RecipeDetailViewModel;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_RECIPE_TITLE = "recipe_title";
    private static final int REQUEST_EDIT_RECIPE = 201;

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

    private RecipeDetailViewModel viewModel;
    private String recipeId;
    private RecipeDetail currentRecipe;
    private CartDao cartDao;
    private Executor executor;

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

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(RecipeDetailViewModel.class);

        cartDao = AppDatabase.getInstance(this).cartDao();
        executor = Executors.newSingleThreadExecutor();

        // Configurar observadores
        setupObservers();

        // Configurar botón de carrito
        btnAddToCart.setOnClickListener(v -> addIngredientsToCart());

        // Configurar botón de favoritos
        btnFavorite.setOnClickListener(v -> {
            if (currentRecipe != null) {
                viewModel.toggleFavorite(recipeId, currentRecipe.isFavorite());
            }
        });

        // Cargar datos de la receta
        viewModel.loadRecipeDetail(recipeId);
    }

    private void setupObservers() {
        // Observar receta
        viewModel.getRecipeDetail().observe(this, recipe -> {
            if (recipe != null) {
                currentRecipe = recipe;
                displayRecipeDetail(recipe);

                // Loguear evento de visualización de receta
                AnalyticsHelper.logViewRecipe(this, recipe.getId(), recipe.getTitle());
            }
        });

        // Observar estado de carga
        viewModel.getIsLoading().observe(this, isLoading -> {
            showLoading(isLoading != null && isLoading);
        });

        // Observar errores
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.equals("DELETE_SUCCESS")) {
                    // Eliminación exitosa
                    Toast.makeText(this, R.string.delete_recipe_success, Toast.LENGTH_SHORT).show();

                    // Loguear evento de eliminación de receta
                    if (currentRecipe != null) {
                        AnalyticsHelper.logDeleteRecipe(this, currentRecipe.getId(), currentRecipe.getTitle());
                    }

                    setResult(RESULT_OK);
                    finish();
                } else {
                    showErrorDialog();
                }
                viewModel.clearError();
            }
        });

        // Observar cambios en favoritos
        viewModel.getFavoriteUpdated().observe(this, updated -> {
            if (updated != null && updated) {
                String message = viewModel.getFavoriteMessage().getValue();
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                // Si se quitó de favoritos, notificar cambio
                RecipeDetail current = viewModel.getRecipeDetail().getValue();
                if (current != null) {
                    if (current.isFavorite()) {
                        // Loguear evento de agregar a favoritos
                        AnalyticsHelper.logAddFavorite(this, current.getId(), current.getTitle());
                    } else {
                        // Loguear evento de remover de favoritos
                        AnalyticsHelper.logRemoveFavorite(this, current.getId(), current.getTitle());
                        setResult(RESULT_OK);
                    }
                }
                viewModel.resetUpdateStates();
            }
        });

        // Observar cambios en rating
        viewModel.getRatingUpdated().observe(this, updated -> {
            if (updated != null && updated) {
                String message = viewModel.getRatingMessage().getValue();
                if (message != null) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                // Loguear evento de rating
                RecipeDetail current = viewModel.getRecipeDetail().getValue();
                if (current != null) {
                    AnalyticsHelper.logRateRecipe(this, current.getId(), current.getTitle(),
                        current.getUserRating());
                }
                viewModel.resetUpdateStates();
            }
        });
    }


    private void displayRecipeDetail(RecipeDetail recipe) {
        // Título
        tvRecipeTitle.setText(recipe.getTitle());
        toolbar.setTitle(recipe.getTitle());

        // Invalidar menú para mostrar opciones si es owner
        invalidateOptionsMenu();

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

        // Si es receta propia, ocultar favorito y rating del usuario, mostrar botones de edición/borrado
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
        viewModel.updateRating(recipeId, newRating, currentUserRating);
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
            tvIngredient.setText(getString(R.string.recipe_detail_ingredient_bullet) + ingredientText);
            tvIngredient.setTextSize(14);
            tvIngredient.setTextColor(com.google.android.material.color.MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorOnSurface,
                    android.graphics.Color.BLACK
            ));

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
            tvStepTitle.setTextColor(com.google.android.material.color.MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorOnSurface,
                    android.graphics.Color.BLACK
            ));
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
            tvStepDescription.setTextColor(com.google.android.material.color.MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorOnSurface,
                    android.graphics.Color.BLACK
            ));

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


    private void updateFavoriteButton(boolean isFavorite) {
        btnFavorite.setImageResource(isFavorite
                ? R.drawable.ic_favorite_filled_24
                : R.drawable.ic_favorite_border_24);
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_RECIPE && resultCode == RESULT_OK) {
            // Recargar el detalle de la receta después de editarla
            setResult(RESULT_OK);
            if (recipeId != null) {
                viewModel.loadRecipeDetail(recipeId);
            }
        }
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.recipe_detail_error_title)
                .setMessage(R.string.recipe_detail_error_message)
                .setPositiveButton(R.string.recipe_detail_error_retry, (dialog, which) -> {
                    if (recipeId != null) {
                        viewModel.loadRecipeDetail(recipeId);
                    }
                })
                .setNegativeButton(R.string.recipe_detail_error_back, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void editRecipe() {
        if (currentRecipe == null) return;

        Intent intent = new Intent(this, CreateRecipeActivity.class);
        intent.putExtra(CreateRecipeActivity.EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(CreateRecipeActivity.EXTRA_IS_EDIT_MODE, true);
        startActivityForResult(intent, REQUEST_EDIT_RECIPE);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_recipe_title)
                .setMessage(R.string.delete_recipe_message)
                .setPositiveButton(R.string.delete_recipe_confirm, (dialog, which) -> deleteRecipe())
                .setNegativeButton(R.string.delete_recipe_cancel, null)
                .show();
    }

    private void deleteRecipe() {
        viewModel.deleteRecipe(recipeId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentRecipe != null && currentRecipe.isOwner()) {
            getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            editRecipe();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar la receta cuando volvemos de editar
        if (recipeId != null) {
            viewModel.loadRecipeDetail(recipeId);
        }
    }

    private void addIngredientsToCart() {
        if (currentRecipe == null || currentRecipe.getIngredients() == null || currentRecipe.getIngredients().isEmpty()) {
            Toast.makeText(this, R.string.recipe_detail_no_ingredients, Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            int count = 0;
            for (RecipeIngredient ingredient : currentRecipe.getIngredients()) {
                CartItem cartItem = new CartItem(
                        ingredient.getName(),
                        ingredient.getQuantity(),
                        ingredient.getUnit()
                );
                cartDao.insert(cartItem);
                count++;
            }

            final int finalCount = count;
            runOnUiThread(() -> {
                String message = getString(R.string.cart_ingredients_added_count, finalCount);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                // Loguear evento de agregar al carrito
                if (currentRecipe != null) {
                    AnalyticsHelper.logAddToCart(this, currentRecipe.getId(),
                        currentRecipe.getTitle(), finalCount);
                }
            });
        });
    }
}

