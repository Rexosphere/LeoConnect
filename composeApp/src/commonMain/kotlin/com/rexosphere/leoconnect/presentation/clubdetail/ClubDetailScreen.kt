package com.rexosphere.leoconnect.presentation.clubdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.presentation.components.PostCard
import com.rexosphere.leoconnect.presentation.postdetail.PostDetailScreen
import com.rexosphere.leoconnect.presentation.userprofile.UserProfileScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

data class ClubDetailScreen(val club: Club) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ClubDetailScreenModel>()
        val state by screenModel.uiState.collectAsState()

        LaunchedEffect(club.id) {
            screenModel.loadClubPosts(club.id)
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Club") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item { ClubHeader(club = club) }
                item { Spacer(Modifier.height(24.dp)) }

                when (val uiState = state) {
                    is ClubDetailUiState.Loading -> {
                        item {
                            Box(Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is ClubDetailUiState.Success -> {
                        if (uiState.posts.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.PostAdd, null, modifier = Modifier.size(80.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                        Spacer(Modifier.height(16.dp))
                                        Text("No posts yet", style = MaterialTheme.typography.titleMedium)
                                        Text("Be the first to post!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        } else {
                            items(uiState.posts) { post ->
                                PostCard(
                                    post = post,
                                    onLikeClick = { screenModel.toggleLike(post.postId) },
                                    onPostClick = { navigator.push(PostDetailScreen(post)) },
                                    onUserClick = { userId -> navigator.push(UserProfileScreen(userId)) },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            }
                        }
                    }
                    is ClubDetailUiState.Error -> {
                        item {
                            Text(uiState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun ClubHeader(club: Club) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Top border
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Club Logo
                if (club.logoUrl != null) {
                    KamelImage(
                        resource = asyncPainterResource(club.logoUrl),
                        contentDescription = "Club logo",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onLoading = { Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) },
                        onFailure = {
                            Box(Modifier.size(88.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Groups, null, modifier = Modifier.size(40.dp))
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Groups, null, modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.width(20.dp))

                // Stats
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = club.membersCount, label = "Members")
                        StatItem(count = club.followersCount, label = "Followers")
                        StatItem(count = club.postsCount ?: 0, label = "Posts")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Club Name + Verified
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = club.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                )
                if (club.isOfficial == true) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Verified, "Official Club", tint = Color(0xFF1DA1F2), modifier = Modifier.size(26.dp))
                }
            }

            Text(
                text = club.district,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            // Description
            if (!club.description.isNullOrBlank()) {
                Text(
                    text = club.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Follow */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Follow", fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = { /* Message / View Members */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Members")
                }
            }

            if (club.isUserAdmin == true) {
                Spacer(Modifier.height(12.dp))
                AssistChip(
                    onClick = { /* Open admin menu */ },
                    label = { Text("You are an admin") },
                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }
}

@Composable
private fun StatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
