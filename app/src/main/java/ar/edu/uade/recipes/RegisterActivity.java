package ar.edu.uade.recipes;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import ar.edu.uade.recipes.util.AnalyticsHelper;
import ar.edu.uade.recipes.util.ImageHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilName, tilEmail, tilPass, tilConfirm;
    private TextInputEditText etUsername, etName, etEmail, etPass, etConfirm;
    private MaterialCheckBox cbTerms;
    private MaterialButton btnRegister;
    private FloatingActionButton fabEditProfileImage;
    private ImageView ivProfileImage;
    private ProgressBar progressBar;
    private AuthService authService;

    private String selectedImageBase64 = null;
    private Uri cameraImageUri;

    // Launcher para el permiso de la cámara
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showImageSourceDialog();
                } else {
                    Toast.makeText(this, getString(R.string.register_error_permission_denied), Toast.LENGTH_SHORT).show();
                }
            });

    // Launcher para la cámara
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result && cameraImageUri != null) {
                    processImage(cameraImageUri);
                }
            });

    // Launcher para la galería
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
        fabEditProfileImage = findViewById(R.id.fabEditProfileImage);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        MaterialButton btnAlready = findViewById(R.id.btnAlready);
        progressBar = findViewById(R.id.progressBar);

        authService = RetrofitClient.getRetrofitInstance(this).create(AuthService.class);

        // TextWatcher para habilitar/deshabilitar el botón
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateForButtonEnable();
            }
        };

        etUsername.addTextChangedListener(watcher);
        etName.addTextChangedListener(watcher);
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> validateForButtonEnable());

        // TextWatcher para email
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Limpiar error mientras escribe
                if (tilEmail.getError() != null) {
                    tilEmail.setErrorEnabled(false);
                    tilEmail.setError(null);
                }
                validateForButtonEnable();
            }
        });

        // TextWatcher para contraseña
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Limpiar error mientras escribe
                if (tilPass.getError() != null) {
                    tilPass.setErrorEnabled(false);
                    tilPass.setError(null);
                }
                validateForButtonEnable();
            }
        });

        // TextWatcher para confirmar contraseña
        etConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Limpiar error mientras escribe
                if (tilConfirm.getError() != null) {
                    tilConfirm.setErrorEnabled(false);
                    tilConfirm.setError(null);
                }
                validateForButtonEnable();
            }
        });

        // Validar campos cuando en blur
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail();
            }
        });

        etPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePassword();
            }
        });

        etConfirm.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateConfirmPassword();
            }
        });

        // mostrar/ocultar contraseña en campo de pass y en campo de confirmar
        handlePasswordVisibility();

        // Configurar imagen de perfil
        fabEditProfileImage.setOnClickListener(v -> checkPermissionAndShowDialog());
        ivProfileImage.setOnClickListener(v -> checkPermissionAndShowDialog());

        btnRegister.setOnClickListener(v -> {
            hideKeyboard(v);
            handleRegister();
        });

        btnAlready.setOnClickListener(v -> {
            hideKeyboard(v);
            Toast.makeText(this, getString(R.string.register_msg_returning_to_login), Toast.LENGTH_SHORT).show();
            finish();
        });

        validateForButtonEnable();
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
            // Mostrar preview de la imagen
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Corregir orientación EXIF para el preview
                Bitmap correctedBitmap = ImageHelper.fixImageOrientation(this, imageUri, bitmap);
                ivProfileImage.setImageBitmap(correctedBitmap);
                ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivProfileImage.setPadding(0, 0, 0, 0); // Quitar padding cuando hay imagen
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.register_error_processing_image), Toast.LENGTH_SHORT).show();
            }
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

                    // Loguear evento de registro
                    AnalyticsHelper.logRegister(RegisterActivity.this, "email");

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

    /**
     * Valida solo para habilitar/deshabilitar el botón de registro sin mostrar errores visuales
     */
    private void validateForButtonEnable() {
        String username = getTextOf(etUsername);
        String name = getTextOf(etName);
        String email = getTextOf(etEmail);
        String pass = getTextOf(etPass);
        String confirm = getTextOf(etConfirm);

        boolean isValid = !username.isEmpty()
                && !name.isEmpty()
                && !email.isEmpty()
                && !pass.isEmpty()
                && !confirm.isEmpty()
                && pass.equals(confirm)
                && isValidEmail(email)
                && isValidPassword(pass)
                && cbTerms.isChecked();

        btnRegister.setEnabled(isValid);
    }

    /**
     * Valida el email y muestra error
     */
    private void validateEmail() {
        String email = getTextOf(etEmail);

        if (!email.isEmpty() && !isValidEmail(email)) {
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(getString(R.string.register_error_invalid_email));
        } else {
            tilEmail.setErrorEnabled(false);
            tilEmail.setError(null);
        }

        validateForButtonEnable();
    }

    /**
     * Valida la contraseña y muestra error
     */
    private void validatePassword() {
        String pass = getTextOf(etPass);

        if (!pass.isEmpty() && !isValidPassword(pass)) {
            tilPass.setErrorEnabled(true);
            tilPass.setError(getString(R.string.register_error_invalid_password));
        } else {
            tilPass.setErrorEnabled(false);
            tilPass.setError(null);
        }

        validateForButtonEnable();
    }

    /**
     * Valida la confirmación de contraseña y muestra error
     */
    private void validateConfirmPassword() {
        String pass = getTextOf(etPass);
        String confirm = getTextOf(etConfirm);

        if (!confirm.isEmpty() && !pass.equals(confirm)) {
            tilConfirm.setErrorEnabled(true);
            tilConfirm.setError(getString(R.string.register_error_passwords_dont_match));
        } else {
            tilConfirm.setErrorEnabled(false);
            tilConfirm.setError(null);
        }

        validateForButtonEnable();
    }

    /**
     * MAneja la visibilidad de las contraseñas entre ambos campos
     */
    private void handlePasswordVisibility() {
        // Listener unificado para ambos campos
        View.OnClickListener togglePasswordVisibility = v -> {
            // Obtener el estado actual (cualquiera de los dos campos sirve)
            boolean isPasswordVisible = (etPass.getInputType() & android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) != 0;

            // Determinar el nuevo tipo de input
            int newInputType = isPasswordVisible
                ? android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                : android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

            // Aplicar a ambos campos
            etPass.setInputType(newInputType);
            etConfirm.setInputType(newInputType);

            // Mantener el cursor al final del texto en ambos campos
            etPass.setSelection(etPass.getText() != null ? etPass.getText().length() : 0);
            etConfirm.setSelection(etConfirm.getText() != null ? etConfirm.getText().length() : 0);

            // Actualizar ambos íconos
            tilPass.refreshEndIconDrawableState();
            tilConfirm.refreshEndIconDrawableState();
        };

        // Asignar el mismo listener a ambos campos
        tilPass.setEndIconOnClickListener(togglePasswordVisibility);
        tilConfirm.setEndIconOnClickListener(togglePasswordVisibility);
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
