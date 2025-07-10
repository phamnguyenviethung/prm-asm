package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.service.CartNotificationService;
import com.example.myapplication.ui.dialog.CartNotificationDialog;
import com.example.myapplication.ui.widget.DraggableChatButton;
import com.example.myapplication.util.AuthManager;
import com.example.myapplication.util.HubSpotChatManager;
import com.example.myapplication.util.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements CartNotificationDialog.CartNotificationListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize HubSpot Chat SDK
        initializeHubSpotChat();

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this);

        // Check if user is logged in
        AuthManager.getInstance(this).checkLoginAndRedirect(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
        setupChatButton();

        // Handle navigation from notification
        handleNotificationNavigation();

        // Check cart after login if needed
        handleCartCheckAfterLogin();
    }

    private void initializeHubSpotChat() {
        try {
            HubSpotChatManager.getInstance().initialize(this);
            Log.d(TAG, "HubSpot Chat SDK initialization completed");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize HubSpot Chat SDK", e);
        }
    }

    private void setupChatButton() {
        DraggableChatButton fabChat = findViewById(R.id.fab_chat);
        // The DraggableChatButton handles its own click events
        // But we can override the click behavior if needed
        fabChat.setOnClickListener(v -> {
            Log.d(TAG, "Chat button clicked");
            openHubSpotChat();
        });
    }

    private void openHubSpotChat() {
        try {
            HubSpotChatManager.getInstance().openChat(this);
            Log.d(TAG, "HubSpot chat opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to open HubSpot chat", e);
        }
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Setup custom toolbar instead of action bar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Handle bottom navigation with custom listener to clear back stack
        navView.setOnItemSelectedListener(item -> {
            // Clear back stack and navigate to the selected destination
            navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            // Check if user is logged in before navigating to cart
            if (AuthManager.getInstance(this).isLoggedIn()) {
                navController.navigate(R.id.navigation_cart);
            } else {
                AuthManager.getInstance(this).checkLoginAndRedirect(this);
            }
            return true;
        } else if (item.getItemId() == R.id.action_test_cart_notification) {
            // Test cart notification (for debugging)
            testCartNotification();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void handleNotificationNavigation() {
        if (getIntent() != null && getIntent().getBooleanExtra("navigate_to_cart", false)) {
            // Navigate to cart when coming from notification
            navController.navigate(R.id.navigation_cart);
        }
    }

    private void handleCartCheckAfterLogin() {
        if (getIntent() != null && getIntent().getBooleanExtra("check_cart_after_login", false)) {
            // Use a small delay to ensure MainActivity is fully loaded
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Log.d(TAG, "Checking cart after login...");
                CartNotificationService.getInstance().checkCartAfterLogin(this);
            }, 1000);
        }
    }

    private void testCartNotification() {
        Log.d(TAG, "Testing cart notification...");
        CartNotificationService.getInstance().checkCartAfterLogin(this);
    }

    // CartNotificationDialog.CartNotificationListener implementation
    @Override
    public void onGoToCart() {
        navController.navigate(R.id.navigation_cart);
    }

    @Override
    public void onGoToHome() {
        navController.navigate(R.id.navigation_home);
    }

}
