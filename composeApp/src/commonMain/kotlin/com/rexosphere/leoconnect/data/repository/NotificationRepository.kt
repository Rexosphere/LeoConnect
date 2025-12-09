package com.rexosphere.leoconnect.data.repository

import com.mmk.kmpnotifier.notification.PayloadData
import com.rexosphere.leoconnect.data.model.Notification
import com.rexosphere.leoconnect.data.model.NotificationListResponse
import com.rexosphere.leoconnect.data.model.NotificationPreferences
import com.rexosphere.leoconnect.data.model.NotificationResponse
import com.rexosphere.leoconnect.data.service.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface NotificationRepository {
    val unreadCount: StateFlow<Int>
    val latestNotification: StateFlow<Notification?>
    
    suspend fun registerDeviceToken()
    suspend fun unregisterDeviceToken()
    suspend fun getNotifications(limit: Int = 50, offset: Int = 0, unreadOnly: Boolean = false): Result<NotificationListResponse>
    suspend fun markAsRead(notificationId: String): Result<NotificationResponse>
    suspend fun markAllAsRead(): Result<NotificationResponse>
    suspend fun getPreferences(): Result<NotificationPreferences>
    suspend fun updatePreferences(preferences: NotificationPreferences): Result<NotificationResponse>
    fun setupNotificationHandlers(
        onNotificationClicked: (data: PayloadData) -> Unit = {}
    )
}

class NotificationRepositoryImpl(
    private val notificationService: NotificationService
) : NotificationRepository {
    
    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    private val _latestNotification = MutableStateFlow<Notification?>(null)
    override val latestNotification: StateFlow<Notification?> = _latestNotification.asStateFlow()
    
    private var currentToken: String? = null

    override suspend fun registerDeviceToken() {
        val token = notificationService.getCurrentToken()
        if (token != null) {
            currentToken = token
            val deviceType = getPlatformType()
            notificationService.registerToken(token, deviceType = deviceType)
                .onSuccess {
                    println("Device token registered successfully")
                }
                .onFailure { error ->
                    println("Failed to register device token: ${error.message}")
                }
        }
    }

    override suspend fun unregisterDeviceToken() {
        currentToken?.let { token ->
            notificationService.removeToken(token)
                .onSuccess {
                    println("Device token unregistered successfully")
                    currentToken = null
                }
                .onFailure { error ->
                    println("Failed to unregister device token: ${error.message}")
                }
        }
    }

    override suspend fun getNotifications(
        limit: Int,
        offset: Int,
        unreadOnly: Boolean
    ): Result<NotificationListResponse> {
        return notificationService.getNotifications(limit, offset, unreadOnly).also { result ->
            result.onSuccess { response ->
                // Update unread count
                val unread = response.notifications.count { !it.isRead }
                _unreadCount.value = unread
            }
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<NotificationResponse> {
        return notificationService.markAsRead(notificationId).also { result ->
            result.onSuccess {
                // Decrease unread count
                if (_unreadCount.value > 0) {
                    _unreadCount.value -= 1
                }
            }
        }
    }

    override suspend fun markAllAsRead(): Result<NotificationResponse> {
        return notificationService.markAllAsRead().also { result ->
            result.onSuccess {
                _unreadCount.value = 0
            }
        }
    }

    override suspend fun getPreferences(): Result<NotificationPreferences> {
        return notificationService.getPreferences()
    }

    override suspend fun updatePreferences(preferences: NotificationPreferences): Result<NotificationResponse> {
        return notificationService.updatePreferences(preferences)
    }

    @OptIn(ExperimentalTime::class)
    override fun setupNotificationHandlers(
        onNotificationClicked: (data: PayloadData) -> Unit
    ) {
        notificationService.setupNotificationListeners(
            onNewToken = { token ->
                // Auto-register new token
                currentToken = token
                // Note: We can't use suspend functions here, so token registration
                // should be handled in the app initialization
            },
            onNotificationReceived = { title, body, data ->
                // Update unread count
                _unreadCount.value += 1
                
                // Create a notification object for the latest notification
                // Note: This is a simplified version, actual implementation may vary
                if (title != null && body != null) {
                    val timestamp = Clock.System.now().toEpochMilliseconds()
                    val notification = Notification(
                        id = timestamp.toString(),
                        type = data["type"] as? String ?: "unknown",
                        title = title,
                        body = body,
                        data = data.mapValues { it.value.toString() },
                        isRead = false,
                        createdAt = timestamp.toString()
                    )
                    _latestNotification.value = notification
                }
            },
            onNotificationClicked = onNotificationClicked
        )
    }

    private fun getPlatformType(): String {
        return getPlatform()
    }
}

// Platform-specific implementation
expect fun getPlatform(): String
