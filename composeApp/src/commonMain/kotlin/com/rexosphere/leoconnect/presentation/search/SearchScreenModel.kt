package com.rexosphere.leoconnect.presentation.search

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class SearchUiState {
    data object Initial : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(
        val posts: List<Post>,
        val clubs: List<Club>,
        val districts: List<String>
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchScreenModel(
    private val repository: LeoRepository
) : StateScreenModel<SearchUiState>(SearchUiState.Initial) {
    val uiState = mutableState
    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            mutableState.value = SearchUiState.Initial
            return
        }

        searchJob = screenModelScope.launch {
            mutableState.value = SearchUiState.Loading
            delay(500) // Debounce

            repository.search(query)
                .onSuccess { result ->
                    mutableState.value = SearchUiState.Success(
                        posts = result.posts,
                        clubs = result.clubs,
                        districts = result.districts
                    )
                }
                .onFailure { error ->
                    mutableState.value = SearchUiState.Error(
                        error.message ?: "Failed to search"
                    )
                }
        }
    }
}
