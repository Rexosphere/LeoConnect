package com.rexosphere.leoconnect.domain.repository

import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LeoRepository {
    /**
     * Get the unread messages count as a flow
     */
    val unreadMessagesCount: StateFlow<Int>

    /**
     * Refresh the unread messages count
     */
    suspend fun refreshUnreadMessagesCount()

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
     * Get the home feed (posts from followed users and clubs)
     */
    suspend fun getHomeFeed(limit: Int): Result<List<Post>>

    /**
     * Get the explore feed (all posts from anyone)
     */
    suspend fun getExploreFeed(limit: Int): Result<List<Post>>

    /**
     * Like a post
     */
    suspend fun likePost(postId: String): Result<Unit>

    /**
     * Create a new post
     */
    suspend fun createPost(content: String, imagesList: List<String>, clubId: String?, clubName: String?): Result<Post>

    /**
     * Delete a post (only author can delete)
     */
    suspend fun deletePost(postId: String): Result<Boolean>

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
     * Update user profile information
     */
    suspend fun updateUserProfile(displayName: String?, leoId: String?, assignedClubId: String?, bio: String?, photoBase64: String?): Result<UserProfile>

    /**
     * Complete onboarding for first-time user
     */
    suspend fun completeOnboarding(leoId: String?, assignedClubId: String?): Result<UserProfile>

    /**
     * Follow a user
     */
    suspend fun followUser(userId: String): Result<Boolean>

    /**
     * Unfollow a user
     */
    suspend fun unfollowUser(userId: String): Result<Boolean>

    /**
     * Follow a club
     */
    suspend fun followClub(clubId: String): Result<Boolean>

    /**
     * Unfollow a club
     */
    suspend fun unfollowClub(clubId: String): Result<Boolean>

    /**
     * Get followers of a user
     */
    suspend fun getUserFollowers(userId: String, limit: Int = 50, offset: Int = 0): Result<com.rexosphere.leoconnect.data.source.remote.FollowersResponse>

    /**
     * Get users that a user is following
     */
    suspend fun getUserFollowing(userId: String, limit: Int = 50, offset: Int = 0): Result<com.rexosphere.leoconnect.data.source.remote.FollowersResponse>

    /**
     * Get clubs that a user is following
     */
    suspend fun getUserFollowingClubs(userId: String, limit: Int = 50, offset: Int = 0): Result<List<Club>>

    /**
     * Get comments for a post
     */
    suspend fun getComments(postId: String): Result<List<com.rexosphere.leoconnect.domain.model.Comment>>

    /**
     * Add a comment to a post
     */
    suspend fun addComment(postId: String, content: String): Result<com.rexosphere.leoconnect.domain.model.Comment>

    /**
     * Like/unlike a comment
     */
    suspend fun likeComment(commentId: String): Result<com.rexosphere.leoconnect.domain.model.CommentLikeResponse>

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

    // ==================== EVENT METHODS ====================

    /**
     * Get all events, optionally filtered by club
     */
    suspend fun getEvents(limit: Int = 20, clubId: String? = null): Result<List<com.rexosphere.leoconnect.domain.model.Event>>

    /**
     * Get a single event by ID
     */
    suspend fun getEventById(eventId: String): Result<com.rexosphere.leoconnect.domain.model.Event>

    /**
     * Create a new event
     */
    suspend fun createEvent(
        name: String,
        description: String,
        eventDate: String,
        clubId: String? = null,
        imageBytes: String? = null
    ): Result<com.rexosphere.leoconnect.domain.model.Event>

    /**
     * Update an existing event
     */
    suspend fun updateEvent(
        eventId: String,
        name: String? = null,
        description: String? = null,
        eventDate: String? = null,
        imageBytes: String? = null
    ): Result<com.rexosphere.leoconnect.domain.model.Event>

    /**
     * Delete an event
     */
    suspend fun deleteEvent(eventId: String): Result<Boolean>

    /**
     * RSVP to an event (toggle)
     */
    suspend fun rsvpEvent(eventId: String): Result<com.rexosphere.leoconnect.domain.model.RSVPResponse>
}

data class SearchResult(
    val posts: List<Post>,
    val clubs: List<Club>,
    val districts: List<String>
)
