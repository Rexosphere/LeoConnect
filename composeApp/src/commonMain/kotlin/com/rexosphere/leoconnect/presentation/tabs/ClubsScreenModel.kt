package com.rexosphere.leoconnect.presentation.tabs

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ClubsUiState {
    data object Loading : ClubsUiState
    data class Success(
        val districts: List<String>,
        val selectedDistrict: String?,
        val clubs: List<Club>
    ) : ClubsUiState
    data class Error(val message: String) : ClubsUiState
}

class ClubsScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<ClubsUiState>(ClubsUiState.Loading)
    val uiState: StateFlow<ClubsUiState> = _uiState.asStateFlow()

    init {
        loadDistricts()
    }

    fun loadDistricts() {
        screenModelScope.launch {
            _uiState.value = ClubsUiState.Loading
            repository.getDistricts()
                .onSuccess { districts ->
                    if (districts.isNotEmpty()) {
                        loadClubs(districts.first(), districts)
                    } else {
                        _uiState.value = ClubsUiState.Success(emptyList(), null, emptyList())
                    }
                }
                .onFailure { error ->
                    _uiState.value = ClubsUiState.Error(error.message ?: "Failed to load districts")
                }
        }
    }

    fun selectDistrict(district: String) {
        val currentState = _uiState.value
        if (currentState is ClubsUiState.Success) {
            loadClubs(district, currentState.districts)
        }
    }

    private fun loadClubs(district: String, districts: List<String>) {
        screenModelScope.launch {
            // Keep showing current content but maybe with a loading indicator overlay?
            // For simplicity, we'll just switch to Success with empty clubs momentarily or keep old ones
            // Let's just update the state when data arrives to avoid flickering to full loading screen
            
            repository.getClubsByDistrict(district)
                .onSuccess { clubs ->
                    _uiState.value = ClubsUiState.Success(districts, district, clubs)
                }
                .onFailure { error ->
                    _uiState.value = ClubsUiState.Error(error.message ?: "Failed to load clubs")
                }
        }
    }
}
