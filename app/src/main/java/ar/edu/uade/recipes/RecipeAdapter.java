package ar.edu.uade.recipes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClick { void onClick(String title); }

    private final List<String> data;
    private final OnRecipeClick listener;

    public RecipeAdapter(List<String> data, OnRecipeClick listener) {
        this.data = data;
        this.listener = listener;
    }

    // Se ejecuta cuando RecyclerView va a crear una nueva "celda" para un item
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new VH(v);
    }

    // Se ejecuta cada vez que un ítem se muestra en pantalla
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        String title = data.get(pos);
        h.tvTitle.setText(title);
        // Acá habría que setear la imagen principal de la receta cuando la tengamos
        h.itemView.setOnClickListener(v -> listener.onClick(title));
    }

    @Override public int getItemCount() { return data.size(); }

    // Clase interna para guardar referencia a cada widget
    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle; ImageView imgCover;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imgCover = itemView.findViewById(R.id.imgCover);
        }
    }
}