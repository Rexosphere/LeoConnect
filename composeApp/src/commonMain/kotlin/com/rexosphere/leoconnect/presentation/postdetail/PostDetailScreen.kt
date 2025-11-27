package com.rexosphere.leoconnect.presentation.postdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.domain.model.Comment
import com.rexosphere.leoconnect.domain.model.Post
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

data class PostDetailScreen(val post: Post) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<PostDetailScreenModel>()

        LaunchedEffect(post.postId) {
            screenModel.loadComments(post.postId)
        }

        val uiState by screenModel.uiState.collectAsState()
        var commentText by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Post") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                        IconButton(onClick = { /* TODO: More options */ }) {
                            Icon(Icons.Default.MoreVert, "More")
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Write a comment...") },
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    screenModel.addComment(post.postId, commentText)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Send")
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Post content
                item {
                    PostDetailContent(
                        post = post,
                        onLikeClick = { screenModel.toggleLike(post.postId) },
                        onCommentClick = { /* Focus comment field */ },
                        onShareClick = { /* TODO: Share */ }
                    )
                }

                // Comments header
                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Comments (${post.commentsCount})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Comments list
                when (val state = uiState) {
                    is PostDetailUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is PostDetailUiState.Success -> {
                        items(state.comments, key = { it.commentId }) { comment ->
                            CommentItem(
                                comment = comment,
                                onLikeClick = { screenModel.toggleCommentLike(comment.commentId) }
                            )
                        }
                        if (state.comments.isEmpty()) {
                            item {
                                Text(
                                    text = "No comments yet. Be the first to comment!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    is PostDetailUiState.Error -> {
                        item {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostDetailContent(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Author info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (post.authorLogo != null) {
                KamelImage(
                    resource = { asyncPainterResource(data = post.authorLogo) },
                    contentDescription = "Author Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    onLoading = { CircularProgressIndicator(modifier = Modifier.size(24.dp)) },
                    onFailure = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.clubName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (post.createdAt.isNotEmpty()) {
                    Text(
                        text = formatTimeAgo(post.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Content
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Image
        if (post.imageUrl != null) {
            KamelImage(
                resource = { asyncPainterResource(data = post.imageUrl) },
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                contentScale = ContentScale.Crop,
                onLoading = { progress ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                onFailure = {
                    Icon(Icons.Default.Warning, contentDescription = "Error",
                         tint = MaterialTheme.colorScheme.error)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${post.likesCount} likes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "${post.commentsCount} comments",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${post.sharesCount} shares",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Divider()

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = if (post.isLikedByUser) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                text = "Like",
                tint = if (post.isLikedByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                onClick = onLikeClick
            )
            ActionButton(
                icon = Icons.Default.ModeComment,
                text = "Comment",
                onClick = onCommentClick
            )
            ActionButton(
                icon = Icons.Default.Share,
                text = "Share",
                onClick = onShareClick
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (comment.authorPhotoUrl != null) {
            KamelImage(
                resource = { asyncPainterResource(data = comment.authorPhotoUrl) },
                contentDescription = "Commenter Photo",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onLoading = { CircularProgressIndicator(modifier = Modifier.size(18.dp)) },
                onFailure = { Icon(Icons.Default.Person, contentDescription = null) }
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = comment.authorName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = comment.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = formatTimeAgo(comment.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onLikeClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (comment.isLikedByUser) "Liked" else "Like",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (comment.isLikedByUser) FontWeight.Bold else FontWeight.Normal
                    )
                    if (comment.likesCount > 0) {
                        Text(
                            text = " · ${comment.likesCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                TextButton(
                    onClick = { /* TODO: Reply */ },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Reply",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = tint)
    }
}

fun formatTimeAgo(timestamp: String): String {
    // Simplified time ago formatter
    // In real app, use proper datetime library
    return if (timestamp.isNotEmpty()) {
        "Just now" // Placeholder
    } else {
        ""
    }
}
