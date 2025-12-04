package com.rexosphere.leoconnect.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val posts: List<Post>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        screenModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getHomeFeed(20)
                .onSuccess { posts ->
                    _uiState.value = HomeUiState.Success(posts)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun likePost(postId: String) {
        screenModelScope.launch {
            // Optimistic update
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
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
                _uiState.value = HomeUiState.Success(updatedPosts)
            }

            // Send network request
            repository.likePost(postId)
                .onFailure {
                    // Revert optimistic update on failure
                    if (currentState is HomeUiState.Success) {
                        _uiState.value = currentState
                    }
                }
        }
    }
}
