package com.rexosphere.leoconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationTokenRequest(
    val token: String,
    val deviceId: String? = null,
    val deviceType: String? = null
)

@Serializable
data class NotificationResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class Notification(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null,
    val isRead: Boolean,
    val createdAt: String
)

@Serializable
data class NotificationListResponse(
    val notifications: List<Notification>,
    val total: Int,
    val hasMore: Boolean
)

@Serializable
data class NotificationPreferences(
    val messagesEnabled: Boolean = true,
    val followsEnabled: Boolean = true,
    val postsEnabled: Boolean = true,
    val likesEnabled: Boolean = false,
    val commentsEnabled: Boolean = true
)
