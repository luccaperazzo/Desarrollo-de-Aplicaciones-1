package ar.edu.uade.recipes.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.recipes.R;
import ar.edu.uade.recipes.model.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> items;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onItemCheckedChanged(CartItem item, boolean isChecked);
        void onItemDelete(CartItem item);
    }

    public CartAdapter(OnCartItemListener listener) {
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public CartItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private MaterialCheckBox checkboxCompleted;
        private TextView tvItemName;
        private TextView tvItemQuantity;
        private MaterialButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxCompleted = itemView.findViewById(R.id.checkboxCompleted);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(CartItem item) {
            tvItemName.setText(item.getName());

            // Formatear cantidad y unidad
            String quantityText = item.getQuantity() + " " + item.getUnit();
            tvItemQuantity.setText(quantityText);

            // Estado de completado
            checkboxCompleted.setChecked(item.isCompleted());
            updateCompletedState(item.isCompleted());

            // Listener del checkbox
            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setCompleted(isChecked);
                updateCompletedState(isChecked);
                if (listener != null) {
                    listener.onItemCheckedChanged(item, isChecked);
                }
            });

            // Listener del botón de eliminar
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemDelete(item);
                }
            });
        }

        private void updateCompletedState(boolean isCompleted) {
            if (isCompleted) {
                // Texto tachado
                tvItemName.setPaintFlags(tvItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvItemQuantity.setPaintFlags(tvItemQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvItemName.setAlpha(0.5f);
                tvItemQuantity.setAlpha(0.5f);
            } else {
                // Texto normal
                tvItemName.setPaintFlags(tvItemName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvItemQuantity.setPaintFlags(tvItemQuantity.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvItemName.setAlpha(1.0f);
                tvItemQuantity.setAlpha(1.0f);
            }
        }
    }
}

