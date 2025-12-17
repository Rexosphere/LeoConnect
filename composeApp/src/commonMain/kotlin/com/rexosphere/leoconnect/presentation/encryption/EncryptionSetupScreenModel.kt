package com.rexosphere.leoconnect.presentation.encryption

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import com.rexosphere.leoconnect.domain.service.CryptoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class EncryptionSetupState {
    data object Loading : EncryptionSetupState()
    data object GeneratingKeys : EncryptionSetupState()
    data object UploadingKeys : EncryptionSetupState()
    data class KeyConflict(val hasLocalKeys: Boolean) : EncryptionSetupState()
    data object Success : EncryptionSetupState()
    data class Error(val message: String) : EncryptionSetupState()
}

class EncryptionSetupScreenModel(
    private val repository: LeoRepository,
    private val cryptoService: CryptoService
) : ScreenModel {
    private val _state = MutableStateFlow<EncryptionSetupState>(EncryptionSetupState.Loading)
    val state: StateFlow<EncryptionSetupState> = _state.asStateFlow()

    init {
        checkAndSetupEncryption()
    }

    private fun checkAndSetupEncryption() {
        screenModelScope.launch {
            try {
                _state.value = EncryptionSetupState.Loading
                
                val hasLocalKeys = cryptoService.hasKeyPair()
                
                // Generate keys if we don't have them
                if (!hasLocalKeys) {
                    _state.value = EncryptionSetupState.GeneratingKeys
                    println("E2E Encryption: Generating new key pair...")
                    
                    val keyGenResult = cryptoService.generateKeyPair()
                    if (keyGenResult.isFailure) {
                        _state.value = EncryptionSetupState.Error(
                            keyGenResult.exceptionOrNull()?.message ?: "Failed to generate keys"
                        )
                        return@launch
                    }
                    println("E2E Encryption: Key pair generated successfully")
                }
                
                // Try to upload keys
                uploadKeys(force = false)
                
            } catch (e: Exception) {
                println("E2E Encryption: Error during setup: ${e.message}")
                e.printStackTrace()
                _state.value = EncryptionSetupState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun uploadKeys(force: Boolean) {
        try {
            _state.value = EncryptionSetupState.UploadingKeys
            println("E2E Encryption: Uploading public key (force=$force)...")
            
            repository.setupEncryptionKeys(force).onSuccess {
                println("E2E Encryption: Keys uploaded successfully")
                _state.value = EncryptionSetupState.Success
            }.onFailure { error ->
                // Check if it's a conflict error
                if (error.message?.contains("409") == true || error.message?.contains("conflict") == true) {
                    println("E2E Encryption: Key conflict detected")
                    val hasLocalKeys = cryptoService.hasKeyPair()
                    _state.value = EncryptionSetupState.KeyConflict(hasLocalKeys)
                } else {
                    println("E2E Encryption: Upload failed: ${error.message}")
                    _state.value = EncryptionSetupState.Error(error.message ?: "Failed to upload keys")
                }
            }
        } catch (e: Exception) {
            println("E2E Encryption: Upload error: ${e.message}")
            _state.value = EncryptionSetupState.Error(e.message ?: "Failed to upload keys")
        }
    }

    fun useNewKeys() {
        screenModelScope.launch {
            println("E2E Encryption: User chose to use new keys (overwrite)")
            uploadKeys(force = true)
        }
    }

    fun useExistingKeys() {
        screenModelScope.launch {
            println("E2E Encryption: User chose to keep existing keys")
            // Clear local keys and mark as success (user will use server keys)
            cryptoService.clearKeys()
            _state.value = EncryptionSetupState.Success
        }
    }

    fun retry() {
        checkAndSetupEncryption()
    }
}
