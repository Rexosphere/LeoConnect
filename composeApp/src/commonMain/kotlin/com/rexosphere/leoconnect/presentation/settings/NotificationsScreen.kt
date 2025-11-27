package com.rexosphere.leoconnect.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class NotificationsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var pushNotifications by remember { mutableStateOf(true) }
        var postNotifications by remember { mutableStateOf(true) }
        var commentNotifications by remember { mutableStateOf(true) }
        var likeNotifications by remember { mutableStateOf(false) }
        var clubUpdateNotifications by remember { mutableStateOf(true) }
        var eventReminders by remember { mutableStateOf(true) }
        var emailNotifications by remember { mutableStateOf(false) }
        var soundEnabled by remember { mutableStateOf(true) }
        var vibrationEnabled by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notifications") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "General",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        description = "Enable push notifications",
                        checked = pushNotifications,
                        onCheckedChange = { pushNotifications = it }
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Email,
                        title = "Email Notifications",
                        description = "Receive updates via email",
                        checked = emailNotifications,
                        onCheckedChange = { emailNotifications = it }
                    )
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                item {
                    Text(
                        text = "Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Article,
                        title = "New Posts",
                        description = "Notifications for new posts from clubs you follow",
                        checked = postNotifications,
                        onCheckedChange = { postNotifications = it },
                        enabled = pushNotifications
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Comment,
                        title = "Comments",
                        description = "Someone commented on your post",
                        checked = commentNotifications,
                        onCheckedChange = { commentNotifications = it },
                        enabled = pushNotifications
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Favorite,
                        title = "Likes",
                        description = "Someone liked your post or comment",
                        checked = likeNotifications,
                        onCheckedChange = { likeNotifications = it },
                        enabled = pushNotifications
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Group,
                        title = "Club Updates",
                        description = "Updates from your Leo Club",
                        checked = clubUpdateNotifications,
                        onCheckedChange = { clubUpdateNotifications = it },
                        enabled = pushNotifications
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Event,
                        title = "Event Reminders",
                        description = "Reminders for upcoming Leo events",
                        checked = eventReminders,
                        onCheckedChange = { eventReminders = it },
                        enabled = pushNotifications
                    )
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                item {
                    Text(
                        text = "Notification Style",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.VolumeUp,
                        title = "Sound",
                        description = "Play sound for notifications",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it },
                        enabled = pushNotifications
                    )
                }

                item {
                    NotificationToggleItem(
                        icon = Icons.Default.Vibration,
                        title = "Vibration",
                        description = "Vibrate for notifications",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it },
                        enabled = pushNotifications
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    }
}
