package com.rexosphere.leoconnect.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val leoId: String = "",
    val selectedClubId: String? = null,
    val districts: List<String> = emptyList(),
    val selectedDistrict: String? = null,
    val clubs: List<Club> = emptyList(),
    val error: String? = null,
    val isCompleted: Boolean = false
)

class OnboardingScreenModel(
    private val repository: LeoRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDistricts()
    }

    private fun loadDistricts() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getDistricts().fold(
                onSuccess = { districts ->
                    _uiState.value = _uiState.value.copy(
                        districts = districts,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun updateLeoId(leoId: String) {
        _uiState.value = _uiState.value.copy(leoId = leoId)
    }

    fun selectDistrict(district: String) {
        _uiState.value = _uiState.value.copy(selectedDistrict = district)
        loadClubsByDistrict(district)
    }

    private fun loadClubsByDistrict(district: String) {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getClubsByDistrict(district).fold(
                onSuccess = { clubs ->
                    _uiState.value = _uiState.value.copy(
                        clubs = clubs,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun selectClub(clubId: String) {
        _uiState.value = _uiState.value.copy(selectedClubId = clubId)
    }

    fun completeOnboarding() {
        val state = _uiState.value
        if (state.leoId.isBlank() && state.selectedClubId == null) {
            _uiState.value = state.copy(error = "Please provide at least LEO ID or select a club")
            return
        }

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                repository.completeOnboarding(
                    leoId = if (state.leoId.isNotBlank()) state.leoId else null,
                    assignedClubId = state.selectedClubId
                ).fold(
                    onSuccess = { profile ->
                        println("Onboarding successful: uid=${profile.uid}, email=${profile.email}, name=${profile.displayName}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isCompleted = true,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        println("Onboarding failed: ${error.message}")
                        error.printStackTrace()
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to complete onboarding: ${error.message}",
                            isLoading = false
                        )
                    }
                )
            } catch (e: Exception) {
                println("Onboarding exception: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    error = "Error during onboarding: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
