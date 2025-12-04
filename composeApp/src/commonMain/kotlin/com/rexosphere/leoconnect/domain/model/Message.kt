package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val isRead: Boolean,
    val createdAt: String
)

@Serializable
data class Conversation(
    val userId: String,
    val displayName: String,
    val photoUrl: String?,
    val lastMessage: String,
    val lastMessageAt: String,
    val unreadCount: Int
)
