package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.api.OrderService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.example.myapplication.dto.response.OrderDetailResponse;
import com.example.myapplication.dto.response.OrderHistoryItem;
import com.example.myapplication.util.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private OrderHistoryAdapter orderHistoryAdapter;
    private OrderService orderService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        orderService = ApiClient.getRetrofitInstance(getContext()).create(OrderService.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners();

        // Load order history when fragment is created
        dashboardViewModel.loadOrderHistory();

        return root;
    }

    private void setupRecyclerView() {
        orderHistoryAdapter = new OrderHistoryAdapter();
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrders.setAdapter(orderHistoryAdapter);

        orderHistoryAdapter.setOnOrderClickListener(this::loadOrderDetailAndNavigate);
    }

    private void setupObservers() {
        // Observe order history
        dashboardViewModel.getOrderHistory().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                orderHistoryAdapter.setOrders(orders);
                binding.recyclerViewOrders.setVisibility(View.VISIBLE);
                binding.layoutEmpty.setVisibility(View.GONE);
            } else {
                binding.recyclerViewOrders.setVisibility(View.GONE);
                binding.layoutEmpty.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        dashboardViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error
        dashboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            } else {
                binding.tvError.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        binding.ivRefresh.setOnClickListener(v -> {
            dashboardViewModel.refreshOrderHistory();
        });
    }

    private void loadOrderDetailAndNavigate(OrderHistoryItem orderItem) {
        String token = AuthManager.getInstance(requireContext()).tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);

        orderService.getOrderDetail("Bearer " + token, orderItem.getId()).enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Navigate to order detail
                    Bundle args = new Bundle();
                    args.putSerializable("orderDetail", response.body());

                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_dashboard_to_order_detail, args);
                } else {
                    Toast.makeText(requireContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}