package com.rexosphere.leoconnect.presentation.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.data.source.remote.LeoAiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AiChatUiState {
    data object Idle : AiChatUiState()
    data object Loading : AiChatUiState()
    data class Success(val messages: List<AiChatMessage>) : AiChatUiState()
    data class Error(val message: String) : AiChatUiState()
}

class LeoAiChatScreenModel(
    private val aiService: LeoAiService
) : ScreenModel {

    private val _uiState = MutableStateFlow<AiChatUiState>(AiChatUiState.Idle)
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    private val messages = mutableListOf<AiChatMessage>()

    fun getMessages(): List<AiChatMessage> = messages.toList()

    fun sendMessage(question: String) {
        // Add user message
        val userMessage = AiChatMessage(
            id = UUID.randomUUID().toString(),
            content = question,
            isUser = true
        )
        messages.add(userMessage)
        _uiState.value = AiChatUiState.Success(messages.toList())

        // Get AI response
        screenModelScope.launch {
            _uiState.value = AiChatUiState.Loading
            try {
                val response = aiService.askQuestion(question)
                
                if (response.success) {
                    val aiMessage = AiChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = response.answer,
                        isUser = false
                    )
                    messages.add(aiMessage)
                    _uiState.value = AiChatUiState.Success(messages.toList())
                } else {
                    _uiState.value = AiChatUiState.Error("Failed to get response from AI")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AiChatUiState.Error(
                    e.message ?: "Failed to connect to Leo AI. Please check your internet connection."
                )
                // Keep the messages visible even on error
                screenModelScope.launch {
                    kotlinx.coroutines.delay(3000)
                    if (_uiState.value is AiChatUiState.Error) {
                        _uiState.value = AiChatUiState.Success(messages.toList())
                    }
                }
            }
        }
    }

    fun deleteMessage(messageId: String) {
        messages.removeAll { it.id == messageId }
        _uiState.value = if (messages.isEmpty()) {
            AiChatUiState.Idle
        } else {
            AiChatUiState.Success(messages.toList())
        }
    }
}
