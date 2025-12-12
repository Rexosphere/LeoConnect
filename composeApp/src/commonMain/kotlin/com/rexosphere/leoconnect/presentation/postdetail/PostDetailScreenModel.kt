package com.rexosphere.leoconnect.presentation.postdetail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Comment
import kotlinx.coroutines.launch

sealed class PostDetailUiState {
    data object Loading : PostDetailUiState()
    data class Success(val comments: List<Comment>) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}

class PostDetailScreenModel(
    private val repository: com.rexosphere.leoconnect.domain.repository.LeoRepository
) : StateScreenModel<PostDetailUiState>(PostDetailUiState.Loading) {
    val uiState = mutableState

    fun loadComments(postId: String) {
        screenModelScope.launch {
            mutableState.value = PostDetailUiState.Loading
            repository.getComments(postId)
                .onSuccess { comments ->
                    mutableState.value = PostDetailUiState.Success(comments)
                }
                .onFailure { e ->
                    mutableState.value = PostDetailUiState.Error(e.message ?: "Failed to load comments")
                }
        }
    }

    fun addComment(postId: String, content: String) {
        screenModelScope.launch {
            // Optimistic update or show loading? For now, just call API and reload
            // Ideally we'd append to the list locally first
            repository.addComment(postId, content)
                .onSuccess { newComment ->
                    val currentState = mutableState.value
                    if (currentState is PostDetailUiState.Success) {
                        // Prepend new comment
                        mutableState.value = PostDetailUiState.Success(listOf(newComment) + currentState.comments)
                    } else {
                        loadComments(postId)
                    }
                }
                .onFailure { e ->
                    // TODO: Show error (maybe via a side effect channel)
                    println("Failed to add comment: ${e.message}")
                }
        }
    }

    fun toggleLike(postId: String) {
        screenModelScope.launch {
            repository.likePost(postId)
                .onSuccess {
                    // Success, UI should already be updated optimistically in the screen if we had local state there
                    // But here we might want to refresh the post or just assume success
                    // The screen currently handles post like state via the Post object passed in.
                    // To update it properly, we might need to return the new state or refresh the post.
                    // For now, we just fire and forget as the UI might need a refresh mechanism for the post itself.
                }
                .onFailure {
                    println("Failed to like post: ${it.message}")
                }
        }
    }

    fun toggleCommentLike(commentId: String) {
        screenModelScope.launch {
            // Optimistic local update first for responsive UI
            val currentState = mutableState.value
            if (currentState is PostDetailUiState.Success) {
                val updatedComments = currentState.comments.map { comment ->
                    if (comment.commentId == commentId) {
                        comment.copy(
                            isLikedByUser = !comment.isLikedByUser,
                            likesCount = if (comment.isLikedByUser) comment.likesCount - 1 else comment.likesCount + 1
                        )
                    } else {
                        comment
                    }
                }
                mutableState.value = PostDetailUiState.Success(updatedComments)
            }
            
            // Call API and update with real values
            repository.likeComment(commentId)
                .onSuccess { response ->
                    val state = mutableState.value
                    if (state is PostDetailUiState.Success) {
                        val syncedComments = state.comments.map { comment ->
                            if (comment.commentId == commentId) {
                                comment.copy(
                                    isLikedByUser = response.isLikedByUser,
                                    likesCount = response.likesCount
                                )
                            } else {
                                comment
                            }
                        }
                        mutableState.value = PostDetailUiState.Success(syncedComments)
                    }
                }
                .onFailure { e ->
                    println("Failed to like comment: ${e.message}")
                    // Revert optimistic update on failure
                    val state = mutableState.value
                    if (state is PostDetailUiState.Success) {
                        val revertedComments = state.comments.map { comment ->
                            if (comment.commentId == commentId) {
                                comment.copy(
                                    isLikedByUser = !comment.isLikedByUser,
                                    likesCount = if (comment.isLikedByUser) comment.likesCount - 1 else comment.likesCount + 1
                                )
                            } else {
                                comment
                            }
                        }
                        mutableState.value = PostDetailUiState.Success(revertedComments)
                    }
                }
        }
    }
}
