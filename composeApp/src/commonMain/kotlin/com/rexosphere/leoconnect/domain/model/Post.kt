package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val postId: String,
    val clubId: String,
    val clubName: String = "",
    val authorId: String = "",
    val authorName: String,
    val authorLogo: String?,
    val content: String,
    val imageUrl: String?,
    val images: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)
