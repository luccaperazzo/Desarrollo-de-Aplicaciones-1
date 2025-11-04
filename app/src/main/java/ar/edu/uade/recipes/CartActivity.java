package ar.edu.uade.recipes;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.edu.uade.recipes.adapter.CartAdapter;
import ar.edu.uade.recipes.database.AppDatabase;
import ar.edu.uade.recipes.database.CartDao;
import ar.edu.uade.recipes.model.CartItem;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private View emptyState;

    private CartAdapter adapter;
    private CartDao cartDao;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupRecyclerView();
        setupSwipeToDelete();
        loadCartItems();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        emptyState = findViewById(R.id.emptyState);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        cartDao = AppDatabase.getInstance(this).cartDao();
        executor = Executors.newSingleThreadExecutor();

        fabAdd.setOnClickListener(v -> showAddItemDialog());
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CartItem item = adapter.getItem(position);
                deleteItem(item, position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadCartItems() {
        executor.execute(() -> {
            List<CartItem> items = cartDao.getAllItems();
            runOnUiThread(() -> {
                adapter.setItems(items);
                updateEmptyState();
            });
        });
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cart_add_item_dialog_title);

        // Layout para el diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Input para nombre
        final EditText inputName = new EditText(this);
        inputName.setHint(R.string.cart_item_name_hint);
        layout.addView(inputName);

        // Input para cantidad
        final EditText inputQuantity = new EditText(this);
        inputQuantity.setHint(R.string.cart_item_quantity_hint);
        inputQuantity.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputQuantity);

        // Spinner para unidad
        final Spinner spinnerUnit = new Spinner(this);
        String[] units = getResources().getStringArray(R.array.units_array);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        spinnerUnit.setAdapter(unitAdapter);
        layout.addView(spinnerUnit);

        builder.setView(layout);

        builder.setPositiveButton(R.string.cart_add_item_dialog_add, (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String quantity = inputQuantity.getText().toString().trim();
            String unit = spinnerUnit.getSelectedItem().toString();

            if (name.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(this, R.string.cart_error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            addItem(name, quantity, unit);
        });

        builder.setNegativeButton(R.string.cart_add_item_dialog_cancel, null);
        builder.show();
    }

    private void addItem(String name, String quantity, String unit) {
        CartItem item = new CartItem(name, quantity, unit);
        executor.execute(() -> {
            cartDao.insert(item);
            runOnUiThread(() -> {
                loadCartItems();
                Toast.makeText(this, R.string.cart_item_added, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void deleteItem(CartItem item, int position) {
        executor.execute(() -> {
            cartDao.delete(item);
            runOnUiThread(() -> {
                adapter.removeItem(position);
                updateEmptyState();
                Toast.makeText(this, R.string.cart_item_deleted, Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onItemCheckedChanged(CartItem item, boolean isChecked) {
        executor.execute(() -> {
            cartDao.update(item);
        });
    }

    @Override
    public void onItemDelete(CartItem item) {
        executor.execute(() -> {
            cartDao.delete(item);
            runOnUiThread(() -> {
                loadCartItems();
                Toast.makeText(this, R.string.cart_item_deleted, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

