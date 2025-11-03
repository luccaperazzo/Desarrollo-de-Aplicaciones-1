package ar.edu.uade.recipes;

import android.content.Context;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPass, tilConfirm;
    private TextInputEditText etName, etEmail, etPass, etConfirm;
    private MaterialCheckBox cbTerms;
    private MaterialButton btnRegister;

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

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPass = findViewById(R.id.tilPass);
        tilConfirm = findViewById(R.id.tilConfirm);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etConfirm = findViewById(R.id.etConfirm);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        MaterialButton btnAlready = findViewById(R.id.btnAlready);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPass.addTextChangedListener(watcher);
        etConfirm.addTextChangedListener(watcher);
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> validate());

        btnRegister.setOnClickListener(v -> {
            hideKeyboard(v);
            Toast.makeText(this, "Registro enviado (mock)", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnAlready.setOnClickListener(v -> {
            hideKeyboard(v);
            Toast.makeText(this, "Volviendo al login", Toast.LENGTH_SHORT).show();
            finish();
        });

        validate();
    }

    private void validate() {
        String name = getTextOf(etName);
        String email = getTextOf(etEmail);
        String pass = getTextOf(etPass);
        String confirm = getTextOf(etConfirm);


        tilName.setError(null);
        tilEmail.setError(null);
        tilPass.setError(null);
        tilConfirm.setError(null);

        boolean fieldsfilled = !name.isEmpty()
                && !email.isEmpty()
                && !pass.isEmpty()
                && !confirm.isEmpty()
                && pass.equals(confirm)
                && cbTerms.isChecked();

        if (!pass.equals(confirm) && !confirm.isEmpty()) {
            tilConfirm.setError("Las contraseñas no coinciden");
        }

        btnRegister.setEnabled(fieldsfilled);
    }

    private String getTextOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}