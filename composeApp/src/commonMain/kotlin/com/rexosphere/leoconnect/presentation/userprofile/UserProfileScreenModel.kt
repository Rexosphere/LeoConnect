package com.rexosphere.leoconnect.presentation.userprofile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.repository.LeoRepository
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

    fun loadUser(userId: String) {
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
        // TODO: Implement follow logic
    }
}
