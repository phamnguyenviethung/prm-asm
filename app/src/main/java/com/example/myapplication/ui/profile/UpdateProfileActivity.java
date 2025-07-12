package com.example.myapplication.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.api.CustomerService;
import com.example.myapplication.api.RegionService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.request.UpdateAddressRequest;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.model.Customer;
import com.example.myapplication.model.District;
import com.example.myapplication.model.Province;
import com.example.myapplication.model.Ward;
import com.example.myapplication.util.AuthManager;
import com.example.myapplication.util.ErrorUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";

    private Toolbar toolbar;
    private TextInputLayout tilEmail;
    private TextInputLayout tilFullName;
    private TextInputLayout tilPhone;
    private TextInputLayout tilStreetAddress;
    private TextInputLayout tilProvince;
    private TextInputLayout tilDistrict;
    private TextInputLayout tilWard;

    private TextInputEditText etEmail;
    private TextInputEditText etFullName;
    private TextInputEditText etPhone;
    private TextInputEditText etStreetAddress;
    private AutoCompleteTextView actvProvince;
    private AutoCompleteTextView actvDistrict;
    private AutoCompleteTextView actvWard;

    private MaterialButton btnSave;
    private TextView tvError;
    private ProgressBar progressBar;

    private CustomerService customerService;
    private RegionService regionService;
    private Customer currentCustomer;

    // Data for address selection
    private List<Province> provinces = new ArrayList<>();
    private List<District> districts = new ArrayList<>();
    private List<Ward> wards = new ArrayList<>();

    private Province selectedProvince;
    private District selectedDistrict;
    private Ward selectedWard;

    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Check if user is logged in
        if (!AuthManager.getInstance(this).isLoggedIn()) {
            finish();
            return;
        }

        initViews();
        initServices();
        setupToolbar();
        setupAdapters();
        setupListeners();

        // Load customer data and provinces
        loadCustomerData();
        loadProvinces();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilStreetAddress = findViewById(R.id.tilStreetAddress);
        tilProvince = findViewById(R.id.tilProvince);
        tilDistrict = findViewById(R.id.tilDistrict);
        tilWard = findViewById(R.id.tilWard);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etStreetAddress = findViewById(R.id.etStreetAddress);
        actvProvince = findViewById(R.id.actvProvince);
        actvDistrict = findViewById(R.id.actvDistrict);
        actvWard = findViewById(R.id.actvWard);

        btnSave = findViewById(R.id.btnSave);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        // Initially disable district and ward selection
        tilDistrict.setEnabled(false);
        tilWard.setEnabled(false);

        // Disable personal information fields as we're only updating address
        tilFullName.setEnabled(false);
        tilEmail.setEnabled(false);
        tilPhone.setEnabled(false);
    }

    private void initServices() {
        customerService = ApiClient.getRetrofitInstance(this).create(CustomerService.class);
        regionService = ApiClient.getRetrofitInstance(this).create(RegionService.class);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Update Address");
        }
    }

    private void setupAdapters() {
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinces);
        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, districts);
        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, wards);

        actvProvince.setAdapter(provinceAdapter);
        actvDistrict.setAdapter(districtAdapter);
        actvWard.setAdapter(wardAdapter);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveAddress());

        // Setup selection listeners for address fields
        actvProvince.setOnItemClickListener((parent, view, position, id) -> {
            selectedProvince = provinceAdapter.getItem(position);
            if (selectedProvince != null) {
                actvDistrict.setText("");
                actvWard.setText("");
                selectedDistrict = null;
                selectedWard = null;
                districts.clear();
                wards.clear();
                districtAdapter.notifyDataSetChanged();
                wardAdapter.notifyDataSetChanged();

                tilDistrict.setEnabled(true);
                tilWard.setEnabled(false);

                loadDistricts(selectedProvince.getId());
            }
        });

        actvDistrict.setOnItemClickListener((parent, view, position, id) -> {
            selectedDistrict = districtAdapter.getItem(position);
            if (selectedDistrict != null) {
                actvWard.setText("");
                selectedWard = null;
                wards.clear();
                wardAdapter.notifyDataSetChanged();

                tilWard.setEnabled(true);

                loadWards(selectedDistrict.getId());
            }
        });

        actvWard.setOnItemClickListener((parent, view, position, id) -> {
            selectedWard = wardAdapter.getItem(position);
        });
    }

    private void loadCustomerData() {
        showLoading(true);
        String token = AuthManager.getInstance(this).tokenManager.getToken();

        customerService.getCustomerProfile().enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentCustomer = response.body();
                    displayCustomerData(currentCustomer);
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    showError(errorResponse != null ? errorResponse.getError() : "Failed to load profile");
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to load customer profile", t);
            }
        });
    }

    private void displayCustomerData(Customer customer) {
        etFullName.setText(customer.getFullName());
        etEmail.setText(customer.getEmail());
        etPhone.setText(customer.getPhone());
        etStreetAddress.setText(customer.getStreetAddress());

        // We'll set the province/district/ward after loading the data
        String customerProvince = customer.getProvince();
        String customerDistrict = customer.getDistrict();
        String customerWard = customer.getWard();

        // Find and set the correct province in the dropdown
        for (Province province : provinces) {
            if (province.getName().equals(customerProvince)) {
                actvProvince.setText(province.getName(), false);
                selectedProvince = province;
                loadDistricts(province.getId());
                break;
            }
        }
    }

    private void loadProvinces() {
        showLoading(true);
        regionService.getProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinces.clear();
                    provinces.addAll(response.body());
                    provinceAdapter.notifyDataSetChanged();

                    // If we already have customer data, try to match the province
                    if (currentCustomer != null) {
                        for (Province province : provinces) {
                            if (province.getName().equals(currentCustomer.getProvince())) {
                                actvProvince.setText(province.getName(), false);
                                selectedProvince = province;
                                loadDistricts(province.getId());
                                break;
                            }
                        }
                    }
                    showLoading(false);
                } else {
                    showLoading(false);
                    showError("Failed to load provinces");
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to load provinces", t);
            }
        });
    }

    private void loadDistricts(String provinceId) {
        Log.i("Districts", "Loading districts for province ID: " + provinceId);
        showLoading(true);
        regionService.getDistricts(provinceId).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    districts.clear();
                    districts.addAll(response.body());
                    districtAdapter.notifyDataSetChanged();
                    Log.i("Districts", "Loaded " + districts.size() + " districts");
                    tilDistrict.setEnabled(true);

                    // If we already have customer data, try to match the district
                    if (currentCustomer != null) {
                        for (District district : districts) {
                            if (district.getName().equals(currentCustomer.getDistrict())) {
                                actvDistrict.setText(district.getName(), false);
                                selectedDistrict = district;
                                loadWards(district.getId());
                                break;
                            }
                        }
                    }
                    showLoading(false);
                } else {
                    showLoading(false);
                    showError("Failed to load districts");
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to load districts", t);
            }
        });
    }

    private void loadWards(String districtId) {
        showLoading(true);
        regionService.getWards(districtId).enqueue(new Callback<List<Ward>>() {
            @Override
            public void onResponse(Call<List<Ward>> call, Response<List<Ward>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wards.clear();
                    wards.addAll(response.body());
                    wardAdapter.notifyDataSetChanged();
                    tilWard.setEnabled(true);

                    // If we already have customer data, try to match the ward
                    if (currentCustomer != null) {
                        for (Ward ward : wards) {
                            if (ward.getName().equals(currentCustomer.getWard())) {
                                actvWard.setText(ward.getName(), false);
                                selectedWard = ward;
                                break;
                            }
                        }
                    }
                    showLoading(false);
                } else {
                    showLoading(false);
                    showError("Failed to load wards");
                }
            }

            @Override
            public void onFailure(Call<List<Ward>> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to load wards", t);
            }
        });
    }

    private void saveAddress() {
        // Get input values
        String streetAddress = etStreetAddress.getText().toString().trim();

        // Clear previous errors
        clearAllErrors();
        hideError();

        // Validate inputs
        if (!validateInputs(streetAddress)) {
            return;
        }

        // Show loading state
        showLoading(true);

        // Create update address request
        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest(
                streetAddress,
                selectedWard != null ? selectedWard.getName() : "",
                selectedProvince != null ? selectedProvince.getName() : "",
                selectedDistrict != null ? selectedDistrict.getName() : "");

        // Make API call to update address
        String token = AuthManager.getInstance(this).tokenManager.getToken();
        customerService.updateCustomerProfile("Bearer " + token, updateAddressRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProfileActivity.this, "Address updated successfully", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    showError(errorResponse != null ? errorResponse.getError() : "Failed to update address");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to update address", t);
            }
        });
    }

    private void clearAllErrors() {
        tilStreetAddress.setError(null);
        tilProvince.setError(null);
        tilDistrict.setError(null);
        tilWard.setError(null);
    }

    private boolean validateInputs(String streetAddress) {
        boolean isValid = true;

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

    private void showError(String errorMessage) {
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);

        // Enable/disable address fields
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}