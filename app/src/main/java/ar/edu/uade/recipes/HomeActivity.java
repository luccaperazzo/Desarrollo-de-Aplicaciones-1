package ar.edu.uade.recipes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rv = findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RecipeAdapter(mockRecipes(), title ->
                Toast.makeText(this, "Abrir: " + title, Toast.LENGTH_SHORT).show()
        ));

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                Toast.makeText(this, "Agregar receta (mock)", Toast.LENGTH_SHORT).show()
        );

        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                Toast.makeText(this, "Explorar", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_my) {
                Toast.makeText(this, "Mis recetas (mock)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_favs) {
                Toast.makeText(this, "Favoritas (mock)", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        bottom.setSelectedItemId(R.id.nav_explore); // seleccionado por defecto
    }

    @NonNull
    private List<String> mockRecipes() {
        List<String> out = new ArrayList<>();
        out.add("Milanesas con puré");
        out.add("Ensalada");
        out.add("Fideos");
        out.add("Tarta de pollo");
        out.add("Brownies");
        return out;
    }
}