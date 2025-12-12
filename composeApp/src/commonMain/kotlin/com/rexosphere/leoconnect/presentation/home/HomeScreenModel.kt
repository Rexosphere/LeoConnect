package com.rexosphere.leoconnect.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val posts: List<Post>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Success(val events: List<com.rexosphere.leoconnect.domain.model.Event>) : EventsUiState
    data class Error(val message: String) : EventsUiState
}

class HomeScreenModel(
    private val repository: LeoRepository
) : ScreenModel {

    private val _currentTab = MutableStateFlow(HomeTab.FEED)
    val currentTab: StateFlow<HomeTab> = _currentTab.asStateFlow()

    private val _feedState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val feedState: StateFlow<HomeUiState> = _feedState.asStateFlow()

    private val _exploreState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val exploreState: StateFlow<HomeUiState> = _exploreState.asStateFlow()

    private val _eventsState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val eventsState: StateFlow<EventsUiState> = _eventsState.asStateFlow()

    init {
        loadFeed()
    }

    fun switchTab(tab: HomeTab) {
        _currentTab.value = tab
        when (tab) {
            HomeTab.FEED -> if (_feedState.value is HomeUiState.Loading) loadFeed()
            HomeTab.EXPLORE -> if (_exploreState.value is HomeUiState.Loading) loadExploreFeed()
            HomeTab.EVENTS -> if (_eventsState.value is EventsUiState.Loading) loadEvents()
        }
    }

    fun loadFeed() {
        screenModelScope.launch {
            _feedState.value = HomeUiState.Loading
            repository.getHomeFeed(20)
                .onSuccess { posts ->
                    _feedState.value = HomeUiState.Success(posts)
                }
                .onFailure { error ->
                    _feedState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun loadExploreFeed() {
        screenModelScope.launch {
            _exploreState.value = HomeUiState.Loading
            repository.getExploreFeed(20)
                .onSuccess { posts ->
                    _exploreState.value = HomeUiState.Success(posts)
                }
                .onFailure { error ->
                    _exploreState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun loadEvents() {
        screenModelScope.launch {
            _eventsState.value = EventsUiState.Loading
            repository.getEvents(20)
                .onSuccess { events ->
                    _eventsState.value = EventsUiState.Success(events)
                }
                .onFailure { error ->
                    _eventsState.value = EventsUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun likePost(postId: String) {
        screenModelScope.launch {
            // Optimistic update for current tab
            val currentState = when (_currentTab.value) {
                HomeTab.FEED -> _feedState.value
                HomeTab.EXPLORE -> _exploreState.value
                HomeTab.EVENTS -> return@launch // No posts in events tab
            }

            if (currentState is HomeUiState.Success) {
                val updatedPosts = currentState.posts.map { post ->
                    if (post.postId == postId) {
                        post.copy(
                            isLikedByUser = !post.isLikedByUser,
                            likesCount = if (post.isLikedByUser) post.likesCount - 1 else post.likesCount + 1
                        )
                    } else {
                        post
                    }
                }

                when (_currentTab.value) {
                    HomeTab.FEED -> _feedState.value = HomeUiState.Success(updatedPosts)
                    HomeTab.EXPLORE -> _exploreState.value = HomeUiState.Success(updatedPosts)
                    HomeTab.EVENTS -> {}
                }
            }

            // Send network request
            repository.likePost(postId)
                .onFailure {
                    // Revert optimistic update on failure
                    if (currentState is HomeUiState.Success) {
                        when (_currentTab.value) {
                            HomeTab.FEED -> _feedState.value = currentState
                            HomeTab.EXPLORE -> _exploreState.value = currentState
                            HomeTab.EVENTS -> {}
                        }
                    }
                }
        }
    }

    fun deletePost(postId: String) {
        screenModelScope.launch {
            // Optimistic update - remove post from current list
            val currentState = when (_currentTab.value) {
                HomeTab.FEED -> _feedState.value
                HomeTab.EXPLORE -> _exploreState.value
                HomeTab.EVENTS -> return@launch
            }

            if (currentState is HomeUiState.Success) {
                val updatedPosts = currentState.posts.filter { it.postId != postId }

                when (_currentTab.value) {
                    HomeTab.FEED -> _feedState.value = HomeUiState.Success(updatedPosts)
                    HomeTab.EXPLORE -> _exploreState.value = HomeUiState.Success(updatedPosts)
                    HomeTab.EVENTS -> {}
                }
            }

            // Send network request
            repository.deletePost(postId)
                .onFailure {
                    // Revert on failure
                    if (currentState is HomeUiState.Success) {
                        when (_currentTab.value) {
                            HomeTab.FEED -> _feedState.value = currentState
                            HomeTab.EXPLORE -> _exploreState.value = currentState
                            HomeTab.EVENTS -> {}
                        }
                    }
                }
        }
    }

    fun rsvpEvent(eventId: String) {
        screenModelScope.launch {
            // Optimistic update
            val currentState = _eventsState.value
            if (currentState is EventsUiState.Success) {
                val updatedEvents = currentState.events.map { event ->
                    if (event.eventId == eventId) {
                        event.copy(
                            hasRSVPd = !event.hasRSVPd,
                            rsvpCount = if (event.hasRSVPd) event.rsvpCount - 1 else event.rsvpCount + 1
                        )
                    } else {
                        event
                    }
                }
                _eventsState.value = EventsUiState.Success(updatedEvents)
            }

            // Send network request
            repository.rsvpEvent(eventId)
                .onFailure {
                    // Revert on failure
                    if (currentState is EventsUiState.Success) {
                        _eventsState.value = currentState
                    }
                }
        }
    }

    fun deleteEvent(eventId: String) {
        screenModelScope.launch {
            // Optimistic update - remove event from list
            val currentState = _eventsState.value
            if (currentState is EventsUiState.Success) {
                val updatedEvents = currentState.events.filter { it.eventId != eventId }
                _eventsState.value = EventsUiState.Success(updatedEvents)
            }

            // Send network request
            repository.deleteEvent(eventId)
                .onFailure {
                    // Revert on failure
                    if (currentState is EventsUiState.Success) {
                        _eventsState.value = currentState
                    }
                }
        }
    }
}
