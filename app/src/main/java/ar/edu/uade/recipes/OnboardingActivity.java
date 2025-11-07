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

    private ViewPager2 viewPager;
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
        adapter = new OnboardingAdapter(this);
        // TODO: Replace with your actual content
        adapter.addFragment(OnboardingPageFragment.newInstance("Bienvenido a Cookly", "Tu asistente personal de recetas", R.drawable.groceries));
        adapter.addFragment(OnboardingPageFragment.newInstance("Encontrá recetas", "Agregá futuras recetas a tu lista para tenerlas siempre disponibles", R.drawable.recipes));
        adapter.addFragment(OnboardingPageFragment.newInstance("Creá tus recetas", "Y publicalas para que todo el mundo las vea", R.drawable.media));

        viewPager.setAdapter(adapter);
        setupIndicators();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
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
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }
    }

    private void finishOnboarding() {
        // Mark onboarding as completed
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("onboarding_completed", true);
        editor.apply();

        // Navigate to the main part of the app
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
