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
    suspend fun createPost(content: String, imageBytes: String?, clubId: String?, clubName: String?): Result<Post>

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
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(leoId: String?, assignedClubId: String?): Result<UserProfile>

    /**
     * Get comments for a post
     */
    suspend fun getComments(postId: String): Result<List<com.rexosphere.leoconnect.domain.model.Comment>>

    /**
     * Add a comment to a post
     */
    suspend fun addComment(postId: String, content: String): Result<com.rexosphere.leoconnect.domain.model.Comment>

    /**
     * Get posts for a specific club
     */
    suspend fun getClubPosts(clubId: String): Result<List<Post>>

    /**
     * Get user profile by ID
     */
    suspend fun getUserProfileById(userId: String): Result<UserProfile>

    /**
     * Get posts for a specific user
     */
    suspend fun getUserPosts(userId: String): Result<List<Post>>

    /**
     * Search for posts, clubs, and districts
     */
    suspend fun search(query: String): Result<SearchResult>

    /**
     * Get all conversations for the current user
     */
    suspend fun getConversations(): Result<List<com.rexosphere.leoconnect.domain.model.Conversation>>

    /**
     * Get messages with a specific user
     */
    suspend fun getMessages(userId: String): Result<List<com.rexosphere.leoconnect.domain.model.Message>>

    /**
     * Send a message to a user
     */
    suspend fun sendMessage(receiverId: String, content: String): Result<com.rexosphere.leoconnect.domain.model.Message>

    /**
     * Delete a message
     */
    suspend fun deleteMessage(messageId: String): Result<Boolean>

    /**
     * Delete a conversation with a user
     */
    suspend fun deleteConversation(userId: String): Result<Boolean>

    /**
     * Search for users to message
     */
    suspend fun searchUsers(query: String): Result<List<com.rexosphere.leoconnect.data.source.remote.UserSearchResult>>
}

data class SearchResult(
    val posts: List<Post>,
    val clubs: List<Club>,
    val districts: List<String>
)
