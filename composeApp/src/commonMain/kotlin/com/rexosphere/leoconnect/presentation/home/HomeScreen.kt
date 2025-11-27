package com.rexosphere.leoconnect.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.rexosphere.leoconnect.presentation.components.PostCard

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.uiState.collectAsStateWithLifecycle()

        Box(modifier = Modifier.fillMaxSize()) {
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
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.posts) { post ->
                            PostCard(
                                post = post,
                                onLikeClick = { screenModel.likePost(post.postId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
