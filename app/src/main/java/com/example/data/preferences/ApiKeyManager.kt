package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

object ApiKeyManager {
    private const val PREFS_NAME = "pulsecraft_studio_prefs"
    private const val KEY_GEMINI_API_KEY = "gemini_api_key"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Retrieves the stored Gemini API key.
     * Checks SharedPreferences first; if blank, falls back to BuildConfig.GEMINI_API_KEY.
     */
    fun getApiKey(context: Context): String {
        val storedKey = getPrefs(context).getString(KEY_GEMINI_API_KEY, "")?.trim() ?: ""
        if (storedKey.isNotBlank()) {
            return storedKey
        }
        val buildKey = BuildConfig.GEMINI_API_KEY.trim()
        return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            buildKey
        } else {
            ""
        }
    }

    /**
     * Saves the user-supplied Gemini API key to persistent SharedPreferences.
     */
    fun saveApiKey(context: Context, apiKey: String) {
        getPrefs(context).edit()
            .putString(KEY_GEMINI_API_KEY, apiKey.trim())
            .apply()
    }

    /**
     * Clears any custom stored API key.
     */
    fun clearApiKey(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_GEMINI_API_KEY)
            .apply()
    }

    /**
     * Returns true if there is an active non-empty API key available.
     */
    fun hasValidApiKey(context: Context): Boolean {
        return getApiKey(context).isNotBlank()
    }

    /**
     * Masked representation for UI display (e.g. "AIzaSy...4x9Q").
     */
    fun getMaskedKey(context: Context): String {
        val key = getApiKey(context)
        return if (key.length > 8) {
            "${key.take(6)}...${key.takeLast(4)}"
        } else if (key.isNotEmpty()) {
            "••••••••"
        } else {
            "Not Configured"
        }
    }
}
