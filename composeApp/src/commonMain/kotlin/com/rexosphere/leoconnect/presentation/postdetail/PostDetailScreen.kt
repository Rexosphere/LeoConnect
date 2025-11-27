package com.rexosphere.leoconnect.presentation.postdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        val uiState by screenModel.uiState.collectAsState()
        var commentText by remember { mutableStateOf("") }

        LaunchedEffect(post.postId) {
            screenModel.loadComments(post.postId)
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Post") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                CommentInputBar(
                    text = commentText,
                    onTextChange = { commentText = it },
                    onSendClick = {
                        if (commentText.isNotBlank()) {
                            screenModel.addComment(post.postId, commentText.trim())
                            commentText = ""
                        }
                    },
                    isSending = uiState is PostDetailUiState.Loading
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Post
                item {
                    ThreadsStylePostItem(
                        post = post,
                        onLikeClick = { screenModel.toggleLike(post.postId) }
                    )
                }

                // Comments Header
                item {
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (post.commentsCount > 0) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "· ${post.commentsCount}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Comments List
                when (val state = uiState) {
                    is PostDetailUiState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is PostDetailUiState.Success -> {
                        if (state.comments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No comments yet.\nBe the first to comment!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            items(state.comments, key = { it.commentId }) { comment ->
                                ThreadsStyleCommentItem(
                                    comment = comment,
                                    onLikeClick = { screenModel.toggleCommentLike(comment.commentId) }
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

                item { Spacer(Modifier.height(80.dp)) } // Extra space for bottom bar
            }
        }
    }
}

@Composable
private fun ThreadsStylePostItem(
    post: Post,
    onLikeClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Top border
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Author Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                KamelImage(
                    resource = asyncPainterResource(post.authorLogo ?: ""),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    onLoading = { CircularProgressIndicator(strokeWidth = 2.dp) },
                    onFailure = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(post.authorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(post.clubName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Post Text
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 26.sp
            )

            // Image
            post.imageUrl?.let { url ->
                Spacer(Modifier.height(12.dp))
                KamelImage(
                    resource = asyncPainterResource(url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(12.dp))

            // Like Button + Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (post.isLikedByUser) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLikedByUser) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (post.likesCount > 0) "${post.likesCount}" else "Like",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (post.isLikedByUser) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bottom border
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }
}

@Composable
private fun ThreadsStyleCommentItem(
    comment: Comment,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        KamelImage(
            resource = asyncPainterResource(comment.authorPhotoUrl ?: ""),
            contentDescription = null,
            modifier = Modifier.size(36.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
            onFailure = { Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) }
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatTimeAgo(comment.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                TextButton(onClick = onLikeClick, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = if (comment.likesCount > 0) "${comment.likesCount} likes" else "Like",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (comment.isLikedByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { /* Reply */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Reply", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun CommentInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add a comment...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f)
                ),
                singleLine = true
            )

            Spacer(Modifier.width(12.dp))

            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank() && !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatTimeAgo(timestamp: String): String {
    // Replace with real time formatting (e.g., TimeAgo library)
    return "2h ago"
}