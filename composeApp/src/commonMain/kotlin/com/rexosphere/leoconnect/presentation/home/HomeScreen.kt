package com.rexosphere.leoconnect.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.LocalBottomBarPadding
import com.rexosphere.leoconnect.presentation.components.EmptyState
import com.rexosphere.leoconnect.presentation.components.PostCard
import com.rexosphere.leoconnect.presentation.components.PullToRefreshContainer
import com.rexosphere.leoconnect.presentation.createpost.CreatePostScreen
import com.rexosphere.leoconnect.presentation.icons.MagnifyingGlass
import com.rexosphere.leoconnect.presentation.postdetail.PostDetailScreen
import com.rexosphere.leoconnect.presentation.search.SearchScreen
import com.rexosphere.leoconnect.presentation.userprofile.UserProfileScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.uiState.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        val bottomBarPadding = LocalBottomBarPadding.current
        val hazeState = remember { HazeState() }
        var isRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(state) {
            if (state !is HomeUiState.Loading) {
                isRefreshing = false
            }
        }

        Scaffold(
            topBar = {
                // 1. Capture the color outside the draw scope
                val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

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
                                        borderColor, // 2. Use the captured value here
                                        Color.Transparent
                                    )
                                ),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .haze(state = hazeState)
            ) {
                when (val uiState = state) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is HomeUiState.Error -> {
                        Text(
                            text = uiState.message,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is HomeUiState.Success -> {
                        if (uiState.posts.isEmpty()) {
                            EmptyState(
                                onRefresh = { screenModel.loadFeed() }
                            )
                        } else {
                            PullToRefreshContainer(
                                isRefreshing = isRefreshing,
                                onRefresh = {
                                    isRefreshing = true
                                    screenModel.loadFeed()
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = bottomBarPadding + 16.dp)
                                ) {
                                    items(uiState.posts, key = { it.postId }) { post ->
                                        PostCard(
                                            post = post,
                                            onLikeClick = { screenModel.likePost(post.postId) },
                                            onPostClick = { navigator.push(PostDetailScreen(post)) },
                                            onUserClick = { userId ->
                                                navigator.push(
                                                    UserProfileScreen(userId)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
