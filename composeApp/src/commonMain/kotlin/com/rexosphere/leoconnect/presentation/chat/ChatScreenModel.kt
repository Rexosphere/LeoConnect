package com.rexosphere.leoconnect.presentation.chat

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Message
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import com.rexosphere.leoconnect.domain.service.CryptoService
import kotlinx.coroutines.launch

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatScreenModel(
    private val repository: LeoRepository,
    private val cryptoService: CryptoService
) : StateScreenModel<ChatUiState>(ChatUiState.Loading) {
    val uiState = mutableState

    fun loadMessages(userId: String) {
        screenModelScope.launch {
            mutableState.value = ChatUiState.Loading
            repository.getMessages(userId)
                .onSuccess { messages ->
                    // Decrypt messages
                    val decryptedMessages = messages.map { message ->
                        val decryptedContent = if (message.content.startsWith("ENC:")) {
                            // Message is encrypted, try to decrypt
                            val ciphertext = message.content.removePrefix("ENC:")
                            cryptoService.decrypt(ciphertext).getOrElse {
                                "[Failed to decrypt: ${it.message}]"
                            }
                        } else {
                            // Message is not encrypted (backward compatibility)
                            message.content
                        }
                        message.copy(content = decryptedContent)
                    }
                    mutableState.value = ChatUiState.Success(decryptedMessages)
                }
                .onFailure { error ->
                    mutableState.value = ChatUiState.Error(error.message ?: "Failed to load messages")
                }
        }
    }

    fun sendMessage(receiverId: String, content: String, receiverPublicKey: String?) {
        if (content.isBlank()) return

        screenModelScope.launch {
            // Encrypt message if receiver has a public key
            println("E2E Encryption: Receiver public key present: ${receiverPublicKey != null}")
            val messageContent = if (receiverPublicKey != null) {
                println("E2E Encryption: Attempting to encrypt message...")
                cryptoService.encrypt(content, receiverPublicKey).getOrElse {
                    // If encryption fails, send unencrypted with warning
                    println("E2E Encryption: Failed to encrypt message: ${it.message}")
                    it.printStackTrace()
                    content
                }.let { encrypted ->
                    println("E2E Encryption: Message encrypted successfully, length: ${encrypted.length}")
                    "ENC:$encrypted" // Prefix to indicate encrypted message
                }
            } else {
                // Receiver doesn't have public key, send unencrypted
                println("E2E Encryption: Receiver has no public key, sending unencrypted")
                content
            }

            repository.sendMessage(receiverId, messageContent)
                .onSuccess { newMessage ->
                    // Decrypt the message for display (it will be our own encrypted message)
                    val displayContent = if (newMessage.content.startsWith("ENC:")) {
                        val ciphertext = newMessage.content.removePrefix("ENC:")
                        cryptoService.decrypt(ciphertext).getOrElse { content }
                    } else {
                        newMessage.content
                    }
                    
                    val displayMessage = newMessage.copy(content = displayContent)
                    
                    // Add the new message to the list
                    val currentState = mutableState.value
                    if (currentState is ChatUiState.Success) {
                        mutableState.value = ChatUiState.Success(currentState.messages + displayMessage)
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
