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

class PostDetailScreenModel : StateScreenModel<PostDetailUiState>(PostDetailUiState.Loading) {
    val uiState = mutableState

    fun loadComments(postId: String) {
        screenModelScope.launch {
            // TODO: Load comments from repository
            // For now, use mock data
            val mockComments = listOf(
                Comment(
                    commentId = "1",
                    postId = postId,
                    userId = "user1",
                    authorName = "John Doe",
                    authorPhotoUrl = null,
                    content = "Great post! Thanks for sharing.",
                    createdAt = "2024-01-15T10:30:00Z",
                    likesCount = 5,
                    isLikedByUser = false
                ),
                Comment(
                    commentId = "2",
                    postId = postId,
                    userId = "user2",
                    authorName = "Jane Smith",
                    authorPhotoUrl = null,
                    content = "This is amazing! Keep up the good work.",
                    createdAt = "2024-01-15T11:00:00Z",
                    likesCount = 3,
                    isLikedByUser = true
                )
            )
            mutableState.value = PostDetailUiState.Success(mockComments)
        }
    }

    fun addComment(postId: String, content: String) {
        screenModelScope.launch {
            // TODO: Add comment via repository
            println("Adding comment to post $postId: $content")
            // Reload comments
            loadComments(postId)
        }
    }

    fun toggleLike(postId: String) {
        screenModelScope.launch {
            // TODO: Toggle like via repository
            println("Toggling like for post $postId")
        }
    }

    fun toggleCommentLike(commentId: String) {
        screenModelScope.launch {
            // TODO: Toggle comment like via repository
            println("Toggling like for comment $commentId")
            // Update comment in list
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
        }
    }
}
