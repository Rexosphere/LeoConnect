package com.rexosphere.leoconnect.domain.repository

import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface LeoRepository {
    /**
     * Sign in with Google and retrieve user profile
     */
    suspend fun googleSignIn(): Result<UserProfile>

    /**
     * Sign out the current user
     */
    suspend fun signOut()

    /**
     * Get the authentication state as a flow
     */
    fun getAuthState(): Flow<UserProfile?>

    /**
     * Check if user is signed in
     */
    fun isSignedIn(): Boolean

    /**
     * Get the home feed
     */
    suspend fun getHomeFeed(limit: Int): Result<List<Post>>

    /**
     * Like a post
     */
    suspend fun likePost(postId: String): Result<Unit>

    /**
     * Create a new post
     */
    suspend fun createPost(content: String, imageUrl: String?): Result<Post>

    /**
     * Get all districts
     */
    suspend fun getDistricts(): Result<List<String>>

    /**
     * Get clubs by district
     */
    suspend fun getClubsByDistrict(district: String): Result<List<Club>>

    /**
     * Get current user profile
     */
    suspend fun getUserProfile(): Result<UserProfile>

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(leoId: String?, assignedClubId: String?): Result<UserProfile>
}
