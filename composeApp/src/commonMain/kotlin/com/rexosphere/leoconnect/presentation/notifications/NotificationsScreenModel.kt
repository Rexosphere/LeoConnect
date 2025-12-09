package com.rexosphere.leoconnect.presentation.notifications

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.data.model.Notification
import com.rexosphere.leoconnect.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val unreadCount: Int = 0
)

class NotificationsScreenModel(
    private val notificationRepository: NotificationRepository
) : ScreenModel {
    
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        loadNotifications()
        observeUnreadCount()
    }

    fun loadNotifications(unreadOnly: Boolean = false) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            notificationRepository.getNotifications(unreadOnly = unreadOnly)
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            notifications = response.notifications,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load notifications"
                        )
                    }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        screenModelScope.launch {
            notificationRepository.markAsRead(notificationId)
                .onSuccess {
                    // Update the notification in the list
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.map { notification ->
                                if (notification.id == notificationId) {
                                    notification.copy(isRead = true)
                                } else {
                                    notification
                                }
                            }
                        )
                    }
                }
        }
    }

    fun markAllAsRead() {
        screenModelScope.launch {
            notificationRepository.markAllAsRead()
                .onSuccess {
                    // Mark all notifications as read in the list
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.map { it.copy(isRead = true) }
                        )
                    }
                }
        }
    }

    fun handleNotificationClick(notification: Notification) {
        // Handle navigation based on notification type
        // This can be implemented based on your app's navigation structure
        when (notification.type) {
            "message" -> {
                // Navigate to chat screen
                val senderId = notification.data?.get("senderId")
                println("Navigate to chat with user: $senderId")
            }
            "follow" -> {
                // Navigate to follower's profile
                val followerId = notification.data?.get("followerId")
                println("Navigate to profile: $followerId")
            }
            "post" -> {
                // Navigate to post detail
                val postId = notification.data?.get("postId")
                println("Navigate to post: $postId")
            }
            "like", "comment" -> {
                // Navigate to post detail
                val postId = notification.data?.get("postId")
                println("Navigate to post: $postId")
            }
        }
    }

    private fun observeUnreadCount() {
        screenModelScope.launch {
            notificationRepository.unreadCount.collect { count ->
                _state.update { it.copy(unreadCount = count) }
            }
        }
    }
}
