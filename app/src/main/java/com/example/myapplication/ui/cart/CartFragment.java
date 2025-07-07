package com.example.myapplication.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCartBinding;
import com.example.myapplication.dto.response.Cart.CartItem;
import com.example.myapplication.util.AuthManager;

import java.text.NumberFormat;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener {

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    private RecyclerView rvCartItems;
    private ProgressBar progressBar;
    private TextView tvError;
    private TextView tvTotalQuantity;
    private TextView tvTotalPrice;
    private LinearLayout layoutEmptyCart;
    private LinearLayout layoutCartSummary;
    private SwipeRefreshLayout swipeRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Check if user is logged in
        AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupRecyclerView();
        observeViewModel();
        setupSwipeRefresh();

        return root;
    }

    private void initViews() {
        rvCartItems = binding.rvCartItems;
        progressBar = binding.progressBar;
        tvError = binding.tvError;
        tvTotalQuantity = binding.tvTotalQuantity;
        tvTotalPrice = binding.tvTotalPrice;
        layoutEmptyCart = binding.layoutEmptyCart;
        layoutCartSummary = binding.layoutCartSummary;
        swipeRefresh = binding.swipeRefresh;
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            // Check if user is still logged in before refreshing
            if (AuthManager.getInstance(requireContext()).isLoggedIn()) {
                cartViewModel.loadCart();
            } else {
                swipeRefresh.setRefreshing(false);
                AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());
            }
        });
    }

    private void observeViewModel() {
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            cartAdapter.setCartItems(items);
        });

        cartViewModel.getTotalQuantity().observe(getViewLifecycleOwner(), totalQuantity -> {
            if (totalQuantity != null) {
                String quantityText = totalQuantity + " item" + (totalQuantity != 1 ? "s" : "");
                tvTotalQuantity.setText(quantityText);
            }
        });

        cartViewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> {
            if (totalPrice != null) {
                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                String priceText = formatter.format(totalPrice) + " VND";
                tvTotalPrice.setText(priceText);
            }
        });

        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                swipeRefresh.setRefreshing(isLoading);
            }
        });

        cartViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        cartViewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty != null) {
                layoutEmptyCart.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                layoutCartSummary.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                rvCartItems.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        // Check if user is still logged in before making changes
        if (AuthManager.getInstance(requireContext()).isLoggedIn()) {
            cartViewModel.updateItemQuantity(item.getVariantId(), newQuantity);
        } else {
            AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());
        }
    }

    @Override
    public void onItemRemoved(CartItem item) {
        // Check if user is still logged in before making changes
        if (AuthManager.getInstance(requireContext()).isLoggedIn()) {
            cartViewModel.removeItem(item.getVariantId());
            Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
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
