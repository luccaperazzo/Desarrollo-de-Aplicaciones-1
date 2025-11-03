package ar.edu.uade.recipes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import ar.edu.uade.recipes.model.UpdateUserRequest;
import ar.edu.uade.recipes.model.User;
import ar.edu.uade.recipes.service.AuthService;
import ar.edu.uade.recipes.service.RetrofitClient;
import ar.edu.uade.recipes.util.ImageHelper;
import ar.edu.uade.recipes.util.UserManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ShapeableImageView ivProfileImage;
    private FloatingActionButton fabEditProfileImage;
    private TextInputLayout tilEmail, tilUsername, tilFullName, tilPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etUsername, etFullName, etPassword, etConfirmPassword;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    private UserManager userManager;
    private User currentUser;
    private String selectedImageBase64 = null;
    private AuthService authService;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userManager = new UserManager(this);
        authService = RetrofitClient.getRetrofitInstance(this).create(AuthService.class);
        currentUser = userManager.getUser();

        if (currentUser == null) {
            // Si no hay usuario, volver al login
            finish();
            return;
        }

        initializeViews();
        setupActivityResultLaunchers();
        setupListeners();
        loadUserData();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivProfileImage = findViewById(R.id.ivProfileImage);
        fabEditProfileImage = findViewById(R.id.fabEditProfileImage);
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        tilUsername = findViewById(R.id.tilUsername);
        etUsername = findViewById(R.id.etUsername);
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        tilPassword = findViewById(R.id.tilPassword);
        etPassword = findViewById(R.id.etPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        processImage(photo);
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            processImage(bitmap);
                        } catch (IOException e) {
                            Log.e("ProfileActivity", "Error loading image from gallery", e);
                            Toast.makeText(this, getString(R.string.register_error_processing_image), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, getString(R.string.register_error_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestGalleryPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, getString(R.string.register_error_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        fabEditProfileImage.setOnClickListener(v -> showImagePickerDialog());
        btnSave.setOnClickListener(v -> updateProfile());

        // Validación de email
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail();
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                tilEmail.setErrorEnabled(false);
                tilEmail.setError(null);
            }
        });

        // Validación de contraseña
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etPassword.getText() != null && etPassword.getText().length() > 0) {
                validatePassword();
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                tilPassword.setErrorEnabled(false);
                tilPassword.setError(null);
                syncPasswordVisibility();
            }
        });

        // Validación de confirmar contraseña
        etConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etConfirmPassword.getText() != null && etConfirmPassword.getText().length() > 0) {
                validateConfirmPassword();
            }
        });
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                tilConfirmPassword.setErrorEnabled(false);
                tilConfirmPassword.setError(null);
                syncPasswordVisibility();
            }
        });

        // Sincronizar visibilidad de contraseñas
        tilPassword.setEndIconOnClickListener(v -> syncPasswordVisibility());
        tilConfirmPassword.setEndIconOnClickListener(v -> syncPasswordVisibility());
    }

    private void syncPasswordVisibility() {
        // Sincronizar la visibilidad de ambos campos de contraseña
        if (etPassword.getInputType() != etConfirmPassword.getInputType()) {
            etConfirmPassword.setInputType(etPassword.getInputType());
            if (etConfirmPassword.getText() != null) {
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            }
        }
    }

    private void loadUserData() {
        etEmail.setText(currentUser.getEmail());
        etUsername.setText(currentUser.getUsername());
        etFullName.setText(currentUser.getFullName());

        // Cargar imagen de perfil si existe
        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentUser.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person_large)
                    .into(ivProfileImage);
            ivProfileImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            ivProfileImage.setPadding(0, 0, 0, 0);
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.create_recipe_select_image_title));
        String[] options = {
                getString(R.string.create_recipe_option_camera),
                getString(R.string.create_recipe_option_gallery)
        };
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermission();
            } else {
                checkGalleryPermission();
            }
        });
        builder.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void checkGalleryPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void processImage(Bitmap bitmap) {
        try {
            Bitmap resizedBitmap = ImageHelper.resizeBitmap(bitmap, 512);
            selectedImageBase64 = ImageHelper.bitmapToBase64(resizedBitmap);

            // Mostrar preview
            ivProfileImage.setImageBitmap(resizedBitmap);
            ivProfileImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            ivProfileImage.setPadding(0, 0, 0, 0);
        } catch (Exception e) {
            Log.e("ProfileActivity", "Error processing image", e);
            Toast.makeText(this, getString(R.string.register_error_processing_image), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateEmail() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.register_error_empty_email));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.register_error_invalid_email));
            return false;
        }
        tilEmail.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        if (password.isEmpty()) {
            // La contraseña es opcional
            tilPassword.setError(null);
            return true;
        }
        if (password.length() < 8) {
            tilPassword.setError(getString(R.string.register_error_short_password));
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            tilPassword.setError(getString(R.string.register_error_password_lowercase));
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            tilPassword.setError(getString(R.string.register_error_password_uppercase));
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            tilPassword.setError(getString(R.string.register_error_password_number));
            return false;
        }
        tilPassword.setError(null);
        return true;
    }

    private boolean validateConfirmPassword() {
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        if (password.isEmpty()) {
            // Si no hay contraseña, no validar confirmación
            tilConfirmPassword.setError(null);
            return true;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.register_error_passwords_not_match));
            return false;
        }
        tilConfirmPassword.setError(null);
        return true;
    }

    private boolean validateAll() {
        boolean isValid = true;
        if (!validateEmail()) isValid = false;
        if (!validatePassword()) isValid = false;
        if (!validateConfirmPassword()) isValid = false;

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        if (username.isEmpty()) {
            tilUsername.setError(getString(R.string.register_error_empty_username));
            isValid = false;
        } else {
            tilUsername.setError(null);
        }

        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        if (fullName.isEmpty()) {
            tilFullName.setError(getString(R.string.register_error_empty_full_name));
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        return isValid;
    }

    private void updateProfile() {
        if (!validateAll()) {
            Toast.makeText(this, getString(R.string.create_recipe_error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // Preparar datos actualizados
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String password = etPassword.getText() != null && etPassword.getText().length() > 0 ?
                etPassword.getText().toString() : null;
        String profileImageUrl = selectedImageBase64 != null ? selectedImageBase64 : currentUser.getProfileImageUrl();

        UpdateUserRequest updateRequest = new UpdateUserRequest(email, username, fullName, profileImageUrl, password);

        Call<User> call = authService.updateUser(currentUser.getId(), updateRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Actualizar usuario localmente
                    userManager.saveUser(response.body());
                    Toast.makeText(ProfileActivity.this, getString(R.string.profile_success), Toast.LENGTH_SHORT).show();

                    // Notificar a HomeActivity que se actualizó el perfil
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.profile_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(ProfileActivity.this, getString(R.string.profile_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

