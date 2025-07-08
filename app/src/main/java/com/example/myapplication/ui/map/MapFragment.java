package com.example.myapplication.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMapBinding;
import com.example.myapplication.util.AuthManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    
    private FragmentMapBinding binding;
    private MapViewModel mapViewModel;
    private MapView mapView;
    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private TextView tvStoreName;
    private TextView tvStoreAddress;
    private TextView tvStoreHours;
    private TextView tvStorePhone;
    private Button btnGetDirections;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Check if user is logged in
        AuthManager.getInstance(requireContext()).checkLoginAndRedirect(requireContext());

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        initMapView(savedInstanceState);
        observeViewModel();

        return root;
    }

    private void initViews() {
        mapView = binding.mapView;
        progressBar = binding.progressBar;
        tvStoreName = binding.tvStoreName;
        tvStoreAddress = binding.tvStoreAddress;
        tvStoreHours = binding.tvStoreHours;
        tvStorePhone = binding.tvStorePhone;
        btnGetDirections = binding.btnGetDirections;
        
        // Set up get directions button
        btnGetDirections.setOnClickListener(v -> openMapsNavigation());
    }

    private void initMapView(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void observeViewModel() {
        mapViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        mapViewModel.getStoreName().observe(getViewLifecycleOwner(), storeName -> {
            if (storeName != null) {
                tvStoreName.setText(storeName);
            }
        });

        mapViewModel.getStoreAddress().observe(getViewLifecycleOwner(), storeAddress -> {
            if (storeAddress != null) {
                tvStoreAddress.setText(storeAddress);
            }
        });
        
        mapViewModel.getStorePhone().observe(getViewLifecycleOwner(), storePhone -> {
            if (storePhone != null) {
                tvStorePhone.setText("Phone: " + storePhone);
            }
        });

        mapViewModel.getStoreLocation().observe(getViewLifecycleOwner(), storeLocation -> {
            if (storeLocation != null && googleMap != null) {
                addStoreMarker(storeLocation);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        Log.d(TAG, "Map is ready");

        // Configure map settings
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        // Observe store location and add marker when map is ready
        LatLng storeLocation = mapViewModel.getStoreLocation().getValue();
        if (storeLocation != null) {
            addStoreMarker(storeLocation);
        }
    }

    private void addStoreMarker(LatLng storeLocation) {
        if (googleMap == null) return;

        // Clear existing markers
        googleMap.clear();

        // Add store marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(storeLocation)
                .title(mapViewModel.getStoreName().getValue())
                .snippet(mapViewModel.getStoreAddress().getValue())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        googleMap.addMarker(markerOptions);

        // Move camera to store location with appropriate zoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 16f));

        Log.d(TAG, "Store marker added at: " + storeLocation.toString());
    }

    // MapView lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openMapsNavigation() {
        LatLng storeLocation = mapViewModel.getStoreLocation().getValue();
        if (storeLocation == null) return;
        
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + 
                storeLocation.latitude + "," + storeLocation.longitude);
        
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");
        
        // Resolve the intent before starting the activity
        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // If Google Maps isn't installed, open in browser instead
            Uri browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + 
                    storeLocation.latitude + "," + storeLocation.longitude);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
        }
    }
}
