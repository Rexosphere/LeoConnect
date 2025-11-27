package com.rexosphere.leoconnect.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.rexosphere.leoconnect.domain.model.Post
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onPostClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onPostClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (post.authorLogo != null) {
                    KamelImage(
                        resource = { asyncPainterResource(data = post.authorLogo) },
                        contentDescription = "Author Logo",
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onLoading = { CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) },
                        onFailure = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                } else {
                    // Placeholder
                    Spacer(modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = post.authorName, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            Text(text = post.content, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Image
            if (post.imageUrl != null) {
                KamelImage(
                    resource = { asyncPainterResource(data = post.imageUrl) },
                    contentDescription = "Post Image",
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    onLoading = { progress -> CircularProgressIndicator(progress) },
                    onFailure = { exception -> 
                        // Log exception if possible or show error icon
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer (Likes)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (post.isLikedByUser) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(text = "${post.likesCount} Likes", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
