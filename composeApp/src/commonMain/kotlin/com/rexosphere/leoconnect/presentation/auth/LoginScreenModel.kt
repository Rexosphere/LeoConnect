package com.rexosphere.leoconnect.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val needsOnboarding: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val statusMessage: String? = null
)


class LoginScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        screenModelScope.launch {
            if (repository.isSignedIn()) {
                _state.update { it.copy(isLoading = true) }
                repository.getUserProfile()
                    .onSuccess { profile ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSignedIn = true,
                                needsOnboarding = !profile.onboardingCompleted,
                                userProfile = profile
                            )
                        }
                    }
                    .onFailure {
                        _state.update { it.copy(isLoading = false, isSignedIn = false) }
                    }
            }
        }
    }

    fun signInWithGoogle() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, statusMessage = "Signing in...") }

            repository.googleSignIn { status ->
                // Update status message during sign-in
                _state.update { it.copy(statusMessage = status) }
            }
                .onSuccess { userProfile ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSignedIn = true,
                            needsOnboarding = !userProfile.onboardingCompleted,
                            userProfile = userProfile,
                            error = null,
                            statusMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSignedIn = false,
                            error = exception.message ?: "Sign in failed",
                            statusMessage = null
                        )
                    }
                }
        }
    }

}
