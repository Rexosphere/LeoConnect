package com.rexosphere.leoconnect.presentation.userprofile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.data.source.remote.FollowerUser
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserProfileUiState {
    data object Loading : UserProfileUiState()
    data class Success(val profile: UserProfile, val posts: List<Post>) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

class UserProfileScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<UserProfileUiState>(UserProfileUiState.Loading) {
    val uiState = mutableState
    private var currentUserId: String? = null

    // Followers/Following state
    private val _followers = MutableStateFlow<List<FollowerUser>>(emptyList())
    val followers = _followers.asStateFlow()

    private val _following = MutableStateFlow<List<FollowerUser>>(emptyList())
    val following = _following.asStateFlow()

    private val _isLoadingFollowers = MutableStateFlow(false)
    val isLoadingFollowers = _isLoadingFollowers.asStateFlow()

    private val _isLoadingFollowing = MutableStateFlow(false)
    val isLoadingFollowing = _isLoadingFollowing.asStateFlow()

    fun loadUser(userId: String) {
        currentUserId = userId
        screenModelScope.launch {
            mutableState.value = UserProfileUiState.Loading

            val profileResult = repository.getUserProfileById(userId)
            val postsResult = repository.getUserPosts(userId)

            if (profileResult.isSuccess && postsResult.isSuccess) {
                mutableState.value = UserProfileUiState.Success(
                    profile = profileResult.getOrThrow(),
                    posts = postsResult.getOrThrow()
                )
            } else {
                val error = profileResult.exceptionOrNull()?.message
                    ?: postsResult.exceptionOrNull()?.message
                    ?: "Failed to load profile"
                mutableState.value = UserProfileUiState.Error(error)
            }
        }
    }

    fun toggleLike(postId: String) {
        screenModelScope.launch {
            repository.likePost(postId)
                .onSuccess {
                    val currentState = mutableState.value
                    if (currentState is UserProfileUiState.Success) {
                        val updatedPosts = currentState.posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(
                                    isLikedByUser = !post.isLikedByUser,
                                    likesCount = if (post.isLikedByUser) post.likesCount - 1 else post.likesCount + 1
                                )
                            } else {
                                post
                            }
                        }
                        mutableState.value = currentState.copy(posts = updatedPosts)
                    }
                }
        }
    }

    fun toggleFollow() {
        val userId = currentUserId ?: return
        val currentState = mutableState.value
        if (currentState !is UserProfileUiState.Success) return

        val isCurrentlyFollowing = currentState.profile.isFollowing == true

        screenModelScope.launch {
            val result = if (isCurrentlyFollowing) {
                repository.unfollowUser(userId)
            } else {
                repository.followUser(userId)
            }

            result.onSuccess { isFollowing ->
                // Reload the profile to get updated follower counts
                val profileResult = repository.getUserProfileById(userId)
                profileResult.onSuccess { updatedProfile ->
                    mutableState.value = currentState.copy(profile = updatedProfile)
                }
            }
        }
    }

    fun loadFollowers() {
        val userId = currentUserId ?: return
        screenModelScope.launch {
            _isLoadingFollowers.value = true
            repository.getUserFollowers(userId)
                .onSuccess { response ->
                    _followers.value = response.followers ?: emptyList()
                    _isLoadingFollowers.value = false
                }
                .onFailure {
                    _isLoadingFollowers.value = false
                }
        }
    }

    fun loadFollowing() {
        val userId = currentUserId ?: return
        screenModelScope.launch {
            _isLoadingFollowing.value = true
            repository.getUserFollowing(userId)
                .onSuccess { response ->
                    _following.value = response.followers ?: emptyList()
                    _isLoadingFollowing.value = false
                }
                .onFailure {
                    _isLoadingFollowing.value = false
                }
        }
    }

    fun toggleFollowInDialog(targetUserId: String, isCurrentlyFollowing: Boolean) {
        screenModelScope.launch {
            val result = if (isCurrentlyFollowing) {
                repository.unfollowUser(targetUserId)
            } else {
                repository.followUser(targetUserId)
            }

            result.onSuccess { isFollowing ->
                // Update the user in both followers and following lists
                _followers.value = _followers.value.map { user ->
                    if (user.uid == targetUserId) {
                        user.copy(isFollowing = isFollowing)
                    } else {
                        user
                    }
                }
                _following.value = _following.value.map { user ->
                    if (user.uid == targetUserId) {
                        user.copy(isFollowing = isFollowing)
                    } else {
                        user
                    }
                }
            }
        }
    }
}
