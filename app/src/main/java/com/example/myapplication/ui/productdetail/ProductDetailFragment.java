package com.example.myapplication.ui.productdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProductDetailBinding;
import com.example.myapplication.model.ProductDetail;
import com.example.myapplication.model.VariantOption;
import com.example.myapplication.util.AuthManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductDetailFragment extends Fragment implements ProductOptionAdapter.OnOptionSelectedListener {

    private FragmentProductDetailBinding binding;
    private ProductDetailViewModel viewModel;
    private ProgressBar progressBar;
    private TextView tvError;
    private TextView tvEmptyState;
    private View contentScrollView;
    
    private ViewPager2 viewPagerImages;
    private TabLayout tabLayoutIndicator;
    private TextView tvProductName;
    private TextView tvVendor;
    private TextView tvCategory;
    private TextView tvPrice;
    private TextView tvPromotionDescription;
    private TextView tvAvailability;
    private TextView tvSoldQuantity;
    private TextView tvDescription;
    private RecyclerView rvOptions;
    private Button btnAddToCart;
    
    private ProductImageAdapter imageAdapter;
    private ProductOptionAdapter optionAdapter;
    
    private final Map<String, String> selectedOptions = new HashMap<>();
    
    private static final String ARG_PRODUCT_ID = "productId";
    
    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Check if user is logged in
        AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupAdapters();
        observeViewModel();
        setupToolbar();
        handleBackPress();
        
        // Load product detail
        String productId = getArguments().getString(ARG_PRODUCT_ID);
        if (productId != null) {
            viewModel.loadProductDetail(productId);
        } else {
            showError("Product ID not found");
        }

        return root;
    }

    private void setupToolbar() {
        // If using a toolbar, set up the back button
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void handleBackPress() {
        // Handle back button press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), 
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    navigateBack();
                }
            });
    }

    private void navigateBack() {
        // Get the NavController and navigate back to home
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack(R.id.navigation_home, false);
    }

    private void initViews() {
        progressBar = binding.progressBar;
        tvError = binding.tvError;
        tvEmptyState = binding.tvEmptyState;
        contentScrollView = binding.contentScrollView;
        
        viewPagerImages = binding.viewPagerImages;
        tabLayoutIndicator = binding.tabLayoutIndicator;
        tvProductName = binding.tvProductName;
        tvVendor = binding.tvVendor;
        tvCategory = binding.tvCategory;
        tvPrice = binding.tvPrice;
        tvPromotionDescription = binding.tvPromotionDescription;
        tvAvailability = binding.tvAvailability;
        tvSoldQuantity = binding.tvSoldQuantity;
        tvDescription = binding.tvDescription;
        rvOptions = binding.rvOptions;
        btnAddToCart = binding.btnAddToCart;
        
        btnAddToCart.setOnClickListener(v -> addToCart());
    }
    
    private void setupAdapters() {
        // Setup image adapter
        imageAdapter = new ProductImageAdapter(requireContext());
        viewPagerImages.setAdapter(imageAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayoutIndicator, viewPagerImages, (tab, position) -> {
            // No text for tabs
        }).attach();
        
        // Setup options adapter
        optionAdapter = new ProductOptionAdapter(requireContext(), this);
        rvOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOptions.setAdapter(optionAdapter);
    }

    private void observeViewModel() {
        viewModel.getProductDetail().observe(getViewLifecycleOwner(), this::displayProductDetail);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoading();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
        
        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty) {
                showEmptyState();
            }
        });
    }
    
    private void displayProductDetail(ProductDetail product) {
        if (product != null) {
            // Set product name and basic info
            tvProductName.setText(product.getName());
            tvVendor.setText(product.getVendor());
            tvCategory.setText("Category: " + product.getCategoryName());
            
            // Set price information
            String priceText;
            if (product.getFinalStartingPrice() == product.getFinalMaxPrice()) {
                priceText = product.getFinalStartingPrice() + " VNĐ";
            } else {
                priceText = product.getFinalStartingPrice() + " VNĐ - " +
                        product.getFinalMaxPrice() + " VNĐ";
            }
            tvPrice.setText(priceText);
            
            // Set promotion description if available
            if (product.getPromotionDescription() != null && !product.getPromotionDescription().isEmpty()) {
                tvPromotionDescription.setVisibility(View.VISIBLE);
                tvPromotionDescription.setText(product.getPromotionDescription());
            } else {
                tvPromotionDescription.setVisibility(View.GONE);
            }
            
            // Set availability information
            String availabilityText = product.isAvailable() ? 
                    "In Stock (" + product.getAvailableQuantity() + " available)" : 
                    "Out of Stock";
            tvAvailability.setText(availabilityText);
            
            // Set sold quantity if available
            if (product.getSoldQuantity() != null && product.getSoldQuantity() > 0) {
                tvSoldQuantity.setVisibility(View.VISIBLE);
                tvSoldQuantity.setText(product.getSoldQuantity() + " sold");
            } else {
                tvSoldQuantity.setVisibility(View.GONE);
            }
            
            // Set product description
            tvDescription.setText(product.getDescription());
            
            // Set product images
            imageAdapter.setImages(product.getImages());
            
            // Set product options/variants
            optionAdapter.setOptions(product.getOptions());
            
            // Enable/disable add to cart button based on availability
            btnAddToCart.setEnabled(product.isAvailable());
            
            showContent();
        }
    }
    
    @Override
    public void onOptionSelected(String optionId, String valueId) {
        selectedOptions.put(optionId, valueId);
        // You could update price or availability based on selected options here
    }
    
    private void addToCart() {
        // Get selected product variant
        String variantId = findSelectedVariantId();
        
        if (variantId == null) {
            Toast.makeText(requireContext(), "Please select all options", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get product ID from arguments
        String productId = getArguments().getString(ARG_PRODUCT_ID);
        
        // Call ViewModel to add to cart
        viewModel.addToCart(productId, variantId, 1);
        
        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show();
    }

    private String findSelectedVariantId() {
        // This is a simplified example. In a real app, you would need to match
        // the selected options with available variants to find the correct variant ID.
        
        // Check if all options have been selected
        ProductDetail product = viewModel.getProductDetail().getValue();
        if (product == null) return null;
        
        // If there are no options, return the first variant ID
        if (product.getOptions().isEmpty() && !product.getVariants().isEmpty()) {
            return product.getVariants().get(0).getId();
        }
        
        // Check if all options have been selected
        if (selectedOptions.size() != product.getOptions().size()) {
            return null;
        }
        
        // In a real app, you would match the selected options with variants
        // For simplicity, we'll just return the first variant ID
        if (!product.getVariants().isEmpty()) {
            return product.getVariants().get(0).getId();
        }
        
        return null;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(errorMessage);
    }
    
    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText("Product not found");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}







