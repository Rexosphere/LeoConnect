package com.rexosphere.leoconnect.domain.service

import com.rexosphere.leoconnect.domain.model.UserProfile

/**
 * Authentication service interface for handling user authentication
 */
interface AuthService {
    /**
     * Sign in with Google and return the Firebase ID token
     * @return Firebase ID token string
     */
    suspend fun signInWithGoogle(): Result<String>

    /**
     * Create a new user with email and password
     * @return Result with Firebase ID token on success (user created but not verified)
     */
    suspend fun createUserWithEmailPassword(email: String, password: String): Result<String>

    /**
     * Sign in with email and password
     * @return Firebase ID token if email is verified, error if not verified
     */
    suspend fun signInWithEmailPassword(email: String, password: String): Result<String>

    /**
     * Send email verification to current user
     */
    suspend fun sendEmailVerification(): Result<Unit>

    /**
     * Check if current user's email is verified
     */
    suspend fun isEmailVerified(): Boolean

    /**
     * Reload current user to get fresh verification status
     */
    suspend fun reloadUser(): Result<Unit>

    /**
     * Get the current Firebase ID token if user is signed in
     * @param forceRefresh whether to force refresh the token
     * @return Firebase ID token string or null if not signed in
     */
    suspend fun getCurrentToken(forceRefresh: Boolean = false): String?

    /**
     * Sign out the current user
     */
    suspend fun signOut()

    /**
     * Get the current user's UID
     * @return User ID or null if not signed in
     */
    fun getCurrentUserId(): String?

    /**
     * Check if a user is currently signed in
     */
    fun isSignedIn(): Boolean
}
