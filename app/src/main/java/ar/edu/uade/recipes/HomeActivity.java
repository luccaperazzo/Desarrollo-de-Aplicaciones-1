package ar.edu.uade.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import ar.edu.uade.recipes.fragment.ExploreFragment;
import ar.edu.uade.recipes.fragment.FavoritesFragment;
import ar.edu.uade.recipes.fragment.MyRecipesFragment;
import ar.edu.uade.recipes.fragment.RecipeListFragment;
import ar.edu.uade.recipes.model.User;
import ar.edu.uade.recipes.util.UserManager;

// TODO: verificar el tema de que todos los fragments hagan sus llamadas al abrir la app.
// TODO: si se agrega una nueva receta o se agrega/quita un favorito, habría que actualizar

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNav;

    // Drawer views
    private ImageView ivUserProfile;
    private TextView tvUserName;
    private MaterialCardView btnProfile;
    private MaterialCardView btnCart;
    private MaterialCardView btnLogout;
    private MaterialSwitch switchTheme;
    private ImageView ivThemeIcon;

    private ExploreFragment exploreFragment;
    private MyRecipesFragment myRecipesFragment;
    private FavoritesFragment favoritesFragment;
    private Fragment currentFragment;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500; // 500ms de debounce

    private UserManager userManager;

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

        userManager = new UserManager(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etSearch = findViewById(R.id.etSearch);
        fab = findViewById(R.id.fabAdd);
        bottomNav = findViewById(R.id.bottomNav);

        // Inicializar drawer views
        View navHeader = findViewById(R.id.navHeader);
        ivUserProfile = navHeader.findViewById(R.id.ivUserProfile);
        tvUserName = navHeader.findViewById(R.id.tvUserName);
        btnProfile = findViewById(R.id.btnProfile);
        btnCart = findViewById(R.id.btnCart);
        btnLogout = findViewById(R.id.btnLogout);
        switchTheme = findViewById(R.id.switchTheme);
        ivThemeIcon = findViewById(R.id.ivThemeIcon);

        // Configurar drawer
        setupDrawer();
        loadUserData();

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

    private void setupDrawer() {
        // Abrir drawer al hacer click en el icono del menú
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Botón de perfil
        btnProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivityForResult(intent, 100); // Request code 100 para ProfileActivity
        });

        // Botón de carrito (mock)
        btnCart.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // TODO: implementar carrito
        });

        // Botón de logout
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        // Switch de tema - cargar preferencia guardada
        boolean isDarkMode = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark_mode", false);
        switchTheme.setChecked(isDarkMode);
        ivThemeIcon.setImageResource(isDarkMode ? R.drawable.ic_dark_mode_24 : R.drawable.ic_light_mode_24);

        // Switch de tema
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar preferencia
            getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("dark_mode", isChecked).apply();

            // Actualizar icono
            if (isChecked) {
                ivThemeIcon.setImageResource(R.drawable.ic_dark_mode_24);
            } else {
                ivThemeIcon.setImageResource(R.drawable.ic_light_mode_24);
            }

            // TODO: Implementar aplicación del tema
            // AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void loadUserData() {
        User user = userManager.getUser();
        if (user != null) {
            tvUserName.setText(getString(R.string.drawer_hello, user.getFullName()));

            // Cargar imagen de perfil si existe
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_24)
                    .into(ivUserProfile);
            }
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.drawer_logout_confirm_title)
            .setMessage(R.string.drawer_logout_confirm_message)
            .setPositiveButton(R.string.drawer_logout_confirm_yes, (dialog, which) -> logout())
            .setNegativeButton(R.string.drawer_logout_confirm_no, null)
            .show();
    }

    private void logout() {
        userManager.clearAll();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Recargar datos del usuario después de actualizar el perfil
            loadUserData();
        }
    }
}
