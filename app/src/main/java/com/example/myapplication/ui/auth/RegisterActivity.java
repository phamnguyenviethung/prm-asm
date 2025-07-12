package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.R;
import com.example.myapplication.api.AuthService;
import com.example.myapplication.api.RegionService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.request.RegisterRequest;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.dto.response.RegisterResponse;
import com.example.myapplication.model.District;
import com.example.myapplication.model.Province;
import com.example.myapplication.model.Ward;
import com.example.myapplication.util.ErrorUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

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
    private AutoCompleteTextView actvProvince;
    private AutoCompleteTextView actvDistrict;
    private AutoCompleteTextView actvWard;

    private MaterialButton btnRegister;
    private TextView tvLoginLink;
    private TextView tvError;
    private ProgressBar progressBar;

    private AuthService authService;
    private RegionService regionService;

    private final MutableLiveData<List<Province>> provinces = new MutableLiveData<>();
    private final MutableLiveData<List<District>> districts = new MutableLiveData<>();
    private final MutableLiveData<List<Ward>> wards = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    // Selected items
    private Province selectedProvince;
    private District selectedDistrict;
    private Ward selectedWard;

    // Adapters
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;

    public LiveData<List<Province>> getProvinces() {
        return provinces;
    }

    public LiveData<List<District>> getDistricts() {
        return districts;
    }

    public LiveData<List<Ward>> getWards() {
        return wards;
    }

    public void loadProvinces() {
        regionService.getProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinces.setValue(response.body());
                    Log.d(TAG, "Provinces loaded: " + response.body().size());
                } else {
                    Log.e(TAG, "Error loading provinces: " + response.code());
                    error.setValue("Failed to load provinces");
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                Log.e(TAG, "Network error loading provinces", t);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void loadDistricts(String provinceId) {
        regionService.getDistricts(provinceId).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    districts.setValue(response.body());
                    Log.d(TAG, "Districts loaded: " + response.body().size());
                } else {
                    Log.e(TAG, "Error loading districts: " + response.code());
                    error.setValue("Failed to load districts");
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                Log.e(TAG, "Network error loading districts", t);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void loadWards(String districtId) {
        regionService.getWards(districtId).enqueue(new Callback<List<Ward>>() {
            @Override
            public void onResponse(Call<List<Ward>> call, Response<List<Ward>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wards.setValue(response.body());
                    Log.d(TAG, "Wards loaded: " + response.body().size());
                } else {
                    Log.e(TAG, "Error loading wards: " + response.code());
                    error.setValue("Failed to load wards");
                }
            }

            @Override
            public void onFailure(Call<List<Ward>> call, Throwable t) {
                Log.e(TAG, "Network error loading wards", t);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initServices();
        setupAdapters();
        setupListeners();
        loadProvinces();
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
        actvProvince = findViewById(R.id.actvProvince);
        actvDistrict = findViewById(R.id.actvDistrict);
        actvWard = findViewById(R.id.actvWard);

        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        // Initially disable district and ward selection
        tilDistrict.setEnabled(false);
        tilWard.setEnabled(false);
    }

    private void initServices() {
        authService = ApiClient.getRetrofitInstance(this).create(AuthService.class);
        regionService = ApiClient.getRetrofitInstance(this).create(RegionService.class);
    }

    private void setupAdapters() {
        // Setup province adapter
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvProvince.setAdapter(provinceAdapter);

        // Setup district adapter
        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvDistrict.setAdapter(districtAdapter);

        // Setup ward adapter
        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvWard.setAdapter(wardAdapter);

        // Observe data changes
        provinces.observe(this, provinceList -> {
            if (provinceList != null) {
                provinceAdapter.clear();
                provinceAdapter.addAll(provinceList);
                provinceAdapter.notifyDataSetChanged();
            }
        });

        districts.observe(this, districtList -> {
            if (districtList != null) {
                districtAdapter.clear();
                districtAdapter.addAll(districtList);
                districtAdapter.notifyDataSetChanged();

                // Clear ward selection when districts change
                selectedWard = null;
                actvWard.setText("", false);
                wardAdapter.clear();
                tilWard.setEnabled(false);
            }
        });

        wards.observe(this, wardList -> {
            if (wardList != null) {
                wardAdapter.clear();
                wardAdapter.addAll(wardList);
                wardAdapter.notifyDataSetChanged();
                tilWard.setEnabled(true);
            }
        });

        // Observe errors
        error.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(errorMessage);
            }
        });
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Province selection listener
        actvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedProvince = provinceAdapter.getItem(position);
                selectedDistrict = null;
                selectedWard = null;

                // Clear district and ward selections
                actvDistrict.setText("", false);
                actvWard.setText("", false);
                districtAdapter.clear();
                wardAdapter.clear();

                // Enable district selection and load districts
                tilDistrict.setEnabled(true);
                tilWard.setEnabled(false);

                if (selectedProvince != null) {
                    loadDistricts(selectedProvince.getId());
                }

                // Clear any previous errors
                tilProvince.setError(null);
            }
        });

        // District selection listener
        actvDistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = districtAdapter.getItem(position);
                selectedWard = null;

                // Clear ward selection
                actvWard.setText("", false);
                wardAdapter.clear();

                if (selectedDistrict != null) {
                    loadWards(selectedDistrict.getId());
                }

                // Clear any previous errors
                tilDistrict.setError(null);
            }
        });

        // Ward selection listener
        actvWard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedWard = wardAdapter.getItem(position);

                // Clear any previous errors
                tilWard.setError(null);
            }
        });
    }

    private void attemptRegister() {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String streetAddress = etStreetAddress.getText().toString().trim();

        // Clear previous errors
        clearAllErrors();
        hideError();

        // Validate inputs
        if (!validateInputs(email, password, fullName, phone, streetAddress)) {
            return;
        }

        // Show loading state
        showLoading(true);

        // Create register request with selected address data
        RegisterRequest registerRequest = new RegisterRequest(
                email, password, fullName, phone, streetAddress,
                selectedWard != null ? selectedWard.getName() : "",
                selectedProvince != null ? selectedProvince.getName() : "",
                selectedDistrict != null ? selectedDistrict.getName() : "");

        // Make API call
        authService.register(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulRegistration(response.body());
                } else {
                    handleFailedRegistration(response);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoading(false);
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
            String streetAddress) {
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

        if (selectedProvince == null) {
            tilProvince.setError("Province is required");
            isValid = false;
        }

        if (selectedDistrict == null) {
            tilDistrict.setError("District is required");
            isValid = false;
        }

        if (selectedWard == null) {
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

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);

        // Enable/disable all input fields
        tilEmail.setEnabled(!isLoading);
        tilPassword.setEnabled(!isLoading);
        tilFullName.setEnabled(!isLoading);
        tilPhone.setEnabled(!isLoading);
        tilStreetAddress.setEnabled(!isLoading);
        tilProvince.setEnabled(!isLoading);

        // Only enable these if they should be enabled
        if (!isLoading) {
            tilDistrict.setEnabled(selectedProvince != null);
            tilWard.setEnabled(selectedDistrict != null);
        } else {
            tilDistrict.setEnabled(false);
            tilWard.setEnabled(false);
        }
    }
}