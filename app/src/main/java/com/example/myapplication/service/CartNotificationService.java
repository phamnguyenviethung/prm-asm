package com.example.myapplication.service;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.api.CartService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.response.Cart.GetCartResponse;
import com.example.myapplication.ui.dialog.CartNotificationDialog;
import com.example.myapplication.util.AuthManager;
import com.example.myapplication.util.NotificationHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartNotificationService {

    private static final String TAG = "CartNotificationService";
    private static CartNotificationService instance;

    private CartNotificationService() {
        // Empty constructor - CartService will be initialized when needed
    }

    public static synchronized CartNotificationService getInstance() {
        if (instance == null) {
            instance = new CartNotificationService();
        }
        return instance;
    }

    /**
     * Check cart and show notification/popup if items exist
     *
     * @param context   Application context
     * @param showPopup Whether to show popup dialog (true for after login, false for background check)
     */
    public void checkCartAndNotify(Context context, boolean showPopup) {
        Log.d(TAG, "checkCartAndNotify called with showPopup=" + showPopup + ", context=" + context.getClass().getSimpleName());

        String token = AuthManager.getInstance(context).tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Log.d(TAG, "No token available, skipping cart check");
            return;
        }

        Log.d(TAG, "Token found, proceeding with cart check");

        // Create notification channel
        NotificationHelper.createNotificationChannel(context);

        // Initialize CartService with context
        CartService cartService = ApiClient.getRetrofitInstance(context).create(CartService.class);

        cartService.getCart().enqueue(new Callback<GetCartResponse>() {
            @Override
            public void onResponse(Call<GetCartResponse> call, Response<GetCartResponse> response) {
                Log.d(TAG, "Cart API response received. Success: " + response.isSuccessful() + ", Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    GetCartResponse cartSummary = response.body();

                    if (cartSummary.getItems() != null && !cartSummary.getItems().isEmpty()) {
                        int itemCount = cartSummary.getItems().size();
                        Log.d(TAG, "Found " + itemCount + " items in cart");

                        // Show notification
                        NotificationHelper.showCartNotification(context, itemCount);

                        // Show popup if requested and context is FragmentActivity
                        if (showPopup) {
                            Log.d(TAG, "showPopup=true, checking context type: " + context.getClass().getSimpleName());
                            if (context instanceof FragmentActivity) {
                                Log.d(TAG, "Context is FragmentActivity, showing popup");
                                showCartPopup((FragmentActivity) context, itemCount);
                            } else {
                                Log.w(TAG, "Context is not FragmentActivity, cannot show popup");
                            }
                        } else {
                            Log.d(TAG, "showPopup=false, skipping popup");
                        }
                    } else {
                        Log.d(TAG, "Cart is empty");
                        // Cancel any existing notifications
                        NotificationHelper.cancelCartNotification(context);
                    }
                } else {
                    Log.e(TAG, "Failed to get cart summary: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetCartResponse> call, Throwable t) {
                Log.e(TAG, "Error checking cart: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Show cart popup dialog
     */
    private void showCartPopup(FragmentActivity activity, int itemCount) {
        try {
            Log.d(TAG, "Attempting to show cart popup with " + itemCount + " items");
            CartNotificationDialog dialog = CartNotificationDialog.newInstance(itemCount);

            // Set listener if activity implements the interface
            if (activity instanceof CartNotificationDialog.CartNotificationListener) {
                dialog.setCartNotificationListener((CartNotificationDialog.CartNotificationListener) activity);
            }

            dialog.show(activity.getSupportFragmentManager(), "CartNotificationDialog");
            Log.d(TAG, "Cart popup dialog shown successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error showing cart popup: " + e.getMessage(), e);
        }
    }

    /**
     * Check cart in background (without popup)
     */
    public void checkCartInBackground(Context context) {
        checkCartAndNotify(context, false);
    }

    /**
     * Check cart after login (with popup)
     */
    public void checkCartAfterLogin(Context context) {
        checkCartAndNotify(context, true);
    }

    /**
     * Clear cart notifications
     */
    public void clearNotifications(Context context) {
        NotificationHelper.cancelCartNotification(context);
    }
}
