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
import com.example.myapplication.util.AuthManager;
import com.example.myapplication.util.HubSpotChatManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize HubSpot Chat SDK
        initializeHubSpotChat();

        // Check if user is logged in
        AuthManager.getInstance(this).checkLoginAndRedirect(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
        setupChatButton();
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
        FloatingActionButton fabChat = findViewById(R.id.fab_chat);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

}

