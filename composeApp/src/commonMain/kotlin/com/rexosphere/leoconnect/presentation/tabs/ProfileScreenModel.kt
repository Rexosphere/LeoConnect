package com.rexosphere.leoconnect.presentation.tabs

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        screenModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.getUserProfile()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState.Success(profile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Failed to load profile")
                }
        }
    }

    fun updateProfile(leoId: String?, assignedClubId: String?) {
        screenModelScope.launch {
            // Optimistic update or loading state could be added here
            repository.updateUserProfile(leoId, assignedClubId)
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState.Success(profile)
                }
                .onFailure { error ->
                    // Handle error, maybe show snackbar
                }
        }
    }

    fun signOut() {
        screenModelScope.launch {
            repository.signOut()
            // Navigation to login should be handled by observing auth state in main activity or app
        }
    }
}
