package com.rexosphere.leoconnect.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.presentation.icons.ExclamationTriangle
import com.rexosphere.leoconnect.presentation.icons.FilledHeart
import com.rexosphere.leoconnect.presentation.icons.Heart
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.util.ClickableTextWithLinks
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit = {},
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    if (onDelete != null) {
                        showDeleteDialog = true
                    }
                }
            )
            .padding(horizontal = 16.dp)
    ) {
        // Top border (1.dp thin line)
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            // Header: Avatar + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAuthorClick(post.authorId) }
            ) {
                if (post.authorLogo != null) {
                    KamelImage(
                        resource = { asyncPainterResource(data = post.authorLogo) },
                        contentDescription = "Author avatar",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        },
                        onFailure = {
                            Icon(
                                User,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post content
            ClickableTextWithLinks(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                linkColor = MaterialTheme.colorScheme.primary
            )

            // Post image (if exists)
            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                KamelImage(
                    resource = { asyncPainterResource(data = post.imageUrl) },
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.large), // slight corner radius like Threads
                    contentScale = ContentScale.Crop,
                    onLoading = { CircularProgressIndicator(it) },
                    onFailure = {
                        Icon(
                            ExclamationTriangle,
                            contentDescription = "Failed to load",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Like row
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike) {
                    Icon(
                        imageVector = if (post.isLikedByUser) FilledHeart else Heart,
                        contentDescription = "Like",
                        tint = if (post.isLikedByUser)
                            Color(0xFFE91E63) // Instagram/Threads pink-red
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (post.likesCount > 0) "${post.likesCount} likes" else "Like",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bottom border
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete?.invoke()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}