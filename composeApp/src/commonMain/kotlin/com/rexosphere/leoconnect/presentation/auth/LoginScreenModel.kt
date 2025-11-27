package com.rexosphere.leoconnect.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val error: String? = null
)

class LoginScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    init {
        // Check if user is already signed in
        _state.update { it.copy(isSignedIn = repository.isSignedIn()) }
    }

    fun signInWithGoogle() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            repository.googleSignIn()
                .onSuccess { userProfile ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSignedIn = true,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSignedIn = false,
                            error = exception.message ?: "Sign in failed"
                        )
                    }
                }
        }
    }
}
