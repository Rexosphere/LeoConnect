package com.rexosphere.leoconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.data.repository.NotificationRepository
import com.rexosphere.leoconnect.presentation.icons.Bell
import com.rexosphere.leoconnect.presentation.icons.BellAlert
import com.rexosphere.leoconnect.presentation.notifications.NotificationsScreen
import org.koin.compose.koinInject

/**
 * Notification bell icon with unread count badge
 * Add this to your app's top bar to show notifications
 * 
 * Shows BellAlert icon when there are unread notifications,
 * otherwise shows regular Bell icon
 */
@Composable
fun NotificationButton(
    modifier: Modifier = Modifier,
    notificationRepository: NotificationRepository = koinInject()
) {
    val navigator = LocalNavigator.currentOrThrow
    val unreadCount by notificationRepository.unreadCount.collectAsState()

    IconButton(
        onClick = { navigator.push(NotificationsScreen()) },
        modifier = modifier
    ) {
        BadgedBox(
            badge = {
                if (unreadCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (unreadCount > 0) BellAlert else Bell,
                contentDescription = if (unreadCount > 0) {
                    "Notifications ($unreadCount unread)"
                } else {
                    "Notifications"
                },
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
