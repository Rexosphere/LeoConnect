package com.rexosphere.leoconnect.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.exception.EmailNotVerifiedException
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import com.rexosphere.leoconnect.domain.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Email Authentication Screen
 */
data class EmailAuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUpMode: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val statusMessage: String? = null,
    // Validation states
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    // Navigation states
    val showVerificationScreen: Boolean = false,
    val isSignedIn: Boolean = false,
    val needsOnboarding: Boolean = false
)

/**
 * Screen Model for Email Authentication
 */
class EmailAuthScreenModel(
    private val authService: AuthService,
    private val repository: LeoRepository
) : ScreenModel {

    private val _state = MutableStateFlow(EmailAuthUiState())
    val state: StateFlow<EmailAuthUiState> = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email, emailError = null, error = null) }
    }

    fun updatePassword(password: String) {
        _state.update { it.copy(password = password, passwordError = null, error = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, error = null) }
    }

    fun toggleMode() {
        _state.update { 
            it.copy(
                isSignUpMode = !it.isSignUpMode,
                error = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null
            ) 
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val currentState = _state.value

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (currentState.email.isBlank()) {
            _state.update { it.copy(emailError = "Email is required") }
            isValid = false
        } else if (!emailRegex.matches(currentState.email)) {
            _state.update { it.copy(emailError = "Please enter a valid email address") }
            isValid = false
        }

        if (currentState.password.isBlank()) {
            _state.update { it.copy(passwordError = "Password is required") }
            isValid = false
        } else if (currentState.password.length < 6) {
            _state.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        if (currentState.isSignUpMode) {
            if (currentState.confirmPassword.isBlank()) {
                _state.update { it.copy(confirmPasswordError = "Please confirm your password") }
                isValid = false
            } else if (currentState.password != currentState.confirmPassword) {
                _state.update { it.copy(confirmPasswordError = "Passwords do not match") }
                isValid = false
            }
        }

        return isValid
    }

    fun submit() {
        if (!validateForm()) return

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            if (_state.value.isSignUpMode) {
                signUp()
            } else {
                signIn()
            }
        }
    }

    private suspend fun signUp() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        _state.update { it.copy(statusMessage = "Creating account...") }

        authService.createUserWithEmailPassword(email, password)
            .onSuccess { token ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        showVerificationScreen = true
                    ) 
                }
            }
            .onFailure { exception ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        error = exception.message ?: "Failed to create account"
                    ) 
                }
            }
    }

    private suspend fun signIn() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        _state.update { it.copy(statusMessage = "Signing in...") }

        authService.signInWithEmailPassword(email, password)
            .onSuccess { token ->
                // Email is verified, proceed with backend authentication
                _state.update { it.copy(statusMessage = "Authenticating...") }
                completeSignIn()
            }
            .onFailure { exception ->
                if (exception is EmailNotVerifiedException) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            showVerificationScreen = true
                        ) 
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            error = exception.message ?: "Sign in failed"
                        ) 
                    }
                }
            }
    }

    private suspend fun completeSignIn() {
        repository.emailSignIn { status ->
            _state.update { it.copy(statusMessage = status) }
        }
            .onSuccess { userProfile ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSignedIn = true,
                        needsOnboarding = !userProfile.onboardingCompleted,
                        statusMessage = null
                    )
                }
            }
            .onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Authentication failed",
                        statusMessage = null
                    )
                }
            }
    }

    fun resendVerificationEmail() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, statusMessage = "Sending verification email...") }

            authService.sendEmailVerification()
                .onSuccess {
                    _state.update { it.copy(isLoading = false, statusMessage = null, error = null) }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            error = exception.message ?: "Failed to send verification email"
                        ) 
                    }
                }
        }
    }

    fun checkVerificationStatus() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, statusMessage = "Checking verification status...") }

            authService.reloadUser()

            if (authService.isEmailVerified()) {
                // Email verified! Sign out and redirect to sign-in form
                authService.signOut()
                _state.update { 
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        showVerificationScreen = false,
                        isSignUpMode = false,
                        password = "",
                        confirmPassword = "",
                        error = null
                    ) 
                }
            } else {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        error = "Email not yet verified. Please check your inbox and click the verification link."
                    ) 
                }
            }
        }
    }

    fun backFromVerification() {
        screenModelScope.launch {
            authService.signOut()
            _state.update { 
                it.copy(
                    showVerificationScreen = false,
                    password = "",
                    confirmPassword = "",
                    error = null
                ) 
            }
        }
    }
}
