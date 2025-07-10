package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.api.AuthService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.request.RegisterRequest;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.dto.response.RegisterResponse;
import com.example.myapplication.util.ErrorUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilFullName;
    private TextInputLayout tilPhone;
    private TextInputLayout tilStreetAddress;
    private TextInputLayout tilProvince;
    private TextInputLayout tilDistrict;
    private TextInputLayout tilWard;

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etFullName;
    private TextInputEditText etPhone;
    private TextInputEditText etStreetAddress;
    private TextInputEditText etProvince;
    private TextInputEditText etDistrict;
    private TextInputEditText etWard;

    private MaterialButton btnRegister;
    private TextView tvLoginLink;
    private TextView tvError;
    private ProgressBar progressBar;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initServices();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilStreetAddress = findViewById(R.id.tilStreetAddress);
        tilProvince = findViewById(R.id.tilProvince);
        tilDistrict = findViewById(R.id.tilDistrict);
        tilWard = findViewById(R.id.tilWard);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etStreetAddress = findViewById(R.id.etStreetAddress);
        etProvince = findViewById(R.id.etProvince);
        etDistrict = findViewById(R.id.etDistrict);
        etWard = findViewById(R.id.etWard);

        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initServices() {
        authService = ApiClient.getRetrofitInstance(this).create(AuthService.class);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String streetAddress = etStreetAddress.getText().toString().trim();
        String province = etProvince.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String ward = etWard.getText().toString().trim();

        // Clear previous errors
        clearAllErrors();
        hideError();

        // Validate inputs
        if (!validateInputs(email, password, fullName, phone, streetAddress, province, district, ward)) {
            return;
        }

        // Show loading state
        setLoading(true);

        // Create register request
        RegisterRequest registerRequest = new RegisterRequest(
                email, password, fullName, phone, streetAddress, ward, province, district);

        // Make API call
        authService.register(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulRegistration(response.body());
                } else {
                    handleFailedRegistration(response);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                setLoading(false);
                String errorMessage = ErrorUtils.handleThrowable(t);
                showError(errorMessage);
                Log.e(TAG, "Network error", t);
            }
        });
    }

    private void clearAllErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilFullName.setError(null);
        tilPhone.setError(null);
        tilStreetAddress.setError(null);
        tilProvince.setError(null);
        tilDistrict.setError(null);
        tilWard.setError(null);
    }

    private boolean validateInputs(String email, String password, String fullName, String phone,
            String streetAddress, String province, String district, String ward) {
        boolean isValid = true;

        // Simple validation
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (fullName.isEmpty()) {
            tilFullName.setError("Full name is required");
            isValid = false;
        }

        if (phone.isEmpty()) {
            tilPhone.setError("Phone is required");
            isValid = false;
        }

        if (streetAddress.isEmpty()) {
            tilStreetAddress.setError("Street address is required");
            isValid = false;
        }

        if (province.isEmpty()) {
            tilProvince.setError("Province is required");
            isValid = false;
        }

        if (district.isEmpty()) {
            tilDistrict.setError("District is required");
            isValid = false;
        }

        if (ward.isEmpty()) {
            tilWard.setError("Ward is required");
            isValid = false;
        }

        return isValid;
    }

    private void handleSuccessfulRegistration(RegisterResponse registerResponse) {
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

        // Navigate to login activity
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("registered_email", etEmail.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    private void handleFailedRegistration(Response<RegisterResponse> response) {
        ErrorResponse errorResponse = ErrorUtils.processError(response);
        String errorMessage = "Registration failed";

        if (errorResponse != null && errorResponse.getError() != null) {
            errorMessage += ": " + errorResponse.getError();
        } else if (response.code() == 409) {
            errorMessage = "Email already exists";
            tilEmail.setError("Email already exists");
        }

        showError(errorMessage);
        Log.e(TAG, "Error: " + errorMessage);
    }

    private void showError(String errorMessage) {
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);

        // Enable/disable all input fields
        tilEmail.setEnabled(!isLoading);
        tilPassword.setEnabled(!isLoading);
        tilFullName.setEnabled(!isLoading);
        tilPhone.setEnabled(!isLoading);
        tilStreetAddress.setEnabled(!isLoading);
        tilProvince.setEnabled(!isLoading);
        tilDistrict.setEnabled(!isLoading);
        tilWard.setEnabled(!isLoading);
    }
}