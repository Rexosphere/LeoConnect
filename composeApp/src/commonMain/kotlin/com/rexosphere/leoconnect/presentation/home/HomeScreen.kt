package com.rexosphere.leoconnect.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.service.AuthService
import com.rexosphere.leoconnect.presentation.LocalBottomBarPadding
import com.rexosphere.leoconnect.presentation.components.*
import com.rexosphere.leoconnect.presentation.createevent.CreateEventScreen
import com.rexosphere.leoconnect.presentation.createpost.CreatePostScreen
import com.rexosphere.leoconnect.presentation.icons.MagnifyingGlass
import com.rexosphere.leoconnect.presentation.postdetail.PostDetailScreen
import com.rexosphere.leoconnect.presentation.search.SearchScreen
import com.rexosphere.leoconnect.presentation.userprofile.UserProfileScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials
import org.koin.compose.koinInject

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val currentTab by screenModel.currentTab.collectAsStateWithLifecycle()
        val feedState by screenModel.feedState.collectAsStateWithLifecycle()
        val exploreState by screenModel.exploreState.collectAsStateWithLifecycle()
        val eventsState by screenModel.eventsState.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        val bottomBarPadding = LocalBottomBarPadding.current
        val hazeState = remember { HazeState() }
        val authService = koinInject<AuthService>()
        val currentUserId = authService.getCurrentUserId()

        var isFeedRefreshing by remember { mutableStateOf(false) }
        var isExploreRefreshing by remember { mutableStateOf(false) }
        var isEventsRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(feedState) {
            if (feedState !is HomeUiState.Loading) isFeedRefreshing = false
        }
        LaunchedEffect(exploreState) {
            if (exploreState !is HomeUiState.Loading) isExploreRefreshing = false
        }
        LaunchedEffect(eventsState) {
            if (eventsState !is EventsUiState.Loading) isEventsRefreshing = false
        }

        Scaffold(
            topBar = {
                val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

                Column {
                    TopAppBar(
                        title = {
                            Text(
                                "LeoConnect",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        actions = {
                            IconButton(onClick = { navigator.push(SearchScreen()) }) {
                                Icon(MagnifyingGlass, "Search")
                            }
                            NotificationButton()
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .hazeChild(
                                state = hazeState,
                                style = HazeMaterials.thin(MaterialTheme.colorScheme.surface)
                            )
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                val y = size.height - strokeWidth
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            borderColor,
                                            Color.Transparent
                                        )
                                    ),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                    )

                    // Tab Row
                    TabRow(
                        selectedTabIndex = currentTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = currentTab == HomeTab.FEED,
                            onClick = { screenModel.switchTab(HomeTab.FEED) },
                            text = { Text("Feed") }
                        )
                        Tab(
                            selected = currentTab == HomeTab.EXPLORE,
                            onClick = { screenModel.switchTab(HomeTab.EXPLORE) },
                            text = { Text("Explore") }
                        )
                        Tab(
                            selected = currentTab == HomeTab.EVENTS,
                            onClick = { screenModel.switchTab(HomeTab.EVENTS) },
                            text = { Text("Events") }
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .haze(state = hazeState)
            ) {
                when (currentTab) {
                    HomeTab.FEED -> {
                        FeedTab(
                            state = feedState,
                            isRefreshing = isFeedRefreshing,
                            onRefresh = {
                                isFeedRefreshing = true
                                screenModel.loadFeed()
                            },
                            onLike = { screenModel.likePost(it) },
                            onDelete = { screenModel.deletePost(it) },
                            onPostClick = { post -> navigator.push(PostDetailScreen(post)) },
                            onAuthorClick = { navigator.push(UserProfileScreen(it)) },
                            currentUserId = currentUserId,
                            bottomBarPadding = bottomBarPadding
                        )
                    }
                    HomeTab.EXPLORE -> {
                        ExploreTab(
                            state = exploreState,
                            isRefreshing = isExploreRefreshing,
                            onRefresh = {
                                isExploreRefreshing = true
                                screenModel.loadExploreFeed()
                            },
                            onLike = { screenModel.likePost(it) },
                            onDelete = { screenModel.deletePost(it) },
                            onPostClick = { post -> navigator.push(PostDetailScreen(post)) },
                            onAuthorClick = { navigator.push(UserProfileScreen(it)) },
                            currentUserId = currentUserId,
                            bottomBarPadding = bottomBarPadding
                        )
                    }
                    HomeTab.EVENTS -> {
                        EventsTab(
                            state = eventsState,
                            isRefreshing = isEventsRefreshing,
                            onRefresh = {
                                isEventsRefreshing = true
                                screenModel.loadEvents()
                            },
                            onRSVP = { screenModel.rsvpEvent(it) },
                            onDelete = { screenModel.deleteEvent(it) },
                            currentUserId = currentUserId,
                            bottomBarPadding = bottomBarPadding,
                            navigator = navigator,
                            hazeState = hazeState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedTab(
    state: HomeUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLike: (String) -> Unit,
    onDelete: (String) -> Unit,
    onPostClick: (Post) -> Unit,
    onAuthorClick: (String) -> Unit,
    currentUserId: String?,
    bottomBarPadding: Dp
) {
    when (state) {
        is HomeUiState.Loading -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = bottomBarPadding + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(5) {
                    PostCardShimmer()
                }
            }
        }
        is HomeUiState.Success -> {
            if (state.posts.isEmpty()) {
                EmptyState(
                    message = "No posts yet",
                )
            } else {
                PullToRefreshContainer(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = bottomBarPadding + 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.posts, key = { it.postId }) { post ->
                            PostCard(
                                post = post,
                                onLike = { onLike(post.postId) },
                                onDelete = if (post.authorId == currentUserId) {
                                    { onDelete(post.postId) }
                                } else null,
                                onClick = { onPostClick(post) },
                                onAuthorClick = { onAuthorClick(post.authorId) }
                            )
                        }
                    }
                }
            }
        }
        is HomeUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreTab(
    state: HomeUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLike: (String) -> Unit,
    onDelete: (String) -> Unit,
    onPostClick: (Post) -> Unit,
    onAuthorClick: (String) -> Unit,
    currentUserId: String?,
    bottomBarPadding: Dp
) {
    // Same as FeedTab but for explore
    FeedTab(
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onLike = onLike,
        onDelete = onDelete,
        onPostClick = onPostClick,
        onAuthorClick = onAuthorClick,
        currentUserId = currentUserId,
        bottomBarPadding = bottomBarPadding
    )
}

@Composable
private fun EventsTab(
    state: EventsUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRSVP: (String) -> Unit,
    onDelete: (String) -> Unit,
    currentUserId: String?,
    bottomBarPadding: Dp,
    navigator: cafe.adriel.voyager.navigator.Navigator,
    hazeState: HazeState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
        is EventsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is EventsUiState.Success -> {
            if (state.events.isEmpty()) {
                EmptyState(
                    message = "No events yet",
                )
            } else {
                PullToRefreshContainer(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = bottomBarPadding + 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.events, key = { it.eventId }) { event ->
                            EventCard(
                                event = event,
                                currentUserId = currentUserId,
                                onRSVP = { onRSVP(event.eventId) },
                                onDelete = if (event.authorId == currentUserId) {
                                    { onDelete(event.eventId) }
                                } else null
                            )
                        }
                    }
                }
            }
        }
        is EventsUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }
        }
        }
        
        // FAB for creating events
        FloatingActionButton(
            onClick = { 
                navigator.push(CreateEventScreen())
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = bottomBarPadding)
        ) {
            Icon(
                com.rexosphere.leoconnect.presentation.icons.Plus,
                contentDescription = "Create Event",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
