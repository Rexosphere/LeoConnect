package com.rexosphere.leoconnect.presentation.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.presentation.components.PostCard
import com.rexosphere.leoconnect.presentation.components.PullToRefreshContainer
import com.rexosphere.leoconnect.presentation.icons.CheckBadge
import com.rexosphere.leoconnect.presentation.icons.CheckCircle
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.CodeBracket
import com.rexosphere.leoconnect.presentation.icons.EllipsisVertical
import com.rexosphere.leoconnect.presentation.icons.ExclamationTriangle
import com.rexosphere.leoconnect.presentation.icons.Newspaper
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.presentation.postdetail.PostDetailScreen
import com.rexosphere.leoconnect.presentation.chat.ChatScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

data class UserProfileScreen(val userId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<UserProfileScreenModel>()
        val state by screenModel.uiState.collectAsState()
        var isRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(userId) {
            screenModel.loadUser(userId)
        }

        LaunchedEffect(state) {
            if (state !is UserProfileUiState.Loading) {
                isRefreshing = false
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Report / Block */ }) {
                            Icon(EllipsisVertical, contentDescription = "More")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            when (val uiState = state) {
                is UserProfileUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UserProfileUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(ExclamationTriangle, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Text(uiState.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                is UserProfileUiState.Success -> {
                    val profile = uiState.profile
                    PullToRefreshContainer(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            screenModel.loadUser(userId)
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                        item { OtherUserHeader(profile = profile, screenModel = screenModel, navigator = navigator) }
                        item { Spacer(Modifier.height(24.dp)) }

                        if (uiState.posts.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Newspaper,
                                            null,
                                            modifier = Modifier.size(80.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Text("No posts yet", style = MaterialTheme.typography.titleMedium)
                                        Text("This Leo hasn't posted anything", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        } else {
                            items(uiState.posts, key = { it.postId }) { post ->
                                PostCard(
                                    post = post.copy(
                                        authorName = profile.displayName,
                                        authorLogo = profile.photoURL
                                    ),
                                    onLikeClick = { screenModel.toggleLike(post.postId) },
                                    onPostClick = { navigator.push(PostDetailScreen(post)) },
                                    onUserClick = { userId -> navigator.push(UserProfileScreen(userId)) },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            }
                        }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OtherUserHeader(
    profile: UserProfile,
    screenModel: UserProfileScreenModel,
    navigator: cafe.adriel.voyager.navigator.Navigator
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Profile Picture
                if (profile.photoURL != null) {
                    KamelImage(
                        resource = asyncPainterResource(profile.photoURL),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onLoading = { Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) },
                        onFailure = {
                            Box(Modifier.size(96.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center) {
                                Icon(User, null, modifier = Modifier.size(48.dp))
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(User, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.width(20.dp))

                // Stats
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = profile.postsCount ?: 0, label = "Posts")
                        StatItem(count = profile.followersCount ?: 0, label = "Followers")
                        StatItem(count = profile.followingCount ?: 0, label = "Following")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Name + Leo ID + Verified
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        )
                    )
                    if (!profile.leoId.isNullOrEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Icon(CheckBadge, "Verified Leo", tint = Color(0xFF1DA1F2), modifier = Modifier.size(26.dp))
                    }
                }

                if (!profile.leoId.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Leo ID: ${profile.leoId}",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (profile.isWebmaster == true) {
                    Spacer(Modifier.height(6.dp))
                    Surface(color = Color(0xFFFF6B6B).copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Icon(CodeBracket, null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF6B6B))
                            Spacer(Modifier.width(4.dp))
                            Text("Webmaster", color = Color(0xFFFF6B6B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bio
            if (!profile.bio.isNullOrBlank()) {
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))
            }

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val isFollowing = profile.isFollowing == true

                Button(
                    onClick = { screenModel.toggleFollow() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (isFollowing) "Following" else "Follower",
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedButton(
                    onClick = {
                        navigator.push(ChatScreen(profile))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Message")
                }
            }

            if (profile.isMutualFollow == true) {
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = { },
                    label = { Text("Follows you") },
                    leadingIcon = { Icon(CheckCircle, null, modifier = Modifier.size(16.dp)) }
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
