package com.example.myapplication.ui.checkout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCheckoutBinding;
import com.example.myapplication.dto.response.Cart.CartItem;
import com.example.myapplication.dto.response.OrderDetailResponse;
import com.example.myapplication.model.Customer;
import com.example.myapplication.util.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private CheckoutViewModel checkoutViewModel;
    
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;
    private TextView tvProvince;
    private TextView tvDistrict;
    private TextView tvWard;
    private RadioGroup rgShippingMethod;
    private RadioGroup rgPaymentMethod;
    private TextInputEditText etOrderNote;

    // Customer data for auto-population
    private Customer currentCustomer;

    // Cart items container
    private LinearLayout layoutCartItems;
    private TextView tvSubtotal;
    private TextView tvTotal;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Check if user is logged in
        AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupClickListeners();
        observeViewModel();
        loadOrderSummary();
        loadCustomerProfile();

        return root;
    }

    private void initViews() {
        etFullName = binding.etFullName;
        etEmail = binding.etEmail;
        etPhone = binding.etPhone;
        etAddress = binding.etAddress;
        tvProvince = binding.tvProvince;
        tvDistrict = binding.tvDistrict;
        tvWard = binding.tvWard;
        rgShippingMethod = binding.rgShippingMethod;
        rgPaymentMethod = binding.rgPaymentMethod;
        tvSubtotal = binding.tvSubtotal;
        tvTotal = binding.tvTotal;
        btnPlaceOrder = binding.btnPlaceOrder;
        progressBar = binding.progressBar;
        etOrderNote = binding.etOrderNote;

        // Initialize cart items container
        layoutCartItems = binding.layoutCartItems;
    }



    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(v -> {
            if (validateInputs()) {
                placeOrder();
            }
        });
    }

    private void observeViewModel() {
        checkoutViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                btnPlaceOrder.setEnabled(!isLoading);
                btnPlaceOrder.setText(isLoading ? "Processing..." : "Place Order");
            }
        });

        checkoutViewModel.getOrderSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // Get order detail and show success dialog
                OrderDetailResponse orderDetail = checkoutViewModel.getOrderDetail().getValue();
                if (orderDetail != null) {
                    showOrderSuccessDialog(orderDetail);
                } else {
                    // Fallback to home if no order detail
                    Toast.makeText(requireContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_home);
                }
            }
        });

        checkoutViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        checkoutViewModel.getSubtotal().observe(getViewLifecycleOwner(), subtotal -> {
            if (subtotal != null) {
                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                tvSubtotal.setText(formatter.format(subtotal) + "đ");
                updateTotal();
            }
        });

        // Observe cart items
        checkoutViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null) {
                populateCartItems(cartItems);
            }
        });

        // Observe customer data
        checkoutViewModel.getCustomerData().observe(getViewLifecycleOwner(), customer -> {
            if (customer != null) {
                populateCustomerInfo(customer);
            }
        });
    }

    private void loadOrderSummary() {
        checkoutViewModel.loadCartSummary();
    }



    private void loadCustomerProfile() {
        checkoutViewModel.loadCustomerProfile();
    }



    private void populateCartItems(List<CartItem> cartItems) {
        layoutCartItems.removeAllViews();

        if (cartItems == null || cartItems.isEmpty()) {
            TextView emptyView = new TextView(requireContext());
            emptyView.setText("Giỏ hàng trống");
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setTextColor(getResources().getColor(R.color.gray_600));
            layoutCartItems.addView(emptyView);
            return;
        }

        for (CartItem item : cartItems) {
            View itemView = createCartItemView(item);
            layoutCartItems.addView(itemView);
        }
    }

    private View createCartItemView(CartItem item) {
        View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_checkout_cart_simple, layoutCartItems, false);

        ImageView ivProductImage = itemView.findViewById(R.id.ivProductImage);
        TextView tvProductName = itemView.findViewById(R.id.tvProductName);
        TextView tvProductVariant = itemView.findViewById(R.id.tvProductVariant);
        TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
        TextView tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);

        // Set product name
        tvProductName.setText(item.getProductName());

        // Set quantity
        tvQuantity.setText("x" + item.getQuantity());

        // Set unit price (price per item, not total)
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvUnitPrice.setText(formatter.format(item.getPrice()) + "đ");

        // Load product image
        if (item.getThumbnail() != null && !item.getThumbnail().isEmpty()) {
            Glide.with(requireContext())
                    .load(item.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.placeholder_image);
        }

        return itemView;
    }

    private void populateCustomerInfo(Customer customer) {
        currentCustomer = customer;

        // Populate and disable basic info fields
        if (customer.getFullName() != null) {
            etFullName.setText(customer.getFullName());
        }
        etFullName.setEnabled(false);
        etFullName.setAlpha(0.6f); // Make it look disabled

        if (customer.getEmail() != null) {
            etEmail.setText(customer.getEmail());
        }
        etEmail.setEnabled(false);
        etEmail.setAlpha(0.6f);

        if (customer.getPhone() != null) {
            etPhone.setText(customer.getPhone());
        }
        etPhone.setEnabled(false);
        etPhone.setAlpha(0.6f);

        if (customer.getStreetAddress() != null) {
            etAddress.setText(customer.getStreetAddress());
        }
        etAddress.setEnabled(false);
        etAddress.setAlpha(0.6f);

        // Set region information as text
        if (customer.getProvince() != null) {
            tvProvince.setText(customer.getProvince());
        } else {
            tvProvince.setText("Chưa có thông tin");
        }

        if (customer.getDistrict() != null) {
            tvDistrict.setText(customer.getDistrict());
        } else {
            tvDistrict.setText("Chưa có thông tin");
        }

        if (customer.getWard() != null) {
            tvWard.setText(customer.getWard());
        } else {
            tvWard.setText("Chưa có thông tin");
        }
    }

    private void updateTotal() {
        Integer subtotal = checkoutViewModel.getSubtotal().getValue();
        if (subtotal != null) {
            int shippingFee = 25000; // Fixed shipping fee
            int total = subtotal + shippingFee;

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvTotal.setText("VND " + formatter.format(total) + "đ");
        }
    }

    private boolean validateInputs() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ và tên");
            etFullName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            etAddress.requestFocus();
            return false;
        }

        // Check if customer data is loaded
        if (currentCustomer == null) {
            Toast.makeText(requireContext(), "Đang tải thông tin khách hàng...", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        // Get order note
        String note = etOrderNote.getText().toString().trim();

        // Get selected payment method (default is 1 for COD)
        int paymentMethod = 1; // COD

        // Call ViewModel to checkout
        checkoutViewModel.checkout(paymentMethod, note);
    }

    private String getSelectedShippingMethod() {
        int selectedId = rgShippingMethod.getCheckedRadioButtonId();

        if (selectedId == R.id.rbStandardShipping) {
            return "STANDARD";
        }

        return "STANDARD"; // Default
    }

    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();

        if (selectedId == R.id.rbCashOnDelivery) {
            return "COD";
        }
        return "COD"; // Default
    }

    private void showOrderSuccessDialog(OrderDetailResponse orderDetail) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_order_success, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button btnGoHome = dialogView.findViewById(R.id.btnGoHome);
        Button btnViewOrder = dialogView.findViewById(R.id.btnViewOrder);

        btnGoHome.setOnClickListener(v -> {
            dialog.dismiss();
            Navigation.findNavController(requireView()).navigate(R.id.navigation_home);
        });

        btnViewOrder.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToOrderDetail(orderDetail);
        });

        dialog.show();
    }

    private void navigateToOrderDetail(OrderDetailResponse orderDetail) {
        // Use Navigation Component to navigate to OrderDetailFragment
        Bundle args = new Bundle();
        args.putSerializable("orderDetail", orderDetail);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_checkout_to_order_detail, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
