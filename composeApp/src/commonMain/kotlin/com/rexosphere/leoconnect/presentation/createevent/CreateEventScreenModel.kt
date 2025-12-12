package com.rexosphere.leoconnect.presentation.createevent

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Event
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CreateEventUiState {
    data object Idle : CreateEventUiState()
    data object Loading : CreateEventUiState()
    data class Success(val event: Event) : CreateEventUiState()
    data class Error(val message: String) : CreateEventUiState()
}

class CreateEventScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<CreateEventUiState>(CreateEventUiState.Idle) {
    val uiState = mutableState

    private val _userClubId = MutableStateFlow<String?>(null)
    val userClubId = _userClubId.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        screenModelScope.launch {
            repository.getUserProfile().onSuccess { profile ->
                _userClubId.value = profile.assignedClubId
            }
        }
    }

    fun createEvent(
        name: String,
        description: String,
        eventDate: String,
        imageBytes: String?
    ) {
        if (name.isBlank()) {
            mutableState.value = CreateEventUiState.Error("Event name cannot be empty")
            return
        }
        if (description.isBlank()) {
            mutableState.value = CreateEventUiState.Error("Event description cannot be empty")
            return
        }
        if (eventDate.isBlank()) {
            mutableState.value = CreateEventUiState.Error("Event date cannot be empty")
            return
        }

        screenModelScope.launch {
            mutableState.value = CreateEventUiState.Loading
            repository.createEvent(
                name = name,
                description = description,
                eventDate = eventDate,
                clubId = _userClubId.value,
                imageBytes = imageBytes
            )
                .onSuccess { event ->
                    mutableState.value = CreateEventUiState.Success(event)
                }
                .onFailure { error ->
                    mutableState.value = CreateEventUiState.Error(error.message ?: "Failed to create event")
                }
        }
    }

    fun resetState() {
        mutableState.value = CreateEventUiState.Idle
    }
}
