package com.rexosphere.leoconnect.data.repository

import com.rexosphere.leoconnect.data.source.remote.KtorRemoteDataSource
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import com.rexosphere.leoconnect.domain.service.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeoRepositoryImpl(
    private val remoteDataSource: KtorRemoteDataSource,
    private val authService: AuthService,
    private val localDataSource: com.rexosphere.leoconnect.data.source.local.LocalDataSource
) : LeoRepository {

    private val _authState = MutableStateFlow<UserProfile?>(null)
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Load cached user profile on init
        repositoryScope.launch {
            val cachedProfile = localDataSource.getUserProfile()
            if (cachedProfile != null) {
                _authState.value = cachedProfile
            }
        }
    }

    override suspend fun googleSignIn(): Result<UserProfile> {
        return try {
            // 1. Sign in with Google and get Firebase token
            val tokenResult = authService.signInWithGoogle()
            if (tokenResult.isFailure) {
                return Result.failure(tokenResult.exceptionOrNull() ?: Exception("Sign in failed"))
            }

            val firebaseToken = tokenResult.getOrThrow()

            // 2. Send Firebase token to backend and get user profile
            val profile = remoteDataSource.googleSignIn(firebaseToken)
            _authState.value = profile

            // 3. Cache profile and set logged in state
            localDataSource.saveUserProfile(profile)
            localDataSource.setLoggedIn(true)

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        authService.signOut()
        _authState.value = null
        // Clear all cached data
        localDataSource.clearAll()
    }

    override fun getAuthState(): Flow<UserProfile?> {
        return _authState.asStateFlow()
    }

    override fun isSignedIn(): Boolean {
        // Check both auth service and local cache
        return authService.isSignedIn() || _authState.value != null
    }

    override suspend fun getHomeFeed(limit: Int): Result<List<Post>> {
        return try {
            val posts = remoteDataSource.getHomeFeed(limit)
            // TODO: Cache posts locally
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likePost(postId: String): Result<Unit> {
        return try {
            remoteDataSource.likePost(postId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPost(content: String, imageBytes: String?, clubId: String?, clubName: String?): Result<Post> {
        return try {
            val post = remoteDataSource.createPost(content, imageBytes, clubId, clubName)
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDistricts(): Result<List<String>> {
        return try {
            val districts = remoteDataSource.getDistricts()
            Result.success(districts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClubsByDistrict(district: String): Result<List<Club>> {
        return try {
            val clubs = remoteDataSource.getClubsByDistrict(district)
            Result.success(clubs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val uid = authService.getCurrentUserId()
            val profile = remoteDataSource.getUserProfile(uid)
            _authState.value = profile
            // Cache the profile
            localDataSource.saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            // If fetching profile fails (e.g. 404 User not found), try to sync/create user
            try {
                val token = authService.getCurrentToken()
                if (token != null) {
                    val profile = remoteDataSource.googleSignIn(token)
                    _authState.value = profile
                    // Cache the profile
                    localDataSource.saveUserProfile(profile)
                    return Result.success(profile)
                }
            } catch (syncError: Exception) {
                // Ignore sync error and return original error
                e.printStackTrace()
            }
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(leoId: String?, assignedClubId: String?, bio: String?): Result<UserProfile> {
        return try {
            val profile = remoteDataSource.updateUserProfile(leoId, assignedClubId, bio)
            _authState.value = profile
            // Cache the updated profile
            localDataSource.saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeOnboarding(leoId: String?, assignedClubId: String?): Result<UserProfile> {
        return try {
            val profile = remoteDataSource.completeOnboarding(leoId, assignedClubId)
            _authState.value = profile
            // Cache the profile after onboarding
            localDataSource.saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun followUser(userId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.followUser(userId)
            Result.success(response.isFollowing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unfollowUser(userId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.unfollowUser(userId)
            Result.success(response.isFollowing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun followClub(clubId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.followClub(clubId)
            Result.success(response.isFollowing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unfollowClub(clubId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.unfollowClub(clubId)
            Result.success(response.isFollowing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserFollowers(userId: String, limit: Int, offset: Int): Result<com.rexosphere.leoconnect.data.source.remote.FollowersResponse> {
        return try {
            val response = remoteDataSource.getUserFollowers(userId, limit, offset)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserFollowing(userId: String, limit: Int, offset: Int): Result<com.rexosphere.leoconnect.data.source.remote.FollowersResponse> {
        return try {
            val response = remoteDataSource.getUserFollowing(userId, limit, offset)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserFollowingClubs(userId: String, limit: Int, offset: Int): Result<List<Club>> {
        return try {
            val response = remoteDataSource.getUserFollowingClubs(userId, limit, offset)
            Result.success(response.clubs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getComments(postId: String): Result<List<com.rexosphere.leoconnect.domain.model.Comment>> {
        return try {
            val response = remoteDataSource.getComments(postId)
            Result.success(response.comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addComment(postId: String, content: String): Result<com.rexosphere.leoconnect.domain.model.Comment> {
        return try {
            val response = remoteDataSource.addComment(postId, content)
            Result.success(response.comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeComment(commentId: String): Result<com.rexosphere.leoconnect.domain.model.CommentLikeResponse> {
        return try {
            val response = remoteDataSource.likeComment(commentId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClubPosts(clubId: String): Result<List<Post>> {
        return try {
            val posts = remoteDataSource.getClubPosts(clubId)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfileById(userId: String): Result<UserProfile> {
        return try {
            val profile = remoteDataSource.getUserProfileById(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return try {
            val posts = remoteDataSource.getUserPosts(userId)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun search(query: String): Result<com.rexosphere.leoconnect.domain.repository.SearchResult> {
        return try {
            val response = remoteDataSource.search(query)
            Result.success(
                com.rexosphere.leoconnect.domain.repository.SearchResult(
                    posts = response.posts,
                    clubs = response.clubs,
                    districts = response.districts
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConversations(): Result<List<com.rexosphere.leoconnect.domain.model.Conversation>> {
        return try {
            val conversations = remoteDataSource.getConversations()
            Result.success(conversations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessages(userId: String): Result<List<com.rexosphere.leoconnect.domain.model.Message>> {
        return try {
            val messages = remoteDataSource.getMessages(userId)
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(receiverId: String, content: String): Result<com.rexosphere.leoconnect.domain.model.Message> {
        return try {
            val message = remoteDataSource.sendMessage(receiverId, content)
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.deleteMessage(messageId)
            Result.success(response.success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteConversation(userId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.deleteConversation(userId)
            Result.success(response.success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchUsers(query: String): Result<List<com.rexosphere.leoconnect.data.source.remote.UserSearchResult>> {
        return try {
            val users = remoteDataSource.searchUsers(query)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== NEW METHODS ====================

    override suspend fun getExploreFeed(limit: Int): Result<List<Post>> {
        return try {
            val posts = remoteDataSource.getExploreFeed(limit)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.deletePost(postId)
            Result.success(response.success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== EVENT METHODS ====================

    override suspend fun getEvents(limit: Int, clubId: String?): Result<List<com.rexosphere.leoconnect.domain.model.Event>> {
        return try {
            val events = remoteDataSource.getEvents(limit, clubId)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEventById(eventId: String): Result<com.rexosphere.leoconnect.domain.model.Event> {
        return try {
            val event = remoteDataSource.getEventById(eventId)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createEvent(
        name: String,
        description: String,
        eventDate: String,
        clubId: String?,
        imageBytes: String?
    ): Result<com.rexosphere.leoconnect.domain.model.Event> {
        return try {
            val event = remoteDataSource.createEvent(name, description, eventDate, clubId, imageBytes)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(
        eventId: String,
        name: String?,
        description: String?,
        eventDate: String?,
        imageBytes: String?
    ): Result<com.rexosphere.leoconnect.domain.model.Event> {
        return try {
            val event = remoteDataSource.updateEvent(eventId, name, description, eventDate, imageBytes)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Boolean> {
        return try {
            val response = remoteDataSource.deleteEvent(eventId)
            Result.success(response.success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rsvpEvent(eventId: String): Result<com.rexosphere.leoconnect.domain.model.RSVPResponse> {
        return try {
            val response = remoteDataSource.rsvpEvent(eventId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
