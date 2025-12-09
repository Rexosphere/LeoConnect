package com.rexosphere.leoconnect.presentation.clubdetail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.launch

sealed class ClubDetailUiState {
    data object Loading : ClubDetailUiState()
    data class Success(val posts: List<Post>, val club: Club) : ClubDetailUiState()
    data class Error(val message: String) : ClubDetailUiState()
}

class ClubDetailScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<ClubDetailUiState>(ClubDetailUiState.Loading) {
    val uiState = mutableState
    private var currentClub: Club? = null

    fun loadClubPosts(clubId: String, club: Club) {
        currentClub = club
        screenModelScope.launch {
            mutableState.value = ClubDetailUiState.Loading
            repository.getClubPosts(clubId)
                .onSuccess { posts ->
                    mutableState.value = ClubDetailUiState.Success(posts, club)
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
                        mutableState.value = currentState.copy(posts = updatedPosts)
                    }
                }
                .onFailure {
                    // Handle error
                }
        }
    }

    fun toggleFollow() {
        val club = currentClub ?: return
        val currentState = mutableState.value
        if (currentState !is ClubDetailUiState.Success) return

        val isCurrentlyFollowing = currentState.club.isFollowing

        screenModelScope.launch {
            val result = if (isCurrentlyFollowing) {
                repository.unfollowClub(club.clubId)
            } else {
                repository.followClub(club.clubId)
            }

            result.onSuccess { isFollowing ->
                // Update the club in the state
                val updatedClub = currentState.club.copy(
                    isFollowing = isFollowing,
                    followersCount = if (isFollowing) currentState.club.followersCount + 1 else currentState.club.followersCount - 1
                )
                currentClub = updatedClub
                mutableState.value = currentState.copy(club = updatedClub)
            }
        }
    }
}
