package ar.edu.uade.recipes.fragment;

import android.content.Intent;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.RecipeDetailActivity;
import ar.edu.uade.recipes.adapter.RecipesAdapter;
import ar.edu.uade.recipes.model.Recipe;
import ar.edu.uade.recipes.viewmodel.HomeViewModel;

/**
 * Clase abstracta para los fragments que muestran listas de recetas.
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

    // Data - ViewModel
    protected HomeViewModel viewModel;
    protected static final int PAGE_SIZE = 20;
    protected String currentSearch = "";
    protected boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupInfiniteScroll();

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Observar LiveData
        setupObservers();

        btnRetry.setOnClickListener(v -> {
            loadRecipes(true);
        });

        loadRecipes(true);

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

                    Boolean hasMoreData = viewModel.getHasMoreData().getValue();
                    if (!isLoading && hasMoreData != null && hasMoreData) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            loadMoreRecipes();
                        }
                    }
                }
            }
        });
    }

    private void setupObservers() {
        // Observar recetas según el tipo de fragment
        getRecipesLiveData().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                updateRecipesList(recipes);
            }
        });

        // Observar estado de carga
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading != null && loading;
            if (loading != null) {
                if (loading) {
                    // Mostrar skeleton solo en la primera carga
                    if (adapter.getItemCount() == 0) {
                        adapter.showSkeleton(5);
                        showState(State.CONTENT);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                } else {
                    adapter.hideSkeleton();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // Observar errores
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                handleError(errorMessage);
            }
        });

        // Observar si viene de cache
        viewModel.getIsFromCache().observe(getViewLifecycleOwner(), fromCache -> {
            if (fromCache != null && fromCache && adapter.getItemCount() == 0) {
                Toast.makeText(getContext(), R.string.error_state_offline_with_cache, Toast.LENGTH_SHORT).show();
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
     * Carga las recetas usando el ViewModel
     */
    protected void loadRecipes(boolean reset) {
        loadRecipesFromViewModel(currentSearch, reset);
    }

    /**
     * Carga más recetas (paginación)
     */
    protected void loadMoreRecipes() {
        loadRecipes(false);
    }

    /**
     * Actualiza la lista de recetas cuando cambian los datos
     */
    private void updateRecipesList(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            // Mostrar empty state
            if (currentSearch != null && !currentSearch.isEmpty()) {
                tvEmptyMessage.setText(R.string.empty_state_no_results);
            } else {
                tvEmptyMessage.setText(getEmptyStateMessage());
            }
            showState(State.EMPTY);
        } else {
            // Siempre reemplazar la lista completa cuando vienen del ViewModel
            // El ViewModel ya maneja la paginación agregando a la lista existente
            adapter.setRecipes(recipes);
            showState(State.CONTENT);
        }
    }

    /**
     * Maneja los errores
     */
    private void handleError(String message) {
        if (adapter.getItemCount() == 0) {
            tvErrorMessage.setText(message.equals(getString(R.string.error_no_connection))
                    ? getString(R.string.error_state_offline_no_cache)
                    : getString(R.string.error_state_message));
            showState(State.ERROR);
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void search(String query) {
        currentSearch = query;
        loadRecipes(true);
    }

    @Override
    public void scrollToTopAndRefresh() {
        // Scroll al inicio
        recyclerView.smoothScrollToPosition(0);
        // Resetear y recargar
        loadRecipes(true);
    }

    /**
     * Método abstracto donde cada fragment indica qué método del ViewModel usar
     */
    protected abstract void loadRecipesFromViewModel(String search, boolean reset);

    /**
     * Método abstracto donde cada fragment indica qué LiveData observar
     */
    protected abstract androidx.lifecycle.LiveData<List<Recipe>> getRecipesLiveData();

    /**
     * Método abstracto donde cada fragment indica los mensajes de empty state
     */
    @StringRes
    protected abstract int getEmptyStateMessage();

    /**
     * Método que se llama cuando se hace click en una receta.
     * Abre el detalle de la receta.
     */
    protected void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_TITLE, recipe.getTitle());
        startActivityForResult(intent, 201); // Request code 201 para ver/editar/borrar receta
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201 && resultCode == getActivity().RESULT_OK) {
            // Recargar la lista si hubo cambios (edición o borrado)
            scrollToTopAndRefresh();
        }
    }
}

