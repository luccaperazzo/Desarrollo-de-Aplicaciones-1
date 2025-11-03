package ar.edu.uade.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import ar.edu.uade.recipes.fragment.ExploreFragment;
import ar.edu.uade.recipes.fragment.FavoritesFragment;
import ar.edu.uade.recipes.fragment.MyRecipesFragment;
import ar.edu.uade.recipes.fragment.RecipeListFragment;

// TODO: verificar el tema de que todos los fragments hagan sus llamadas al abrir la app.
// TODO: si se agrega una nueva receta o se agrega/quita un favorito, habría que actualizar

public class HomeActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNav;

    private ExploreFragment exploreFragment;
    private MyRecipesFragment myRecipesFragment;
    private FavoritesFragment favoritesFragment;
    private Fragment currentFragment;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500; // 500ms de debounce

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etSearch = findViewById(R.id.etSearch);
        fab = findViewById(R.id.fabAdd);
        bottomNav = findViewById(R.id.bottomNav);

        // Inicializar fragments
        exploreFragment = new ExploreFragment();
        myRecipesFragment = new MyRecipesFragment();
        favoritesFragment = new FavoritesFragment();

        // Agregar todos los fragments al inicio
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, exploreFragment, "explore")
                .add(R.id.fragmentContainer, myRecipesFragment, "my_recipes")
                .add(R.id.fragmentContainer, favoritesFragment, "favorites")
                .hide(myRecipesFragment)
                .hide(favoritesFragment)
                .commit();

        // Establecer fragment inicial
        currentFragment = exploreFragment;
        toolbar.setTitle(R.string.home_title_explore);

        // Configurar BottomNavigationView
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                if (currentFragment == exploreFragment) {
                    // Ya está en esta pestaña, scroll arriba y recargar
                    scrollToTopAndRefresh(exploreFragment);
                } else {
                    loadFragment(exploreFragment, getString(R.string.home_title_explore));
                }
                return true;
            } else if (id == R.id.nav_my) {
                if (currentFragment == myRecipesFragment) {
                    // Ya está en esta pestaña, scroll arriba y recargar
                    scrollToTopAndRefresh(myRecipesFragment);
                } else {
                    loadFragment(myRecipesFragment, getString(R.string.home_title_my_recipes));
                }
                return true;
            } else if (id == R.id.nav_favs) {
                if (currentFragment == favoritesFragment) {
                    // Ya está en esta pestaña, scroll arriba y recargar
                    scrollToTopAndRefresh(favoritesFragment);
                } else {
                    loadFragment(favoritesFragment, getString(R.string.home_title_favorites));
                }
                return true;
            }
            return false;
        });

        // Configurar FAB
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, CreateRecipeActivity.class))
        );

        // Configurar búsqueda con debounce
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancelar búsqueda pendiente
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Programar nueva búsqueda con delay
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    performSearch(query);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar handler para evitar memory leaks
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        if (currentFragment == fragment) {
            return; // Ya está visible
        }

        toolbar.setTitle(title);

        getSupportFragmentManager()
                .beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commit();

        currentFragment = fragment;
    }

    private void performSearch(String query) {
        if (currentFragment instanceof RecipeListFragment) {
            ((RecipeListFragment) currentFragment).search(query);
        }
    }

    private void scrollToTopAndRefresh(Fragment fragment) {
        if (fragment instanceof RecipeListFragment) {
            ((RecipeListFragment) fragment).scrollToTopAndRefresh();
        }
    }
}
