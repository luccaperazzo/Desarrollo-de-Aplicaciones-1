package ar.edu.uade.recipes.fragment;

import androidx.lifecycle.LiveData;

import java.util.List;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.model.Recipe;
import ar.edu.uade.recipes.viewmodel.HomeViewModel;

/**
 * Fragment que muestra las recetas del usuario
 */
public class MyRecipesFragment extends BaseRecipeListFragment {

    @Override
    protected void loadRecipesFromViewModel(String search, boolean reset) {
        viewModel.loadMyRecipes(search, reset);
    }

    @Override
    protected LiveData<List<Recipe>> getRecipesLiveData() {
        return viewModel.getMyRecipes();
    }

    @Override
    protected int getEmptyStateMessage() {
        return R.string.empty_state_no_my_recipes;
    }
}
