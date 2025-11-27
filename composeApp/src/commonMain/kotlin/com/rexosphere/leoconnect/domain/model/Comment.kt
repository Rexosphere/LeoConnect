package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val commentId: String,
    val postId: String,
    val userId: String,
    val authorName: String,
    val authorPhotoUrl: String?,
    val content: String,
    val createdAt: String,
    val likesCount: Int = 0,
    val isLikedByUser: Boolean = false
)
