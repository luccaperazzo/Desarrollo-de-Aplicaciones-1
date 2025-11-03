package ar.edu.uade.recipes.fragment;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.repository.RecipeRepository;

/**
 * Fragment que muestra las recetas favoritas del usuario (Favorites)
 */
public class FavoritesFragment extends BaseRecipeListFragment {

    @Override
    protected void fetchRecipes(String search, int skip, int limit, RecipeRepository.RecipeCallback callback) {
        repository.getFavoriteRecipes(search, skip, limit, callback);
    }

    @Override
    protected int getEmptyStateMessage() {
        return R.string.empty_state_no_favorites;
    }
}
