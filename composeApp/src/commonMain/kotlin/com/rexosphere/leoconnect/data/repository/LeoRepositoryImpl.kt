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
    private val localDataSource: com.rexosphere.leoconnect.data.source.local.LocalDataSource,
    private val cryptoService: com.rexosphere.leoconnect.domain.service.CryptoService
) : LeoRepository {

    private val _authState = MutableStateFlow<UserProfile?>(null)
    private val _unreadMessagesCount = MutableStateFlow(0)
    override val unreadMessagesCount = _unreadMessagesCount.asStateFlow()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Load cached user profile on init
        repositoryScope.launch {
            val cachedProfile = localDataSource.getUserProfile()
            if (cachedProfile != null) {
                _authState.value = cachedProfile
                // Refresh unread messages count when user is signed in
                refreshUnreadMessagesCount()
            }
        }
    }

    override suspend fun googleSignIn(onStatus: (String) -> Unit): Result<UserProfile> {
        return try {
            onStatus("Signing in with Google...")
            
            // 1. Sign in with Google and get Firebase token
            val tokenResult = authService.signInWithGoogle()
            if (tokenResult.isFailure) {
                return Result.failure(tokenResult.exceptionOrNull() ?: Exception("Sign in failed"))
            }

            val firebaseToken = tokenResult.getOrThrow()

            onStatus("Authenticating with server...")
            
            // 2. Send Firebase token to backend and get user profile
            val profile = remoteDataSource.googleSignIn(firebaseToken)
            _authState.value = profile

            // 3. Cache profile and set logged in state
            localDataSource.saveUserProfile(profile)
            localDataSource.setLoggedIn(true)

            // 4. Generate and upload encryption keys
            try {
                println("E2E Encryption: Checking key status...")
                val hasLocalKeys = cryptoService.hasKeyPair()
                val hasServerKey = profile.publicKey != null
                
                println("E2E Encryption: Local keys exist: $hasLocalKeys, Server key exists: $hasServerKey")
                
                // Generate keys if we don't have them locally
                if (!hasLocalKeys) {
                    onStatus("Setting up encryption...")
                    println("E2E Encryption: Generating new key pair...")
                    val keyGenResult = cryptoService.generateKeyPair()
                    if (keyGenResult.isFailure) {
                        println("E2E Encryption: Failed to generate keys: ${keyGenResult.exceptionOrNull()?.message}")
                    } else {
                        println("E2E Encryption: Key pair generated successfully")
                    }
                }
                
                // Upload public key if server doesn't have one
                if (!hasServerKey) {
                    onStatus("Uploading encryption key...")
                    println("E2E Encryption: Server doesn't have public key, uploading...")
                    val localPublicKey = cryptoService.getPublicKey()
                    if (localPublicKey != null) {
                        try {
                            val updatedProfile = remoteDataSource.updatePublicKey(localPublicKey, force = false)
                            _authState.value = updatedProfile
                            localDataSource.saveUserProfile(updatedProfile)
                            println("E2E Encryption: Public key uploaded successfully")
                        } catch (uploadError: Exception) {
                            println("E2E Encryption: Failed to upload public key: ${uploadError.message}")
                            uploadError.printStackTrace()
                        }
                    } else {
                        println("E2E Encryption: No local public key available to upload")
                    }
                } else {
                    println("E2E Encryption: Server already has public key, skipping upload")
                }
            } catch (e: Exception) {
                // Log error but don't fail login
                println("E2E Encryption: Error setting up encryption: ${e.message}")
                e.printStackTrace()
            }

            onStatus("Finalizing...")
            
            // 5. Refresh unread messages count
            refreshUnreadMessagesCount()

            Result.success(_authState.value ?: profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun signOut() {
        authService.signOut()
        _authState.value = null
        _unreadMessagesCount.value = 0
        // Clear all cached data
        localDataSource.clearAll()
        // Clear encryption keys
        cryptoService.clearKeys()
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

    override suspend fun createPost(content: String, imagesList: List<String>, clubId: String?, clubName: String?): Result<Post> {
        return try {
            val post = remoteDataSource.createPost(content, imagesList, clubId, clubName)
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

    override suspend fun updateUserProfile(displayName: String?, leoId: String?, assignedClubId: String?, bio: String?, photoBase64: String?): Result<UserProfile> {
        return try {
            val profile = remoteDataSource.updateUserProfile(displayName, leoId, assignedClubId, bio, photoBase64)
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

    override suspend fun updatePublicKey(publicKey: String, force: Boolean): Result<UserProfile> {
        return try {
            val profile = remoteDataSource.updatePublicKey(publicKey, force)
            _authState.value = profile
            // Cache the updated profile
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

    override suspend fun refreshUnreadMessagesCount() {
        try {
            val conversations = remoteDataSource.getConversations()
            val totalUnread = conversations.sumOf { it.unreadCount }
            _unreadMessagesCount.value = totalUnread
        } catch (e: Exception) {
            // Silently fail - don't update count if fetch fails
            e.printStackTrace()
        }
    }

    override suspend fun getConversations(): Result<List<com.rexosphere.leoconnect.domain.model.Conversation>> {
        return try {
            val conversations = remoteDataSource.getConversations()
            // Update unread count whenever conversations are fetched
            val totalUnread = conversations.sumOf { it.unreadCount }
            _unreadMessagesCount.value = totalUnread
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
