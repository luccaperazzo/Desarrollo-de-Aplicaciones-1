package ar.edu.uade.recipes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.recipes.model.AudioStepsResponse;
import ar.edu.uade.recipes.model.CreateRecipeRequest;
import ar.edu.uade.recipes.model.RecipeDetail;
import ar.edu.uade.recipes.model.RecipeIngredient;
import ar.edu.uade.recipes.model.RecipeStep;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ar.edu.uade.recipes.service.RecipeService;
import ar.edu.uade.recipes.service.RetrofitClient;
import ar.edu.uade.recipes.util.AnalyticsHelper;
import ar.edu.uade.recipes.util.ImageHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRecipeActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";

    private MaterialToolbar toolbar;
    private MaterialButton btnUploadImage;
    private ImageView ivRecipePreview;
    private TextInputEditText etRecipeName;
    private TextInputEditText etDescription;
    private LinearLayout stepsContainer;
    private LinearLayout ingredientsContainer;
    private MaterialButton btnAddStep;
    private MaterialButton btnAddIngredient;
    private MaterialButton btnRecordAudio;
    private MaterialButton btnSave;
    private View loadingOverlay;

    private RecipeService recipeService;
    private String imageBase64;
    private String recipeId;
    private boolean isEditMode;
    private RecipeDetail currentRecipe;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> audioPermissionLauncher;
    private String[] units;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private static final int REQUEST_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recipeService = RetrofitClient.getRetrofitInstance(this).create(RecipeService.class);
        units = getResources().getStringArray(R.array.units_array);

        // Obtener datos del intent
        recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        isEditMode = getIntent().getBooleanExtra(EXTRA_IS_EDIT_MODE, false);

        initializeViews();
        setupActivityResultLaunchers();
        setupListeners();
        setupAudioPermissionLauncher();

        // Configurar título según el modo
        if (isEditMode) {
            toolbar.setTitle(R.string.edit_recipe_title);
            loadRecipeForEdit();
        } else {
            toolbar.setTitle(R.string.create_recipe_title);
            // Agregar primer paso e ingrediente por defecto
            addStepInput();
            addIngredientInput();
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        ivRecipePreview = findViewById(R.id.ivRecipePreview);
        etRecipeName = findViewById(R.id.etRecipeName);
        etDescription = findViewById(R.id.etDescription);
        stepsContainer = findViewById(R.id.stepsContainer);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        btnAddStep = findViewById(R.id.btnAddStep);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnRecordAudio = findViewById(R.id.btnRecordAudio);
        btnSave = findViewById(R.id.btnSave);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    processImage(imageBitmap);
                }
            }
        );

        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        // Corregir orientación EXIF
                        Bitmap correctedBitmap = ImageHelper.fixImageOrientation(this, imageUri, bitmap);
                        processImage(correctedBitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, R.string.register_error_processing_image, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    private void setupListeners() {
        btnUploadImage.setOnClickListener(v -> showImagePickerDialog());
        btnAddStep.setOnClickListener(v -> addStepInput());
        btnAddIngredient.setOnClickListener(v -> addIngredientInput());
        btnRecordAudio.setOnClickListener(v -> handleAudioRecording());
        btnSave.setOnClickListener(v -> saveRecipe());
    }

    private void setupAudioPermissionLauncher() {
        audioPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startAudioRecording();
                } else {
                    Toast.makeText(this, R.string.create_recipe_audio_permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.create_recipe_select_image_title)
            .setItems(new String[]{
                getString(R.string.create_recipe_option_camera),
                getString(R.string.create_recipe_option_gallery)
            }, (dialog, which) -> {
                if (which == 0) {
                    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(takePictureIntent);
                } else {
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
                    pickPhotoIntent.setType("image/*");
                    galleryLauncher.launch(pickPhotoIntent);
                }
            })
            .show();
    }

    private void processImage(Bitmap bitmap) {
        try {
            Bitmap resizedBitmap = ImageHelper.resizeBitmap(bitmap, 800);
            imageBase64 = ImageHelper.bitmapToBase64(resizedBitmap);

            ivRecipePreview.setImageBitmap(resizedBitmap);
            ivRecipePreview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, R.string.register_error_processing_image, Toast.LENGTH_SHORT).show();
        }
    }

    private void addStepInput() {
        View stepView = LayoutInflater.from(this).inflate(R.layout.item_step_input, stepsContainer, false);
        MaterialButton btnRemove = stepView.findViewById(R.id.btnRemoveStep);

        // Solo permitir eliminar si hay más de un paso
        btnRemove.setOnClickListener(v -> {
            if (stepsContainer.getChildCount() > 1) {
                stepsContainer.removeView(stepView);
            } else {
                Toast.makeText(this, R.string.create_recipe_error_empty_steps, Toast.LENGTH_SHORT).show();
            }
        });

        stepsContainer.addView(stepView);
    }

    private void addIngredientInput() {
        View ingredientView = LayoutInflater.from(this).inflate(R.layout.item_ingredient_input, ingredientsContainer, false);
        MaterialButton btnRemove = ingredientView.findViewById(R.id.btnRemoveIngredient);
        AutoCompleteTextView spinnerUnit = ingredientView.findViewById(R.id.spinnerUnit);

        // Configurar spinner de unidades
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units);
        spinnerUnit.setAdapter(adapter);
        spinnerUnit.setText(units[0], false); // Valor por defecto

        // Solo permitir eliminar si hay más de un ingrediente
        btnRemove.setOnClickListener(v -> {
            if (ingredientsContainer.getChildCount() > 1) {
                ingredientsContainer.removeView(ingredientView);
            } else {
                Toast.makeText(this, R.string.create_recipe_error_empty_ingredients, Toast.LENGTH_SHORT).show();
            }
        });

        ingredientsContainer.addView(ingredientView);
    }

    private void saveRecipe() {
        // Validar campos
        String title = etRecipeName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, R.string.create_recipe_error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageBase64 == null || imageBase64.isEmpty()) {
            Toast.makeText(this, R.string.create_recipe_error_no_image, Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener pasos
        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View stepView = stepsContainer.getChildAt(i);
            TextInputEditText etStep = stepView.findViewById(R.id.etStep);
            String stepText = etStep.getText().toString().trim();

            if (stepText.isEmpty()) {
                Toast.makeText(this, R.string.create_recipe_error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            RecipeStep step = new RecipeStep();
            step.setOrder(i + 1);
            step.setDescription(stepText);
            steps.add(step);
        }

        // Obtener ingredientes
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            TextInputEditText etName = ingredientView.findViewById(R.id.etIngredientName);
            TextInputEditText etQuantity = ingredientView.findViewById(R.id.etQuantity);
            AutoCompleteTextView spinnerUnit = ingredientView.findViewById(R.id.spinnerUnit);

            String name = etName.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String unit = spinnerUnit.getText().toString().trim();

            if (name.isEmpty() || quantity.isEmpty() || unit.isEmpty()) {
                Toast.makeText(this, R.string.create_recipe_error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            RecipeIngredient ingredient = new RecipeIngredient(name, quantity, unit);
            ingredients.add(ingredient);
        }

        // Crear request
        CreateRecipeRequest request = new CreateRecipeRequest(title, description, ingredients, steps, imageBase64);

        // Enviar al backend
        showLoading(true);
        Call<RecipeDetail> call = isEditMode
            ? recipeService.updateRecipe(recipeId, request)
            : recipeService.createRecipe(request);

        call.enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    RecipeDetail recipe = response.body();
                    int messageRes = isEditMode ? R.string.edit_recipe_success : R.string.create_recipe_success;
                    Toast.makeText(CreateRecipeActivity.this, messageRes, Toast.LENGTH_SHORT).show();

                    // Loguear evento de creación o edición
                    if (isEditMode) {
                        AnalyticsHelper.logEditRecipe(CreateRecipeActivity.this, recipe.getId(), recipe.getTitle());
                    } else {
                        int ingredientsCount = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
                        int stepsCount = recipe.getSteps() != null ? recipe.getSteps().size() : 0;
                        AnalyticsHelper.logCreateRecipe(CreateRecipeActivity.this, recipe.getId(),
                            recipe.getTitle(), ingredientsCount, stepsCount);
                    }

                    setResult(RESULT_OK); // Notificar que hubo cambios
                    finish();
                } else {
                    int errorRes = isEditMode ? R.string.edit_recipe_error : R.string.create_recipe_error;
                    Toast.makeText(CreateRecipeActivity.this, errorRes, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                showLoading(false);
                int errorRes = isEditMode ? R.string.edit_recipe_error : R.string.create_recipe_error;
                Toast.makeText(CreateRecipeActivity.this, errorRes, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }

    private void loadRecipeForEdit() {
        showLoading(true);

        recipeService.getRecipeDetail(recipeId).enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentRecipe = response.body();
                    populateFields(currentRecipe);
                } else {
                    Toast.makeText(CreateRecipeActivity.this, R.string.edit_recipe_error, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                showLoading(false);
                Toast.makeText(CreateRecipeActivity.this, R.string.edit_recipe_error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(RecipeDetail recipe) {
        // Título y descripción
        etRecipeName.setText(recipe.getTitle());
        etDescription.setText(recipe.getDescription());

        // Imagen
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            imageBase64 = recipe.getImageUrl();
            // Cargar imagen con Glide
            com.bumptech.glide.Glide.with(this)
                .load(recipe.getImageUrl())
                .into(ivRecipePreview);
            ivRecipePreview.setVisibility(View.VISIBLE);
        }

        // Pasos
        if (recipe.getSteps() != null) {
            for (RecipeStep step : recipe.getSteps()) {
                View stepView = LayoutInflater.from(this).inflate(R.layout.item_step_input, stepsContainer, false);
                TextInputEditText etStep = stepView.findViewById(R.id.etStep);
                etStep.setText(step.getDescription());

                MaterialButton btnRemove = stepView.findViewById(R.id.btnRemoveStep);
                btnRemove.setOnClickListener(v -> {
                    if (stepsContainer.getChildCount() > 1) {
                        stepsContainer.removeView(stepView);
                    } else {
                        Toast.makeText(CreateRecipeActivity.this, R.string.create_recipe_error_empty_steps, Toast.LENGTH_SHORT).show();
                    }
                });

                stepsContainer.addView(stepView);
            }
        }

        // Ingredientes
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                View ingredientView = LayoutInflater.from(this).inflate(R.layout.item_ingredient_input, ingredientsContainer, false);
                TextInputEditText etName = ingredientView.findViewById(R.id.etIngredientName);
                TextInputEditText etQuantity = ingredientView.findViewById(R.id.etQuantity);
                AutoCompleteTextView spinnerUnit = ingredientView.findViewById(R.id.spinnerUnit);

                etName.setText(ingredient.getName());
                etQuantity.setText(ingredient.getQuantity());

                // Configurar spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units);
                spinnerUnit.setAdapter(adapter);
                spinnerUnit.setText(ingredient.getUnit(), false);

                MaterialButton btnRemove = ingredientView.findViewById(R.id.btnRemoveIngredient);
                btnRemove.setOnClickListener(v -> {
                    if (ingredientsContainer.getChildCount() > 1) {
                        ingredientsContainer.removeView(ingredientView);
                    } else {
                        Toast.makeText(CreateRecipeActivity.this, R.string.create_recipe_error_empty_ingredients, Toast.LENGTH_SHORT).show();
                    }
                });

                ingredientsContainer.addView(ingredientView);
            }
        }
    }

    private void handleAudioRecording() {
        if (isRecording) {
            stopAudioRecording();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                startAudioRecording();
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        }
    }

    private void startAudioRecording() {
        try {
            // Crear archivo temporal para el audio
            File audioFile = new File(getCacheDir(), "recipe_audio_" + System.currentTimeMillis() + ".3gp");
            audioFilePath = audioFile.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            btnRecordAudio.setText(R.string.create_recipe_audio_stop);
            btnRecordAudio.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_media_pause));
            Toast.makeText(this, R.string.create_recipe_audio_recording, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, R.string.create_recipe_audio_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                btnRecordAudio.setText(R.string.create_recipe_record_audio);
                btnRecordAudio.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_btn_speak_now));

                // Enviar audio al backend
                sendAudioToBackend();

            } catch (Exception e) {
                Toast.makeText(this, R.string.create_recipe_audio_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                isRecording = false;
                btnRecordAudio.setText(R.string.create_recipe_record_audio);
            }
        }
    }

    private void sendAudioToBackend() {
        if (audioFilePath == null || audioFilePath.isEmpty()) {
            Toast.makeText(this, R.string.create_recipe_audio_error, Toast.LENGTH_SHORT).show();
            return;
        }

        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            Toast.makeText(this, R.string.create_recipe_audio_error, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        Toast.makeText(this, R.string.create_recipe_audio_processing, Toast.LENGTH_SHORT).show();

        // Crear RequestBody para el archivo de audio
        RequestBody requestBody = RequestBody.create(
            audioFile,
            okhttp3.MediaType.parse("audio/3gpp")
        );

        // Crear MultipartBody.Part
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData(
            "audio",
            audioFile.getName(),
            requestBody
        );

        // Enviar al backend
        Call<AudioStepsResponse> call = recipeService.transcribeAudio(audioPart);
        call.enqueue(new Callback<AudioStepsResponse>() {
            @Override
            public void onResponse(Call<AudioStepsResponse> call, Response<AudioStepsResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    AudioStepsResponse audioResponse = response.body();
                    List<String> steps = audioResponse.getSteps();

                    if (steps != null && !steps.isEmpty()) {
                        // Limpiar pasos existentes si es modo creación
                        if (!isEditMode) {
                            stepsContainer.removeAllViews();
                        }

                        // Agregar los pasos generados desde el audio
                        for (int i = 0; i < steps.size(); i++) {
                            View stepView = LayoutInflater.from(CreateRecipeActivity.this)
                                .inflate(R.layout.item_step_input, stepsContainer, false);
                            TextInputEditText etStep = stepView.findViewById(R.id.etStep);
                            etStep.setText(steps.get(i));

                            MaterialButton btnRemove = stepView.findViewById(R.id.btnRemoveStep);
                            btnRemove.setOnClickListener(v -> {
                                if (stepsContainer.getChildCount() > 1) {
                                    stepsContainer.removeView(stepView);
                                } else {
                                    Toast.makeText(CreateRecipeActivity.this,
                                        R.string.create_recipe_error_empty_steps, Toast.LENGTH_SHORT).show();
                                }
                            });

                            stepsContainer.addView(stepView);
                        }

                        Toast.makeText(CreateRecipeActivity.this,
                            R.string.create_recipe_audio_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateRecipeActivity.this,
                            R.string.create_recipe_audio_error_server, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateRecipeActivity.this,
                        R.string.create_recipe_audio_error_server, Toast.LENGTH_SHORT).show();
                }

                // Limpiar archivo temporal
                if (audioFile.exists()) {
                    audioFile.delete();
                }
            }

            @Override
            public void onFailure(Call<AudioStepsResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(CreateRecipeActivity.this,
                    R.string.create_recipe_audio_error_network, Toast.LENGTH_SHORT).show();

                // Limpiar archivo temporal
                File audioFile = new File(audioFilePath);
                if (audioFile.exists()) {
                    audioFile.delete();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            if (isRecording) {
                try {
                    mediaRecorder.stop();
                } catch (Exception e) { }
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }

        // Limpiar archivo temporal si existe
        if (audioFilePath != null) {
            File audioFile = new File(audioFilePath);
            if (audioFile.exists()) {
                audioFile.delete();
            }
        }
    }
}

