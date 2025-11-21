package ar.edu.uade.recipes.adapter;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.model.Recipe;

public class RecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_RECIPE = 0;
    private static final int VIEW_TYPE_SKELETON = 1;

    private List<Recipe> recipes = new ArrayList<>();
    private OnRecipeClickListener listener;
    private boolean showSkeleton = false;
    private int skeletonCount = 5;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipesAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        this.showSkeleton = false;
        notifyDataSetChanged();
    }

    public void addRecipes(List<Recipe> newRecipes) {
        int startPosition = recipes.size();
        recipes.addAll(newRecipes);
        notifyItemRangeInserted(startPosition, newRecipes.size());
    }

    public void addRecipe(Recipe recipe) {
        int position = recipes.size();
        recipes.add(recipe);
        notifyItemInserted(position);
    }

    public List<Recipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public void clearRecipes() {
        recipes.clear();
        notifyDataSetChanged();
    }

    public void showSkeleton(int count) {
        this.showSkeleton = true;
        this.skeletonCount = count;
        notifyDataSetChanged();
    }

    public void hideSkeleton() {
        this.showSkeleton = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return showSkeleton ? VIEW_TYPE_SKELETON : VIEW_TYPE_RECIPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SKELETON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipe_skeleton, parent, false);
            return new SkeletonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipe, parent, false);
            return new RecipeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecipeViewHolder) {
            Recipe recipe = recipes.get(position);
            ((RecipeViewHolder) holder).bind(recipe, listener);
        }
        // SkeletonViewHolder no necesita bind
    }

    @Override
    public int getItemCount() {
        return showSkeleton ? skeletonCount : recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivRecipeImage;
        private TextView tvRecipeTitle;
        private TextView tvRecipeAuthor;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeAuthor = itemView.findViewById(R.id.tvRecipeAuthor);
        }

        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            tvRecipeTitle.setText(recipe.getTitle());
            tvRecipeAuthor.setText(itemView.getContext().getString(R.string.recipe_author_prefix, recipe.getAuthorName()));

            String imageUrl = recipe.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.startsWith("http")) {
                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .centerCrop()
                            .into(ivRecipeImage);
                } else {
                    try {
                        byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                        Glide.with(itemView.getContext())
                                .asBitmap()
                                .load(imageBytes)
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .centerCrop()
                                .into(ivRecipeImage);
                    } catch (Exception e) {
                        ivRecipeImage.setImageResource(R.drawable.ic_launcher_background);
                        Log.e("RecipesAdapter", "Error decodificando imagen Base64", e);
                    }
                }
            } else {
                ivRecipeImage.setImageResource(R.drawable.ic_launcher_background);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        public SkeletonViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
