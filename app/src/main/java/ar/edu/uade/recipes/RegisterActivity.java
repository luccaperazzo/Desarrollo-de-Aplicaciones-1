package ar.edu.uade.recipes;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.uade.recipes.model.RegisterRequest;
import ar.edu.uade.recipes.model.RegisterResponse;
import ar.edu.uade.recipes.service.AuthService;
import ar.edu.uade.recipes.service.RetrofitClient;
import ar.edu.uade.recipes.util.ImageHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilName, tilEmail, tilPass, tilConfirm;
    private TextInputEditText etUsername, etName, etEmail, etPass, etConfirm;
    private MaterialCheckBox cbTerms;
    private MaterialButton btnRegister, btnProfileImage;
    private ProgressBar progressBar;
    private AuthService authService;

    private String selectedImageBase64 = null;
    private Uri cameraImageUri;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showImageSourceDialog();
                } else {
                    Toast.makeText(this, getString(R.string.register_error_permission_denied), Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result && cameraImageUri != null) {
                    processImage(cameraImageUri);
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processImage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tilUsername = findViewById(R.id.tilUsername);
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPass = findViewById(R.id.tilPass);
        tilConfirm = findViewById(R.id.tilConfirm);

        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etConfirm = findViewById(R.id.etConfirm);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        btnProfileImage = findViewById(R.id.btnProfileImage);
        MaterialButton btnAlready = findViewById(R.id.btnAlready);
        progressBar = findViewById(R.id.progressBar);

        authService = RetrofitClient.getRetrofitInstance(this).create(AuthService.class);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        };

        etUsername.addTextChangedListener(watcher);
        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPass.addTextChangedListener(watcher);
        etConfirm.addTextChangedListener(watcher);
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> validate());

        btnProfileImage.setOnClickListener(v -> checkPermissionAndShowDialog());

        btnRegister.setOnClickListener(v -> {
            hideKeyboard(v);
            handleRegister();
        });

        btnAlready.setOnClickListener(v -> {
            hideKeyboard(v);
            Toast.makeText(this, getString(R.string.register_msg_returning_to_login), Toast.LENGTH_SHORT).show();
            finish();
        });

        validate();
    }

    private void checkPermissionAndShowDialog() {
        // Para Android 13+ ya no hace falta READ_EXTERNAL_STORAGE para galería
        // Solo verificamos si tenemos permiso de cámara
        if (checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            showImageSourceDialog();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.register_dialog_select_image_title)
                .setItems(new String[]{getString(R.string.register_dialog_option_camera), getString(R.string.register_dialog_option_gallery)}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new android.content.ContentValues());
        cameraLauncher.launch(cameraImageUri);
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void processImage(Uri imageUri) {
        selectedImageBase64 = ImageHelper.convertImageToBase64(this, imageUri);
        if (selectedImageBase64 != null) {
            btnProfileImage.setText(R.string.register_profile_image_selected);
        } else {
            Toast.makeText(this, getString(R.string.register_error_processing_image), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegister() {
        String username = getTextOf(etUsername);
        String name = getTextOf(etName);
        String email = getTextOf(etEmail);
        String pass = getTextOf(etPass);

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        RegisterRequest request = new RegisterRequest(email, username, name, selectedImageBase64, pass);
        Call<RegisterResponse> call = authService.register(request);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    finish(); // Volver al login
                } else {
                    String errorMessage = getString(R.string.register_error_title);
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            errorMessage = parseErrorMessage(errorBody);
                        } catch (IOException e) {
                            // Fallback
                        }
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, getString(R.string.register_error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validate() {
        String username = getTextOf(etUsername);
        String name = getTextOf(etName);
        String email = getTextOf(etEmail);
        String pass = getTextOf(etPass);
        String confirm = getTextOf(etConfirm);

        tilUsername.setError(null);
        tilName.setError(null);
        tilEmail.setError(null);
        tilPass.setError(null);
        tilConfirm.setError(null);

        boolean hasErrors = false;

        // Validar email
        if (!email.isEmpty() && !isValidEmail(email)) {
            tilEmail.setError(getString(R.string.register_error_invalid_email));
            hasErrors = true;
        }

        // Validar contraseña
        if (!pass.isEmpty() && !isValidPassword(pass)) {
            tilPass.setError(getString(R.string.register_error_invalid_password));
            hasErrors = true;
        }

        // Validar confirmación de contraseña
        if (!pass.equals(confirm) && !confirm.isEmpty()) {
            tilConfirm.setError(getString(R.string.register_error_passwords_dont_match));
            hasErrors = true;
        }

        boolean fieldsfilled = !username.isEmpty()
                && !name.isEmpty()
                && !email.isEmpty()
                && !pass.isEmpty()
                && !confirm.isEmpty()
                && pass.equals(confirm)
                && isValidEmail(email)
                && isValidPassword(pass)
                && cbTerms.isChecked()
                && !hasErrors;

        btnRegister.setEnabled(fieldsfilled);
    }

    private String getTextOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private String parseErrorMessage(String errorBody) {
        try {
            JSONObject json = new JSONObject(errorBody);

            // Intentar parsear el formato {"detail": [{"msg": "..."}]}
            if (json.has("detail")) {
                Object detail = json.get("detail");

                // Si detail es un array
                if (detail instanceof JSONArray) {
                    JSONArray detailArray = (JSONArray) detail;
                    if (detailArray.length() > 0) {
                        JSONObject firstError = detailArray.getJSONObject(0);
                        if (firstError.has("msg")) {
                            return firstError.getString("msg");
                        }
                    }
                }
                // Si detail es un string
                else if (detail instanceof String) {
                    return (String) detail;
                }
            }

            // Fallback: retornar el JSON completo si no se puede parsear
            return errorBody;
        } catch (JSONException e) {
            return errorBody;
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // Al menos una minúscula, una mayúscula y un número
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasLower && hasUpper && hasDigit;
    }
}
