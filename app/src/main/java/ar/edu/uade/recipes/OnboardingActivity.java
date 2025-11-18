package ar.edu.uade.recipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import ar.edu.uade.recipes.adapter.OnboardingAdapter;
import ar.edu.uade.recipes.fragment.OnboardingPageFragment;

public class OnboardingActivity extends AppCompatActivity {

    // Componente de Android que permite crear vistas deslizables
    private ViewPager2 viewPager;
    // Adaptador para manejar los fragmentos de la vista deslizante
    private OnboardingAdapter adapter;
    private LinearLayout indicatorLayout;
    private Button nextButton, backButton, skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        skipButton = findViewById(R.id.skipButton);

        setupViewPager();
        setupListeners();
    }

    private void setupViewPager() {
        // Carga los fragmens al adapter
        adapter = new OnboardingAdapter(this);
        adapter.addFragment(OnboardingPageFragment.newInstance(getString(R.string.onboarding_page1_title), getString(R.string.onboarding_page1_description), R.drawable.groceries));
        adapter.addFragment(OnboardingPageFragment.newInstance(getString(R.string.onboarding_page2_title), getString(R.string.onboarding_page2_description), R.drawable.recipes));
        adapter.addFragment(OnboardingPageFragment.newInstance(getString(R.string.onboarding_page3_title), getString(R.string.onboarding_page3_description), R.drawable.media));

        viewPager.setAdapter(adapter);
        setupIndicators();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Actualiza los indicadores y la visibilidad de los botones
                updateIndicators(position);
                updateButtonVisibility(position);
            }
        });
    }

    private void setupListeners() {
        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        backButton.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));
        skipButton.setOnClickListener(v -> finishOnboarding());
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }
        updateIndicators(0); // Set first indicator as active
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            indicator.setImageDrawable(ContextCompat.getDrawable(this,
                    i == position ? R.drawable.indicator_active : R.drawable.indicator_inactive));
        }
    }

    private void updateButtonVisibility(int position) {
        if (position == 0) {
            backButton.setVisibility(View.INVISIBLE);
        } else {
            backButton.setVisibility(View.VISIBLE);
        }

        if (position == adapter.getItemCount() - 1) {
            nextButton.setText(R.string.onboarding_button_finish);
        } else {
            nextButton.setText(R.string.onboarding_button_next);
        }
    }

    private void finishOnboarding() {
        // Marca el onboarding como completado
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("onboarding_completed", true);
        editor.apply();

        // Navega al login
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
