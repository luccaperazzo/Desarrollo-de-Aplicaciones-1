package ar.edu.uade.recipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ar.edu.uade.recipes.model.LoginRequest;
import ar.edu.uade.recipes.model.LoginResponse;
import ar.edu.uade.recipes.service.AuthService;
import ar.edu.uade.recipes.service.RetrofitClient;
import ar.edu.uade.recipes.util.UserManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: agregar logo en la vista

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private AuthService authService;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        MaterialButton btnNoCuenta = findViewById(R.id.btnNoCuenta);

        authService = RetrofitClient.getRetrofitInstance(this).create(AuthService.class);
        userManager = new UserManager(this);

        // TextWatcher para habilitar botón cuando se ingrese email y pass
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
                String pass  = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
                btnLogin.setEnabled(!email.isEmpty() && !pass.isEmpty());
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etEmail.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);

        // Limpiar estado de error cuando el usuario edita
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilEmail != null) tilEmail.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPassword != null) tilPassword.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Click en Login
        btnLogin.setOnClickListener(v -> this.handleLogin(v));

        // Click en Registro
        btnNoCuenta.setOnClickListener(v -> {
            hideKeyboard(v);
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void handleLogin(View view) {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Ocultar teclado y mostrar loader
        hideKeyboard(view);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        if (tilEmail != null) tilEmail.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        Call<LoginResponse> call = authService.login(new LoginRequest(email, pass));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Lógica de login exitoso
                    Toast.makeText(LoginActivity.this, "Logueado!", Toast.LENGTH_SHORT).show();

                    if (tilEmail != null) tilEmail.setError(null);
                    if (tilPassword != null) tilPassword.setError(null);

                    // Guardar token y usuario
                    userManager.saveToken(response.body().getAccessToken());
                    if (response.body().getUser() != null) {
                        userManager.saveUser(response.body().getUser());
                    }

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish(); // evita volver al login con back
                } else {
                    // Lógica de error
                    String errorMessage = "Credenciales inválidas!";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorBody);
                            errorMessage = jsonObject.getString("detail");
                        }
                    } catch (IOException | JSONException ignored) { }

                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                    // Marcar inputs en rojo (estado de error)
                    if (tilEmail != null) tilEmail.setError(" ");
                    if (tilPassword != null) tilPassword.setError(" ");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                Toast.makeText(LoginActivity.this, "Error de red. Verifique su conexión.", Toast.LENGTH_SHORT).show();

                // Mostrar error visual también en caso de fallo de red
                if (tilEmail != null) tilEmail.setError("");
                if (tilPassword != null) tilPassword.setError("");
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
