package com.rexosphere.leoconnect.presentation.tabs

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Conversation
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MessagesUiState {
    data object Loading : MessagesUiState()
    data class Success(val conversations: List<Conversation>) : MessagesUiState()
    data class Error(val message: String) : MessagesUiState()
}

sealed class UserSearchState {
    data object Idle : UserSearchState()
    data object Loading : UserSearchState()
    data class Success(val users: List<com.rexosphere.leoconnect.data.source.remote.UserSearchResult>) : UserSearchState()
    data class Error(val message: String) : UserSearchState()
}

class MessagesScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<MessagesUiState>(MessagesUiState.Loading) {
    val uiState = mutableState

    private val _userSearchState = MutableStateFlow<UserSearchState>(UserSearchState.Idle)
    val userSearchState: StateFlow<UserSearchState> = _userSearchState

    init {
        loadConversations()
    }

    fun loadConversations() {
        screenModelScope.launch {
            mutableState.value = MessagesUiState.Loading
            repository.getConversations()
                .onSuccess { conversations ->
                    mutableState.value = MessagesUiState.Success(conversations)
                }
                .onFailure { error ->
                    mutableState.value = MessagesUiState.Error(error.message ?: "Failed to load conversations")
                }
        }
    }

    fun deleteConversation(userId: String) {
        screenModelScope.launch {
            // Optimistically remove the conversation from UI
            val currentState = mutableState.value
            if (currentState is MessagesUiState.Success) {
                val updatedConversations = currentState.conversations.filter { it.userId != userId }
                mutableState.value = MessagesUiState.Success(updatedConversations)
            }

            repository.deleteConversation(userId)
                .onFailure { error ->
                    // Revert on failure
                    if (currentState is MessagesUiState.Success) {
                        mutableState.value = currentState
                    }
                    println("Failed to delete conversation: ${error.message}")
                }
        }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _userSearchState.value = UserSearchState.Idle
            return
        }

        screenModelScope.launch {
            _userSearchState.value = UserSearchState.Loading
            repository.searchUsers(query)
                .onSuccess { users ->
                    _userSearchState.value = UserSearchState.Success(users)
                }
                .onFailure { error ->
                    _userSearchState.value = UserSearchState.Error(error.message ?: "Failed to search users")
                }
        }
    }

    fun resetUserSearch() {
        _userSearchState.value = UserSearchState.Idle
    }
}
