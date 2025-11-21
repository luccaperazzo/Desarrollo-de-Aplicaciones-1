package ar.edu.uade.recipes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ar.edu.uade.recipes.R;

public class OnboardingPageFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE_RES = "image_res";

    public static OnboardingPageFragment newInstance(String title, String description, int imageRes) {
        OnboardingPageFragment fragment = new OnboardingPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putInt(ARG_IMAGE_RES, imageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titleTextView = view.findViewById(R.id.onboarding_title);
        TextView descriptionTextView = view.findViewById(R.id.onboarding_description);
        ImageView imageView = view.findViewById(R.id.onboarding_image);

        if (getArguments() != null) {
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            descriptionTextView.setText(getArguments().getString(ARG_DESCRIPTION));
            imageView.setImageResource(getArguments().getInt(ARG_IMAGE_RES));
        }
    }
}
