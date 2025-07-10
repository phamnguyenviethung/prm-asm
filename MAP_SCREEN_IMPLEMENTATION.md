# Map Screen Implementation Guide

## Overview
This document explains the implementation of the Map screen feature that displays your store location at "103 Hoa Lan, Phường 7, Phú Nhuận, Hồ Chí Minh, Vietnam" as a fourth navigation item in your Android app.

## What's Been Implemented

### ✅ Core Implementation
- **Google Maps SDK Dependencies**: Added to `app/build.gradle.kts`
- **Map Fragment**: `MapFragment.java` - Displays Google Maps with store location
- **Map ViewModel**: `MapViewModel.java` - Manages map data and store information
- **Map Layout**: `fragment_map.xml` - UI layout with map view and store info card
- **Navigation Integration**: Added as fourth tab in bottom navigation
- **Permissions**: Added location permissions to AndroidManifest.xml

### ✅ UI Components
- **Bottom Navigation**: Updated to include Map tab with map icon
- **Map View**: Google Maps integration with store marker
- **Store Info Card**: Displays store name, address, and hours
- **Loading Indicator**: Shows while map is loading

### ✅ Features
- **Store Marker**: Red marker at exact store location (10.7969, 106.6761)
- **Camera Position**: Automatically centers on store with appropriate zoom (level 16)
- **Marker Info**: Tap marker to see store name and address
- **Map Controls**: Zoom controls, compass, and map toolbar enabled
- **Responsive Design**: Works with existing navigation and chat button

## Files Modified/Created

### Modified Files:
- `app/build.gradle.kts` - Added Google Maps dependencies
- `app/src/main/AndroidManifest.xml` - Added permissions and API key placeholder
- `app/src/main/res/menu/bottom_nav_menu.xml` - Added map navigation item
- `app/src/main/res/values/strings.xml` - Added map title string
- `app/src/main/res/navigation/mobile_navigation.xml` - Added map fragment destination
- `app/src/main/java/com/example/myapplication/MainActivity.java` - Updated navigation configuration

### New Files:
- `app/src/main/java/com/example/myapplication/ui/map/MapFragment.java`
- `app/src/main/java/com/example/myapplication/ui/map/MapViewModel.java`
- `app/src/main/res/layout/fragment_map.xml`
- `app/src/main/res/drawable/ic_map_black_24dp.xml`

## Required Setup Steps

### 1. Google Maps API Key
You need to obtain a Google Maps API Key and replace the placeholder in AndroidManifest.xml:

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing project
3. Enable "Maps SDK for Android" API
4. Create credentials (API Key)
5. Restrict the API key to Android apps (recommended)
6. Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` in `AndroidManifest.xml` with your actual API key

### 2. Build and Test
After adding the API key:
1. Sync the project in Android Studio
2. Build and run the app
3. Navigate to the Map tab in bottom navigation
4. Verify the store location is displayed correctly

## Store Information
- **Location**: 103 Hoa Lan, Phường 7, Phú Nhuận, Hồ Chí Minh, Vietnam
- **Coordinates**: Latitude 10.7969, Longitude 106.6761
- **Store Hours**: 9:00 AM - 9:00 PM (displayed in info card)

## Navigation Structure
Your app now has four navigation tabs:
1. **Home** - Product listing
2. **Dashboard** - Dashboard content (unchanged)
3. **Profile** - User profile
4. **Map** - Store location (new)

## Technical Details
- **Map Type**: Normal Google Maps view
- **Zoom Level**: 16 (street level detail)
- **Marker Color**: Red (default)
- **Map Controls**: Zoom, compass, toolbar enabled
- **Lifecycle Management**: Proper MapView lifecycle handling
- **Authentication**: Integrated with existing AuthManager

## Troubleshooting

### Common Issues:
1. **Map not loading**: Check if API key is correctly set
2. **Blank map**: Verify Google Maps SDK is enabled in Google Cloud Console
3. **Location not showing**: Check if coordinates are correct
4. **Build errors**: Ensure all dependencies are synced

### Dependencies Added:
```kotlin
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")
```

## Next Steps
1. Obtain Google Maps API key
2. Replace placeholder in AndroidManifest.xml
3. Build and test the application
4. Optionally customize store information or map styling

The Map screen is now fully integrated and ready to use once you provide the Google Maps API key!
