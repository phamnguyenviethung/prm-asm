package com.example.myapplication.ui.productdetail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.activity.OnBackPressedCallback;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProductDetailBinding;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.model.ProductDetail;
import com.example.myapplication.model.ProductVariant;
import com.example.myapplication.model.VariantDetail;
import com.example.myapplication.model.VariantOption;
import com.example.myapplication.util.AuthManager;
import com.example.myapplication.util.ErrorUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.myapplication.config.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private TextView tvQuantity;
    private ImageButton btnDecreaseQuantity;
    private ImageButton btnIncreaseQuantity;
    private TextView tvSelectedPrice;
    private TextView tvTotalPrice;
    private LinearLayout bottomAddToCartSection;
    
    private ProductImageAdapter imageAdapter;
    private ProductOptionAdapter optionAdapter;
    
    private final Map<String, String> selectedOptions = new HashMap<>();

    // Quantity management
    private int selectedQuantity = 1;

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
        tvQuantity = binding.tvQuantity;
        btnDecreaseQuantity = binding.btnDecreaseQuantity;
        btnIncreaseQuantity = binding.btnIncreaseQuantity;
        tvSelectedPrice = binding.tvSelectedPrice;
        tvTotalPrice = binding.tvTotalPrice;
        bottomAddToCartSection = binding.bottomAddToCartSection;

        btnAddToCart.setOnClickListener(v -> addToCart());
        btnDecreaseQuantity.setOnClickListener(v -> decreaseQuantity());
        btnIncreaseQuantity.setOnClickListener(v -> increaseQuantity());
    }
    
    private void setupAdapters() {
        // Setup image adapter
        imageAdapter = new ProductImageAdapter();
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

            // Initialize bottom section price
            updateBottomSectionPrice();

            showContent();
        }
    }
    
    @Override
    public void onOptionSelected(String optionId, String valueId) {
        selectedOptions.put(optionId, valueId);
        updateUIBasedOnSelection();
    }

    private void decreaseQuantity() {
        if (selectedQuantity > 1) {
            selectedQuantity--;
            updateQuantityDisplay();
        }
    }

    private void increaseQuantity() {
        // Get max available quantity for selected variant
        int maxQuantity = getMaxAvailableQuantity();
        if (selectedQuantity < maxQuantity) {
            selectedQuantity++;
            updateQuantityDisplay();
        } else {
            Toast.makeText(requireContext(), "Maximum available quantity: " + maxQuantity, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityDisplay() {
        tvQuantity.setText(String.valueOf(selectedQuantity));

        // Update button states
        btnDecreaseQuantity.setEnabled(selectedQuantity > 1);
        btnIncreaseQuantity.setEnabled(selectedQuantity < getMaxAvailableQuantity());

        // Update total price in bottom section
        updateBottomSectionPrice();
    }

    private int getMaxAvailableQuantity() {
        ProductDetail product = viewModel.getProductDetail().getValue();
        if (product == null) return 1;

        // Find the matching variant
        String variantId = findSelectedVariantId();
        if (variantId != null) {
            for (ProductVariant variant : product.getVariants()) {
                if (variant.getId().equals(variantId)) {
                    return Math.max(1, variant.getAvailableQuantity());
                }
            }
        }

        // Fallback to product's available quantity
        return Math.max(1, product.getAvailableQuantity());
    }

    private void updateUIBasedOnSelection() {
        ProductDetail product = viewModel.getProductDetail().getValue();
        if (product == null) return;

        // Find the matching variant
        String variantId = findSelectedVariantId();
        ProductVariant selectedVariant = null;

        if (variantId != null) {
            for (ProductVariant variant : product.getVariants()) {
                if (variant.getId().equals(variantId)) {
                    selectedVariant = variant;
                    break;
                }
            }
        }

        // Update price if a specific variant is selected
        if (selectedVariant != null) {
            String priceText = selectedVariant.getFinalPrice() + " VNĐ";
            tvPrice.setText(priceText);

            // Update availability
            if (selectedVariant.isAvailable() && selectedVariant.getAvailableQuantity() > 0) {
                tvAvailability.setText("In Stock (" + selectedVariant.getAvailableQuantity() + " available)");
                tvAvailability.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                btnAddToCart.setEnabled(true);
            } else {
                tvAvailability.setText("Out of Stock");
                tvAvailability.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btnAddToCart.setEnabled(false);
            }
        } else {
            // Show price range if no specific variant is selected
            String priceText;
            if (product.getFinalStartingPrice() == product.getFinalMaxPrice()) {
                priceText = product.getFinalStartingPrice() + " VNĐ";
            } else {
                priceText = product.getFinalStartingPrice() + " VNĐ - " +
                        product.getFinalMaxPrice() + " VNĐ";
            }
            tvPrice.setText(priceText);

            // Reset availability to general product availability
            if (product.isAvailable() && product.getAvailableQuantity() > 0) {
                tvAvailability.setText("In Stock (" + product.getAvailableQuantity() + " available)");
                tvAvailability.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvAvailability.setText("Out of Stock");
                tvAvailability.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            // Enable/disable add to cart based on whether all options are selected
            btnAddToCart.setEnabled(product.isAvailable() &&
                (product.getOptions().isEmpty() || selectedOptions.size() == product.getOptions().size()));
        }

        // Update quantity display and controls
        updateQuantityDisplay();
    }

    private void updateBottomSectionPrice() {
        ProductDetail product = viewModel.getProductDetail().getValue();
        if (product == null) return;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        // Find the matching variant
        String variantId = findSelectedVariantId();
        int unitPrice;

        if (variantId != null) {
            ProductVariant selectedVariant = null;
            for (ProductVariant variant : product.getVariants()) {
                if (variant.getId().equals(variantId)) {
                    selectedVariant = variant;
                    break;
                }
            }
            unitPrice = selectedVariant != null ? selectedVariant.getFinalPrice() : product.getFinalStartingPrice();
        } else {
            unitPrice = product.getFinalStartingPrice();
        }

        // Update unit price display
        String unitPriceText = formatter.format(unitPrice) + " VND";
        tvSelectedPrice.setText(unitPriceText);

        // Update total price if quantity > 1
        if (selectedQuantity > 1) {
            int totalPrice = unitPrice * selectedQuantity;
            String totalPriceText = "Total: " + formatter.format(totalPrice) + " VND";
            tvTotalPrice.setText(totalPriceText);
            tvTotalPrice.setVisibility(View.VISIBLE);
        } else {
            tvTotalPrice.setVisibility(View.GONE);
        }
    }
    
    private void addToCart() {
        // Check if user is still logged in
        if (!AuthManager.getInstance(requireContext()).isLoggedIn()) {
            AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());
            return;
        }

        // Get selected product variant
        String variantId = findSelectedVariantId();

        if (variantId == null) {
            // Show specific error message based on the issue
            ProductDetail product = viewModel.getProductDetail().getValue();
            if (product != null && !product.getOptions().isEmpty()) {
                if (selectedOptions.size() == 0) {
                    Toast.makeText(requireContext(), "Please select product options", Toast.LENGTH_SHORT).show();
                } else if (selectedOptions.size() < product.getOptions().size()) {
                    Toast.makeText(requireContext(), "Please select all required options", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Selected combination is not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Product variant not found", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Get product ID from arguments
        String productId = getArguments().getString(ARG_PRODUCT_ID);

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        btnAddToCart.setEnabled(false);
        btnAddToCart.setText("Adding...");

        // Create request object with selected quantity
        com.example.myapplication.dto.request.Cart.AddItemToCartRequest request =
            new com.example.myapplication.dto.request.Cart.AddItemToCartRequest(selectedQuantity);

        // Call API to add item to cart
        ApiClient.getRetrofitInstance(requireContext())
            .create(com.example.myapplication.api.CartService.class)
            .addItemToCart(variantId, request)
            .enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    progressBar.setVisibility(View.GONE);
                    btnAddToCart.setEnabled(true);
                    btnAddToCart.setText("Add to Cart");

                    if (response.isSuccessful()) {
                        String successMsg = "✓ Added " + selectedQuantity + " item" +
                            (selectedQuantity > 1 ? "s" : "") + " to cart!";
                        Toast.makeText(requireContext(), successMsg, Toast.LENGTH_SHORT).show();

                        // Optional: Navigate to cart or show cart icon animation
                        // You can uncomment the line below to navigate to cart after adding
                        // Navigation.findNavController(requireView()).navigate(R.id.navigation_cart);

                    } else {
                        ErrorResponse errorResponse = ErrorUtils.processError(response);
                        String errorMsg = "Failed to add to cart: " + errorResponse.getError();
                        Log.e("ProductDetailFragment", "Error adding to cart: " + errorResponse.getError());
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnAddToCart.setEnabled(true);
                    btnAddToCart.setText("Add to Cart");
                    Toast.makeText(requireContext(), "Network error. Please try again.",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String findSelectedVariantId() {
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

        // Find the variant that matches all selected options
        for (ProductVariant variant : product.getVariants()) {
            if (variantMatchesSelectedOptions(variant, selectedOptions)) {
                return variant.getId();
            }
        }

        return null;
    }

    private boolean variantMatchesSelectedOptions(ProductVariant variant, Map<String, String> selectedOptions) {
        // If variant has no option values, we can't match it properly
        List<VariantDetail> variantDetails = variant.getOptionValues();
        if (variantDetails == null || variantDetails.isEmpty()) {
            return false;
        }

        // First, check if the variant has the same number of details as selected options
        if (variantDetails.size() != selectedOptions.size()) {
            return false;
        }

        // Create a map of the variant's option-value pairs for easier comparison
        Map<String, String> variantOptions = new HashMap<>();
        for (VariantDetail detail : variantDetails) {
            variantOptions.put(detail.getOptionId(), detail.getValueId());
        }

        // Check if all selected options match the variant's details
        for (Map.Entry<String, String> selectedOption : selectedOptions.entrySet()) {
            String optionId = selectedOption.getKey();
            String valueId = selectedOption.getValue();

            // Check if this option exists in the variant and has the same value
            if (!variantOptions.containsKey(optionId) || !variantOptions.get(optionId).equals(valueId)) {
                return false;
            }
        }

        return true;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.GONE);
        bottomAddToCartSection.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.VISIBLE);
        bottomAddToCartSection.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        bottomAddToCartSection.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(errorMessage);
    }
    
    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        bottomAddToCartSection.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText("Product not found");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}












