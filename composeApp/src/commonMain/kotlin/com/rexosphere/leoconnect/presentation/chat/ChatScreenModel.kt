package com.rexosphere.leoconnect.presentation.chat

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Message
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.launch

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<ChatUiState>(ChatUiState.Loading) {
    val uiState = mutableState

    fun loadMessages(userId: String) {
        screenModelScope.launch {
            mutableState.value = ChatUiState.Loading
            repository.getMessages(userId)
                .onSuccess { messages ->
                    mutableState.value = ChatUiState.Success(messages)
                }
                .onFailure { error ->
                    mutableState.value = ChatUiState.Error(error.message ?: "Failed to load messages")
                }
        }
    }

    fun sendMessage(receiverId: String, content: String) {
        if (content.isBlank()) return

        screenModelScope.launch {
            repository.sendMessage(receiverId, content)
                .onSuccess { newMessage ->
                    // Add the new message to the list
                    val currentState = mutableState.value
                    if (currentState is ChatUiState.Success) {
                        mutableState.value = ChatUiState.Success(currentState.messages + newMessage)
                    }
                }
                .onFailure { error ->
                    // TODO: Show error to user
                    println("Failed to send message: ${error.message}")
                }
        }
    }

    fun deleteMessage(messageId: String) {
        screenModelScope.launch {
            // Optimistically remove the message from UI
            val currentState = mutableState.value
            if (currentState is ChatUiState.Success) {
                val updatedMessages = currentState.messages.filter { it.id != messageId }
                mutableState.value = ChatUiState.Success(updatedMessages)
            }

            repository.deleteMessage(messageId)
                .onFailure { error ->
                    // Revert on failure
                    if (currentState is ChatUiState.Success) {
                        mutableState.value = currentState
                    }
                    println("Failed to delete message: ${error.message}")
                }
        }
    }
}
