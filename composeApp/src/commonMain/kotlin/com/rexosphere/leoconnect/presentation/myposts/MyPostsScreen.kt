package com.rexosphere.leoconnect.presentation.myposts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.presentation.components.PostCard
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.ExclamationTriangle

data class MyPostsScreen(val userId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MyPostsScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var showDeleteDialog by remember { mutableStateOf<Post?>(null) }

        LaunchedEffect(userId) {
            screenModel.loadMyPosts(userId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Posts") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            when (val uiState = state) {
                is MyPostsUiState.Loading -> {
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MyPostsUiState.Error -> {
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(ExclamationTriangle, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Text(uiState.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { screenModel.loadMyPosts(userId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is MyPostsUiState.Success -> {
                    if (uiState.posts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No posts yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(uiState.posts, key = { it.postId }) { post ->
                                PostCard(
                                    post = post,
                                    onLike = { screenModel.likePost(post.postId) },
                                    onDelete = { showDeleteDialog = post },
                                    onClick = { /* Navigate to post detail */ },
                                    onAuthorClick = { /* Navigate to user profile */ }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        showDeleteDialog?.let { post ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Post") },
                text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            screenModel.deletePost(post.postId)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
