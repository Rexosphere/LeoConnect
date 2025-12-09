package com.rexosphere.leoconnect.data.source.local

import com.rexosphere.leoconnect.domain.model.UserProfile
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Local data source for caching user data
 */
interface LocalDataSource {
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun getUserProfile(): UserProfile?
    suspend fun clearUserProfile()

    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun clearAuthToken()

    suspend fun setLoggedIn(isLoggedIn: Boolean)
    suspend fun isLoggedIn(): Boolean

    suspend fun clearAll()
}

@Serializable
data class CachedAuthData(
    val token: String?,
    val isLoggedIn: Boolean,
    val userProfileJson: String?
)

/**
 * In-memory implementation for fallback
 */
class InMemoryLocalDataSource : LocalDataSource {
    private var cachedProfile: UserProfile? = null
    private var cachedToken: String? = null
    private var loggedIn: Boolean = false

    override suspend fun saveUserProfile(profile: UserProfile) {
        cachedProfile = profile
    }

    override suspend fun getUserProfile(): UserProfile? = cachedProfile

    override suspend fun clearUserProfile() {
        cachedProfile = null
    }

    override suspend fun saveAuthToken(token: String) {
        cachedToken = token
    }

    override suspend fun getAuthToken(): String? = cachedToken

    override suspend fun clearAuthToken() {
        cachedToken = null
    }

    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        loggedIn = isLoggedIn
    }

    override suspend fun isLoggedIn(): Boolean = loggedIn

    override suspend fun clearAll() {
        cachedProfile = null
        cachedToken = null
        loggedIn = false
    }
}
