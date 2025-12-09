package com.rexosphere.leoconnect.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.rexosphere.leoconnect.data.model.Notification
import com.rexosphere.leoconnect.ui.theme.AppTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<NotificationsScreenModel>()
        val state by screenModel.state.collectAsState()

        NotificationsScreenContent(
            state = state,
            onRefresh = { screenModel.loadNotifications() },
            onNotificationClick = { notification ->
                screenModel.markAsRead(notification.id)
                screenModel.handleNotificationClick(notification)
            },
            onMarkAllRead = { screenModel.markAllAsRead() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenContent(
    state: NotificationsState,
    onRefresh: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onMarkAllRead: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    if (state.notifications.any { !it.isRead }) {
                        TextButton(onClick = onMarkAllRead) {
                            Text("Mark all read")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.notifications.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) {
                            Text("Retry")
                        }
                    }
                }
                state.notifications.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notifications yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.notifications) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = { onNotificationClick(notification) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (!notification.isRead) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon based on notification type
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getNotificationColor(notification.type).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getNotificationIcon(notification.type),
                contentDescription = null,
                tint = getNotificationColor(notification.type),
                modifier = Modifier.size(24.dp)
            )
        }

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(notification.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Unread indicator
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun getNotificationIcon(type: String) = when (type) {
    "message" -> Icons.Default.Email
    "follow" -> Icons.Default.PersonAdd
    "post" -> Icons.Default.Article
    "like" -> Icons.Default.Favorite
    "comment" -> Icons.Default.Comment
    else -> Icons.Default.Notifications
}

@Composable
private fun getNotificationColor(type: String) = when (type) {
    "message" -> MaterialTheme.colorScheme.primary
    "follow" -> MaterialTheme.colorScheme.secondary
    "post" -> MaterialTheme.colorScheme.tertiary
    "like" -> Color(0xFFE91E63)
    "comment" -> Color(0xFF9C27B0)
    else -> MaterialTheme.colorScheme.onSurface
}

@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestamp: String): String {
    // Simple timestamp formatting - you can enhance this
    return try {
        val time = timestamp.toLongOrNull() ?: return timestamp
        val now = Clock.System.now().toEpochMilliseconds()
        val diff = now - time

        when {
            diff < 60000L -> "Just now"
            diff < 3600000L -> "${diff / 60000L}m ago"
            diff < 86400000L -> "${diff / 3600000L}h ago"
            diff < 604800000L -> "${diff / 86400000L}d ago"
            else -> "${diff / 604800000L}w ago"
        }
    } catch (e: Exception) {
        timestamp
    }
}
