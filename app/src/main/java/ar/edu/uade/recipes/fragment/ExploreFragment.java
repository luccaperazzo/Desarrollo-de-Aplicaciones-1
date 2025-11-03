package ar.edu.uade.recipes.fragment;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.repository.RecipeRepository;

/**
 * Fragment que muestra las recetas públicas (Explore)
 */
public class ExploreFragment extends BaseRecipeListFragment {

    @Override
    protected void fetchRecipes(String search, int skip, int limit, RecipeRepository.RecipeCallback callback) {
        repository.getPublicRecipes(search, skip, limit, callback);
    }

    @Override
    protected int getEmptyStateMessage() {
        return R.string.empty_state_message;
    }
}
