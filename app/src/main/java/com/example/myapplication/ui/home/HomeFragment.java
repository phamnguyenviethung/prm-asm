package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.AuthManager;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ProductAdapter productAdapter;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Check if user is logged in
        AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupRecyclerView();
        observeViewModel();
        setupSwipeRefresh();

        return root;
    }

    private void initViews() {
        rvProducts = binding.rvProducts;
        progressBar = binding.progressBar;
        tvError = binding.tvError;
        swipeRefresh = binding.swipeRefresh;
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            // Check if user is still logged in before refreshing
            if (AuthManager.getInstance(requireContext()).isLoggedIn()) {
                homeViewModel.loadProducts();
            } else {
                swipeRefresh.setRefreshing(false);
                AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());
            }
        });
    }

    private void observeViewModel() {
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProducts(products);
            showContent();
        });

        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                if (!swipeRefresh.isRefreshing()) {
                    showLoading();
                }
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        homeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        rvProducts.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        rvProducts.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        rvProducts.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(errorMessage);
    }

    @Override
    public void onProductClick(Product product) {
        // Check if user is still logged in before handling click
        if (AuthManager.getInstance(requireContext()).isLoggedIn()) {
            Toast.makeText(getContext(), "Product clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to product details page
        } else {
            AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}