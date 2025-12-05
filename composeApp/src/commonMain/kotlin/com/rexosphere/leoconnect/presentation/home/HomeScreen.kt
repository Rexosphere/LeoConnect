package com.rexosphere.leoconnect.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.components.PostCard
import com.rexosphere.leoconnect.presentation.components.EmptyState
import com.rexosphere.leoconnect.presentation.components.PullToRefreshContainer
import com.rexosphere.leoconnect.presentation.icons.MagnifyingGlass
import com.rexosphere.leoconnect.presentation.icons.Plus
import com.rexosphere.leoconnect.presentation.postdetail.PostDetailScreen
import com.rexosphere.leoconnect.presentation.search.SearchScreen
import com.rexosphere.leoconnect.presentation.userprofile.UserProfileScreen
import com.rexosphere.leoconnect.presentation.createpost.CreatePostScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.uiState.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        var isRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(state) {
            if (state !is HomeUiState.Loading) {
                isRefreshing = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("LeoConnect") },
                    actions = {
                        IconButton(onClick = { navigator.push(SearchScreen()) }) {
                            Icon(MagnifyingGlass, "Search")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(CreatePostScreen()) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Plus, contentDescription = "Create Post")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(uiState.posts, key = { it.postId }) { post ->
                                        PostCard(
                                            post = post,
                                            onLikeClick = { screenModel.likePost(post.postId) },
                                            onPostClick = { navigator.push(PostDetailScreen(post)) },
                                            onUserClick = { userId -> navigator.push(UserProfileScreen(userId)) }
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
