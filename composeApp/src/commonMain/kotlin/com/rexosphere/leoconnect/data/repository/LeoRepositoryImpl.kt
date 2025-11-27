package com.rexosphere.leoconnect.data.repository

import com.rexosphere.leoconnect.data.source.remote.KtorRemoteDataSource
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import com.rexosphere.leoconnect.domain.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeoRepositoryImpl(
    private val remoteDataSource: KtorRemoteDataSource,
    private val authService: AuthService
) : LeoRepository {

    private val _authState = MutableStateFlow<UserProfile?>(null)

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
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        authService.signOut()
        _authState.value = null
    }

    override fun getAuthState(): Flow<UserProfile?> {
        return _authState.asStateFlow()
    }

    override fun isSignedIn(): Boolean {
        return authService.isSignedIn()
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

    override suspend fun createPost(content: String, imageUrl: String?): Result<Post> {
        return try {
            val post = remoteDataSource.createPost(content, imageUrl)
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
}
