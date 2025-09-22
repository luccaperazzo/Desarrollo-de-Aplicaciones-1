package ar.edu.uade.recipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;

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
        btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnNoCuenta = findViewById(R.id.btnNoCuenta);

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

        // Click en Login
        btnLogin.setOnClickListener(v -> {
                hideKeyboard(v);
                Toast.makeText(this, "Logueado!", Toast.LENGTH_SHORT).show();

                // Lógica de login mock
                getSharedPreferences("auth", MODE_PRIVATE).edit()
                        .putBoolean("logged_in", true)
                        .apply();
                startActivity(new Intent(this, HomeActivity.class));
                finish(); // evita volver al login con back
        });

        // Click en Registro
        btnNoCuenta.setOnClickListener(v -> {
            hideKeyboard(v);
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}