package com.rexosphere.leoconnect.presentation.clubdetail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.launch

sealed class ClubDetailUiState {
    data object Loading : ClubDetailUiState()
    data class Success(val posts: List<Post>) : ClubDetailUiState()
    data class Error(val message: String) : ClubDetailUiState()
}

class ClubDetailScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<ClubDetailUiState>(ClubDetailUiState.Loading) {
    val uiState = mutableState

    fun loadClubPosts(clubId: String) {
        screenModelScope.launch {
            mutableState.value = ClubDetailUiState.Loading
            repository.getClubPosts(clubId)
                .onSuccess { posts ->
                    mutableState.value = ClubDetailUiState.Success(posts)
                }
                .onFailure { e ->
                    mutableState.value = ClubDetailUiState.Error(e.message ?: "Failed to load posts")
                }
        }
    }

    fun toggleLike(postId: String) {
        screenModelScope.launch {
            repository.likePost(postId)
                .onSuccess {
                    // Optimistic update
                    val currentState = mutableState.value
                    if (currentState is ClubDetailUiState.Success) {
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
                        mutableState.value = ClubDetailUiState.Success(updatedPosts)
                    }
                }
                .onFailure {
                    // Handle error
                }
        }
    }
}
