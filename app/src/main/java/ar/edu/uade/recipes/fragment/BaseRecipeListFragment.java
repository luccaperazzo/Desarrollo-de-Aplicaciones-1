package ar.edu.uade.recipes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.adapter.RecipesAdapter;
import ar.edu.uade.recipes.model.Recipe;
import ar.edu.uade.recipes.repository.RecipeRepository;

/**
 * Clase base abstracta para los fragments que muestran listas de recetas.
 * Contiene toda la lógica común de paginación, búsqueda, estados, etc.
 */
public abstract class BaseRecipeListFragment extends Fragment implements RecipeListFragment {

    // Views
    protected RecyclerView recyclerView;
    protected RecipesAdapter adapter;
    protected ProgressBar progressBar;
    protected LinearLayoutManager layoutManager;
    protected View emptyStateLayout;
    protected TextView tvEmptyMessage;
    protected View errorStateLayout;
    protected TextView tvErrorMessage;
    protected MaterialButton btnRetry;

    // Data
    protected RecipeRepository repository;
    protected static final int PAGE_SIZE = 20;
    protected int currentSkip = 0;
    protected String currentSearch = "";
    protected boolean isLoading = false;
    protected boolean hasMoreData = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupInfiniteScroll();

        repository = new RecipeRepository(requireContext());

        btnRetry.setOnClickListener(v -> {
            currentSkip = 0;
            hasMoreData = true;
            loadRecipes();
        });

        loadRecipes();

        return view;
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rvRecipes);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        tvEmptyMessage = emptyStateLayout.findViewById(R.id.tvEmptyMessage);
        errorStateLayout = view.findViewById(R.id.errorStateLayout);
        tvErrorMessage = errorStateLayout.findViewById(R.id.tvErrorMessage);
        btnRetry = errorStateLayout.findViewById(R.id.btnRetry);
    }

    /**
     * Configura el RecyclerView con su adapter y layout manager
     */
    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipesAdapter(recipe -> {
            onRecipeClick(recipe);
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * Configura el listener para infinite scroll
     */
    private void setupInfiniteScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { // Scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && hasMoreData) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            loadMoreRecipes();
                        }
                    }
                }
            }
        });
    }

    /**
     * Muestra el estado correspondiente (contenido, vacío o error)
     */
    protected void showState(State state) {
        recyclerView.setVisibility(state == State.CONTENT ? View.VISIBLE : View.GONE);
        emptyStateLayout.setVisibility(state == State.EMPTY ? View.VISIBLE : View.GONE);
        errorStateLayout.setVisibility(state == State.ERROR ? View.VISIBLE : View.GONE);
    }

    /**
     * Estados posibles del fragment
     */
    protected enum State {
        CONTENT, EMPTY, ERROR
    }

    /**
     * Carga las recetas desde el repositorio
     */
    protected void loadRecipes() {
        isLoading = true;

        // Mostrar skeleton solo en la primera carga
        if (currentSkip == 0) {
            adapter.showSkeleton(5);
            showState(State.CONTENT);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        fetchRecipes(currentSearch, currentSkip, PAGE_SIZE, new RecipeRepository.RecipeCallback() {
            @Override
            public void onSuccess(List<Recipe> recipes, boolean fromCache) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        isLoading = false;
                        adapter.hideSkeleton();
                        progressBar.setVisibility(View.GONE);

                        if (fromCache) {
                            // Datos desde cache, detener paginación
                            hasMoreData = false;
                            if (currentSkip == 0) {
                                Toast.makeText(getContext(), R.string.error_state_offline_with_cache, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Datos desde backend, verificar si hay más
                            if (recipes.size() < PAGE_SIZE) {
                                hasMoreData = false;
                            }
                        }

                        if (currentSkip == 0) {
                            adapter.setRecipes(recipes);

                            if (recipes.isEmpty()) {
                                // Mostrar empty state
                                if (currentSearch != null && !currentSearch.isEmpty()) {
                                    tvEmptyMessage.setText(R.string.empty_state_no_results);
                                } else {
                                    tvEmptyMessage.setText(getEmptyStateMessage());
                                }
                                showState(State.EMPTY);
                            } else {
                                showState(State.CONTENT);
                            }
                        } else {
                            adapter.addRecipes(recipes);
                        }
                    });
                }
            }

            @Override
            public void onError(String message, boolean hasCache) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        isLoading = false;
                        adapter.hideSkeleton();
                        progressBar.setVisibility(View.GONE);
                        hasMoreData = false; // Detener infinite scroll en error

                        if (currentSkip == 0) {
                            if (hasCache) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                tvErrorMessage.setText(message.equals("Sin conexión")
                                    ? getString(R.string.error_state_offline_no_cache)
                                    : getString(R.string.error_state_message));
                                showState(State.ERROR);
                            }
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Carga más recetas (paginación)
     */
    protected void loadMoreRecipes() {
        currentSkip += PAGE_SIZE;
        loadRecipes();
    }

    @Override
    public void search(String query) {
        currentSearch = query;
        currentSkip = 0;
        hasMoreData = true;
        loadRecipes();
    }

    @Override
    public void scrollToTopAndRefresh() {
        // Scroll al inicio
        recyclerView.smoothScrollToPosition(0);

        // Resetear y recargar
        currentSkip = 0;
        hasMoreData = true;
        loadRecipes();
    }

    /**
     * Método abstracto donde cada fragment intica qué recetas obtener
     */
    protected abstract void fetchRecipes(String search, int skip, int limit, RecipeRepository.RecipeCallback callback);

    /**
     * Método abstracto donde cada fragment indica los mensajes de empty state
     */
    @StringRes
    protected abstract int getEmptyStateMessage();

    protected void onRecipeClick(Recipe recipe) {
        Toast.makeText(getContext(), "Abrir: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
    }
}

