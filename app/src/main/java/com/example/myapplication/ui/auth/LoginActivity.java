package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.api.AuthService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.request.LoginRequest;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.dto.response.LoginResponse;
import com.example.myapplication.util.ErrorUtils;
import com.example.myapplication.util.HubSpotChatManager;
import com.example.myapplication.util.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvError;
    private ProgressBar progressBar;
    private AuthService authService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initServices();
        setupListeners();

        // Check if we have a registered email to pre-fill
        String registeredEmail = getIntent().getStringExtra("registered_email");
        if (registeredEmail != null && !registeredEmail.isEmpty()) {
            etEmail.setText(registeredEmail);
            etPassword.requestFocus();
        }
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initServices() {
        authService = ApiClient.getRetrofitInstance(this).create(AuthService.class);
        tokenManager = new TokenManager(this);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Setup register link click listener
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);
        if (tvRegisterLink != null) {
            tvRegisterLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);
        hideError();

        // Validate inputs
        boolean isValid = true;
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Show loading state
        setLoading(true);

        // Create login request
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Make API call
        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulLogin(response.body());
                } else {
                    handleFailedLogin(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                String errorMessage = ErrorUtils.handleThrowable(t);
                showError(errorMessage);
                Log.e("LoginActivity", "Network error", t);
            }
        });
    }

    private void handleSuccessfulLogin(LoginResponse loginResponse) {
        // Save token
        tokenManager.saveToken(loginResponse.getAccessToken());
        // Set user info for HubSpot chat
        String email = etEmail.getText().toString().trim();
        HubSpotChatManager.getInstance().setUserInfo(email, loginResponse.getAccessToken());

        // Navigate to main activity with flag to check cart
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("check_cart_after_login", true);
        startActivity(intent);
        finish();
    }

    private void handleFailedLogin(Response<LoginResponse> response) {
        if (response.code() == 401) {
            showError("Invalid email or password");
        } else {
            ErrorResponse errorResponse = ErrorUtils.processError(response);
            showError("Login failed: " + errorResponse.getError());
            Log.e("LoginActivity", "Error: " + errorResponse.getError());
        }
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
        btnLogin.setEnabled(!isLoading);
        tilEmail.setEnabled(!isLoading);
        tilPassword.setEnabled(!isLoading);
    }
}