package com.rexosphere.leoconnect.presentation.search

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
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

class SearchScreenModel : StateScreenModel<SearchUiState>(SearchUiState.Initial) {
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

            // TODO: Search via repository
            // For now, use mock data
            val mockPosts = listOf(
                Post(
                    postId = "1",
                    clubId = "1",
                    clubName = "Leo Club City",
                    authorName = "John Doe",
                    authorLogo = null,
                    content = "This is a test post about ${query}. It contains relevant information.",
                    imageUrl = null,
                    likesCount = 15,
                    commentsCount = 3
                ),
                Post(
                    postId = "2",
                    clubId = "2",
                    clubName = "Leo Club University",
                    authorName = "Jane Smith",
                    authorLogo = null,
                    content = "Another post mentioning ${query} with interesting content.",
                    imageUrl = null,
                    likesCount = 25,
                    commentsCount = 7
                )
            )

            val mockClubs = listOf(
                Club(
                    clubId = "1",
                    name = "Leo Club ${query}",
                    district = "District 306A",
                    description = "A club matching your search",
                    membersCount = 50,
                    followersCount = 120
                )
            )

            val mockDistricts = listOf("District 306A", "District 306B")

            mutableState.value = SearchUiState.Success(
                posts = mockPosts,
                clubs = mockClubs,
                districts = mockDistricts
            )
        }
    }
}
