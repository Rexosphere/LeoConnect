package com.rexosphere.leoconnect.presentation.createpost

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CreatePostUiState {
    data object Idle : CreatePostUiState()
    data object Loading : CreatePostUiState()
    data class Success(val post: Post) : CreatePostUiState()
    data class Error(val message: String) : CreatePostUiState()
}

class CreatePostScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<CreatePostUiState>(CreatePostUiState.Idle) {
    val uiState = mutableState

    private val _userClubId = MutableStateFlow<String?>(null)
    val userClubId = _userClubId.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        screenModelScope.launch {
            repository.getUserProfile().onSuccess { profile ->
                _userClubId.value = profile.assignedClubId
            }
        }
    }

    fun createPost(content: String, imageBytes: String?) {
        if (content.isBlank()) {
            mutableState.value = CreatePostUiState.Error("Post content cannot be empty")
            return
        }

        screenModelScope.launch {
            mutableState.value = CreatePostUiState.Loading
            // Use the user's assigned club ID if available
            repository.createPost(content, imageBytes, _userClubId.value, null)
                .onSuccess { post ->
                    mutableState.value = CreatePostUiState.Success(post)
                }
                .onFailure { error ->
                    mutableState.value = CreatePostUiState.Error(error.message ?: "Failed to create post")
                }
        }
    }

    fun resetState() {
        mutableState.value = CreatePostUiState.Idle
    }
}
