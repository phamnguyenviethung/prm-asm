package com.example.myapplication.ui.checkout;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.CartService;
import com.example.myapplication.api.CustomerService;
import com.example.myapplication.api.RegionService;
import com.example.myapplication.api.CheckoutService;
import com.example.myapplication.api.OrderService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.request.CheckoutRequest;
import com.example.myapplication.dto.response.Cart.CartItem;
import com.example.myapplication.dto.response.Cart.GetCartResponse;
import com.example.myapplication.dto.response.CheckoutResponse;
import com.example.myapplication.dto.response.OrderDetailResponse;
import com.example.myapplication.model.Customer;
import com.example.myapplication.model.District;
import com.example.myapplication.model.Province;
import com.example.myapplication.model.Ward;
import com.example.myapplication.util.AuthManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutViewModel extends AndroidViewModel {

    private static final String TAG = "CheckoutViewModel";

    private final CartService cartService;
    private final RegionService regionService;
    private final CustomerService customerService;
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final MutableLiveData<Integer> subtotal = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>(false);

    // Region data
    private final MutableLiveData<List<Province>> provinces = new MutableLiveData<>();
    private final MutableLiveData<List<District>> districts = new MutableLiveData<>();
    private final MutableLiveData<List<Ward>> wards = new MutableLiveData<>();

    // Customer data
    private final MutableLiveData<Customer> customerData = new MutableLiveData<>();

    // Cart items data
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();

    // Order detail data
    private final MutableLiveData<OrderDetailResponse> orderDetail = new MutableLiveData<>();

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        cartService = ApiClient.getRetrofitInstance(application).create(CartService.class);
        regionService = ApiClient.getRetrofitInstance(application).create(RegionService.class);
        customerService = ApiClient.getRetrofitInstance(application).create(CustomerService.class);
        checkoutService = ApiClient.getRetrofitInstance(application).create(CheckoutService.class);
        orderService = ApiClient.getRetrofitInstance(application).create(OrderService.class);
    }

    public LiveData<Integer> getSubtotal() {
        return subtotal;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getOrderSuccess() {
        return orderSuccess;
    }

    public LiveData<List<Province>> getProvinces() {
        return provinces;
    }

    public LiveData<List<District>> getDistricts() {
        return districts;
    }

    public LiveData<List<Ward>> getWards() {
        return wards;
    }

    public LiveData<Customer> getCustomerData() {
        return customerData;
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<OrderDetailResponse> getOrderDetail() {
        return orderDetail;
    }

    public void loadCartSummary() {
        isLoading.setValue(true);
        error.setValue(null);

        cartService.getCart().enqueue(new Callback<GetCartResponse>() {
            @Override
            public void onResponse(Call<GetCartResponse> call, Response<GetCartResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    GetCartResponse cartResponse = response.body();
                    List<CartItem> items = cartResponse.getItems();

                    // Set cart items for UI
                    cartItems.setValue(items);

                    int totalPrice = 0;
                    if (items != null) {
                        for (CartItem item : items) {
                            totalPrice += item.getPrice() * item.getQuantity();
                        }
                    }

                    subtotal.setValue(totalPrice);
                    Log.d(TAG, "Cart summary loaded: " + totalPrice + " VND");
                } else {
                    String errorMessage = "Failed to load cart summary";
                    error.setValue(errorMessage);
                    Log.e(TAG, "Error loading cart summary: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetCartResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error loading cart summary", t);
            }
        });
    }

    public void placeOrder(String fullName, String email, String phone, String address,
                          String province, String district, String ward,
                          String shippingMethod, String paymentMethod) {
        isLoading.setValue(true);
        error.setValue(null);
        orderSuccess.setValue(false);

        // Simulate order placement - In real app, you would call an order API
        // For now, we'll just simulate a successful order after 2 seconds
        new android.os.Handler().postDelayed(() -> {
            isLoading.setValue(false);

            // Simulate success (in real app, this would be based on API response)
            boolean success = true; // You can change this to test error scenarios

            if (success) {
                orderSuccess.setValue(true);
                Log.d(TAG, "Order placed successfully");
                Log.d(TAG, "Customer: " + fullName + ", Email: " + email + ", Phone: " + phone);
                Log.d(TAG, "Address: " + address + ", " + ward + ", " + district + ", " + province);
                Log.d(TAG, "Shipping: " + shippingMethod + ", Payment: " + paymentMethod);
            } else {
                error.setValue("Failed to place order. Please try again.");
            }
        }, 2000);
    }

    public void loadCustomerProfile() {
        customerService.getMyProfile().enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    customerData.setValue(response.body());
                    Log.d(TAG, "Customer profile loaded: " + response.body().getFullName());
                } else {
                    Log.e(TAG, "Error loading customer profile: " + response.code());
                    error.setValue("Failed to load customer profile");
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.e(TAG, "Network error loading customer profile", t);
                error.setValue("Network error: " + t.getMessage());
            }
        });
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

    public void checkout(int paymentMethod, String note) {
        isLoading.setValue(true);
        error.setValue(null);
        orderSuccess.setValue(false);

        String token = AuthManager.getInstance(getApplication()).tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            error.setValue("User not authenticated");
            isLoading.setValue(false);
            return;
        }

        CheckoutRequest request = new CheckoutRequest(paymentMethod, note);

        checkoutService.checkout("Bearer " + token, request).enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckoutResponse checkoutResponse = response.body();
                    String orderId = checkoutResponse.getOrderId();

                    if (orderId != null && !orderId.isEmpty()) {
                        // Checkout successful, now get order details
                        Log.d(TAG, "Checkout successful, order ID: " + orderId);
                        loadOrderDetail(orderId);
                    } else {
                        isLoading.setValue(false);
                        error.setValue("Checkout successful but no order ID received");
                        Log.e(TAG, "No order ID in checkout response");
                    }
                } else {
                    isLoading.setValue(false);
                    String errorMessage = "Checkout failed";
                    error.setValue(errorMessage);
                    Log.e(TAG, "Error during checkout: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error during checkout", t);
            }
        });
    }

    private void loadOrderDetail(String orderId) {
        String token = AuthManager.getInstance(getApplication()).tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            isLoading.setValue(false);
            error.setValue("User not authenticated");
            return;
        }

        orderService.getOrderDetail("Bearer " + token, orderId).enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    orderDetail.setValue(response.body());
                    orderSuccess.setValue(true);
                    Log.d(TAG, "Order detail loaded successfully: " + response.body().getOrderCode());
                } else {
                    String errorMessage = "Failed to load order details";
                    error.setValue(errorMessage);
                    Log.e(TAG, "Error loading order detail: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error loading order detail", t);
            }
        });
    }
}
