package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentProfileBinding;
import com.example.myapplication.model.Customer;
import com.example.myapplication.util.AuthManager;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ProgressBar loadingProfile;
    private TextView errorText;
    private TextView textFullName;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textStreetAddress;
    private TextView textProvince;
    private TextView textDistrict;
    private TextView textWard;
    private MaterialButton btnEditProfile;
    private MaterialButton btnLogout;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // Check if user is logged in
        AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupListeners();
        observeViewModel();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh profile data when returning to this fragment
        profileViewModel.refreshCustomerData();
    }

    private void initViews() {
        loadingProfile = binding.loadingProfile;
        errorText = binding.errorText;
        textFullName = binding.textFullName;
        textEmail = binding.textEmail;
        textPhone = binding.textPhone;
        textStreetAddress = binding.textStreetAddress;
        textProvince = binding.textProvince;
        textDistrict = binding.textDistrict;
        textWard = binding.textWard;
        btnEditProfile = binding.btnEditProfile;
        btnLogout = binding.btnLogout;
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UpdateProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        profileViewModel.getCustomerData().observe(getViewLifecycleOwner(), this::displayCustomerData);

        profileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        profileViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void displayCustomerData(Customer customer) {
        if (customer != null) {
            textFullName.setText(customer.getFullName());
            textEmail.setText(customer.getEmail());
            textPhone.setText(customer.getPhone());
            textStreetAddress.setText(customer.getStreetAddress());
            textProvince.setText(customer.getProvince());
            textDistrict.setText(customer.getDistrict());
            textWard.setText(customer.getWard());

            // Show content
            binding.profileCard.setVisibility(View.VISIBLE);
            binding.addressCard.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        }
    }

    private void showLoading() {
        loadingProfile.setVisibility(View.VISIBLE);
        binding.profileCard.setVisibility(View.GONE);
        binding.addressCard.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingProfile.setVisibility(View.GONE);
    }

    private void showError(String errorMessage) {
        errorText.setText(errorMessage);
        errorText.setVisibility(View.VISIBLE);
        binding.profileCard.setVisibility(View.GONE);
        binding.addressCard.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}