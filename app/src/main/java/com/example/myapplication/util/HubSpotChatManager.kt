package com.example.myapplication.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.HubspotWebActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Singleton class to manage HubSpot Chat SDK integration
 * Handles initialization, user context, and chat operations
 */
class HubSpotChatManager private constructor() {
    
    companion object {
        private const val TAG = "HubSpotChatManager"
        
        @Volatile
        private var instance: HubSpotChatManager? = null
        
        @JvmStatic
        fun getInstance(): HubSpotChatManager {
            return instance ?: synchronized(this) {
                instance ?: HubSpotChatManager().also { instance = it }
            }
        }
    }
    
    private var isInitialized = false
    private var applicationContext: Context? = null
    
    /**
     * Initialize HubSpot Chat SDK
     * Should be called from Application class or MainActivity
     */
    fun initialize(context: Context) {
        if (isInitialized) {
            Log.d(TAG, "HubSpot Chat SDK already initialized")
            return
        }
        
        try {
            applicationContext = context.applicationContext
            
            // Initialize HubSpot SDK using HubspotManager
            val hubspotManager = HubspotManager.getInstance(applicationContext!!)
            hubspotManager.configure()
            
            isInitialized = true
            Log.d(TAG, "HubSpot Chat SDK initialized successfully")
            
        } catch (e: Exception) {
            when {
                e.javaClass.simpleName.contains("HubspotConfigError") -> {
                    Log.e(TAG, "HubSpot Configuration Error: ${e.message}", e)
                    Log.e(TAG, "Check your hubspot-info.json file in app/src/main/assets/")
                }
                else -> {
                    Log.e(TAG, "Failed to initialize HubSpot Chat SDK: ${e.message}", e)
                }
            }
        }
    }
    
    /**
     * Open HubSpot chat interface using built-in HubspotWebActivity
     */
    fun openChat(context: Context) {
        if (!isInitialized) {
            Log.w(TAG, "HubSpot Chat SDK not initialized. Attempting to initialize...")
            initialize(context)
        }
        
        try {
            // Use the SDK's built-in HubspotWebActivity
            val intent = Intent(context, HubspotWebActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            Log.d(TAG, "HubSpot chat opened using HubspotWebActivity")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open HubSpot chat: ${e.message}", e)
        }
    }
    
    /**
     * Set user information for personalized chat experience
     * Call this when user logs in
     */
    fun setUserInfo(email: String, token: String) {
        if (!isInitialized) {
            Log.w(TAG, "HubSpot Chat SDK not initialized")
            return
        }
        
        try {
            val hubspotManager = HubspotManager.getInstance(applicationContext!!)
            // HubspotManager uses setUserIdentity with email and token
            // For now, we'll use userId as token - you may need to adjust this based on your auth system
            hubspotManager.setUserIdentity(email, token)
            
            Log.d(TAG, "User info set for HubSpot chat: $email")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user info: ${e.message}", e)
        }
    }
    
    /**
     * Clear user information and logout
     * Call this when user logs out
     */
    fun clearUserInfo() {
        if (!isInitialized) {
            return
        }
        
        try {
            val hubspotManager = HubspotManager.getInstance(applicationContext!!)
            // Use the proper logout method to clear all user data
            // Note: This is an async method, so we'll handle it with coroutines
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    hubspotManager.logout()
                    Log.d(TAG, "User info cleared from HubSpot chat via logout()")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to logout from HubSpot: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear user info: ${e.message}", e)
        }
    }
    
    /**
     * Check if chat SDK is initialized
     */
    fun isInitialized(): Boolean {
        return isInitialized
    }
    
    /**
     * Set custom attributes for better customer context
     */
    fun setCustomAttributes(key: String, value: String) {
        if (!isInitialized) {
            return
        }
        
        try {
            val hubspotManager = HubspotManager.getInstance(applicationContext!!)
            val properties = mapOf(key to value)
            hubspotManager.setChatProperties(properties)
            Log.d(TAG, "Custom attribute set: $key = $value")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set custom attribute: ${e.message}", e)
        }
    }
}
