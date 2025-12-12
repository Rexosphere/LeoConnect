package com.rexosphere.leoconnect.presentation.myposts

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MyPostsUiState {
    data object Loading : MyPostsUiState
    data class Success(val posts: List<Post>) : MyPostsUiState
    data class Error(val message: String) : MyPostsUiState
}

class MyPostsScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<MyPostsUiState>(MyPostsUiState.Loading)
    val uiState: StateFlow<MyPostsUiState> = _uiState.asStateFlow()

    fun loadMyPosts(userId: String) {
        screenModelScope.launch {
            _uiState.value = MyPostsUiState.Loading
            repository.getUserPosts(userId)
                .onSuccess { posts ->
                    _uiState.value = MyPostsUiState.Success(posts)
                }
                .onFailure { error ->
                    _uiState.value = MyPostsUiState.Error(error.message ?: "Failed to load posts")
                }
        }
    }

    fun likePost(postId: String) {
        screenModelScope.launch {
            repository.likePost(postId)
                .onSuccess {
                    // Refresh posts to update like status
                    val currentState = _uiState.value
                    if (currentState is MyPostsUiState.Success) {
                        val userId = currentState.posts.firstOrNull()?.authorId
                        if (userId != null) {
                            loadMyPosts(userId)
                        }
                    }
                }
                .onFailure { error ->
                    // Handle error silently or show a snackbar
                    println("Failed to like post: ${error.message}")
                }
        }
    }

    fun deletePost(postId: String) {
        screenModelScope.launch {
            repository.deletePost(postId)
                .onSuccess {
                    // Remove the post from the current list
                    val currentState = _uiState.value
                    if (currentState is MyPostsUiState.Success) {
                        val updatedPosts = currentState.posts.filter { it.postId != postId }
                        _uiState.value = MyPostsUiState.Success(updatedPosts)
                    }
                }
                .onFailure { error ->
                    // Handle error - could show a snackbar
                    println("Failed to delete post: ${error.message}")
                }
        }
    }
}
