package com.rexosphere.leoconnect.presentation.following

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FollowingUiState {
    data object Loading : FollowingUiState
    data class Success(
        val clubs: List<Club>,
        val users: List<FollowingUser>
    ) : FollowingUiState
    data class Error(val message: String) : FollowingUiState
}

data class FollowingUser(
    val uid: String,
    val displayName: String,
    val photoURL: String?,
    val leoId: String?
)

class FollowingScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<FollowingUiState>(FollowingUiState.Loading)
    val uiState: StateFlow<FollowingUiState> = _uiState.asStateFlow()

    fun loadFollowing(userId: String) {
        screenModelScope.launch {
            _uiState.value = FollowingUiState.Loading
            
            // Load following clubs
            val clubsResult = repository.getUserFollowingClubs(userId)
            
            // Load following users
            val usersResult = repository.getUserFollowing(userId)
            
            if (clubsResult.isSuccess && usersResult.isSuccess) {
                val clubs = clubsResult.getOrNull() ?: emptyList()
                val usersResponse = usersResult.getOrNull()
                val users = usersResponse?.following?.map {
                    FollowingUser(
                        uid = it.uid,
                        displayName = it.displayName,
                        photoURL = it.photoURL,
                        leoId = it.leoId
                    )
                } ?: emptyList()
                
                _uiState.value = FollowingUiState.Success(clubs, users)
            } else {
                val error = clubsResult.exceptionOrNull() ?: usersResult.exceptionOrNull()
                _uiState.value = FollowingUiState.Error(
                    error?.message ?: "Failed to load following"
                )
            }
        }
    }
}
