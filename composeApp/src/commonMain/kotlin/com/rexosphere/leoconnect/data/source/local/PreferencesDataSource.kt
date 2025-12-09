package com.rexosphere.leoconnect.data.source.local

import com.rexosphere.leoconnect.domain.model.UserProfile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Preferences-based local data source implementation
 */
class PreferencesDataSource(
    private val preferences: LeoPreferences
) : LocalDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        val profileJson = json.encodeToString(profile)
        preferences.putString(KEY_USER_PROFILE, profileJson)
    }

    override suspend fun getUserProfile(): UserProfile? {
        val profileJson = preferences.getString(KEY_USER_PROFILE) ?: return null
        return try {
            json.decodeFromString<UserProfile>(profileJson)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearUserProfile() {
        preferences.remove(KEY_USER_PROFILE)
    }

    override suspend fun saveAuthToken(token: String) {
        preferences.putString(KEY_AUTH_TOKEN, token)
    }

    override suspend fun getAuthToken(): String? {
        return preferences.getString(KEY_AUTH_TOKEN)
    }

    override suspend fun clearAuthToken() {
        preferences.remove(KEY_AUTH_TOKEN)
    }

    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
    }

    override suspend fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    override suspend fun clearAll() {
        preferences.clear()
    }

    companion object {
        private const val KEY_USER_PROFILE = "user_profile"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}

/**
 * Platform-agnostic preferences interface
 */
interface LeoPreferences {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun remove(key: String)
    fun clear()
}

/**
 * In-memory implementation for testing/fallback
 */
class InMemoryPreferences : LeoPreferences {
    private val map = mutableMapOf<String, Any>()

    override fun putString(key: String, value: String) {
        map[key] = value
    }

    override fun getString(key: String): String? {
        return map[key] as? String
    }

    override fun putBoolean(key: String, value: Boolean) {
        map[key] = value
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return map[key] as? Boolean ?: defaultValue
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun clear() {
        map.clear()
    }
}
