package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val postId: String,
    val clubId: String,
    val authorName: String,
    val authorLogo: String?,
    val content: String,
    val imageUrl: String?,
    val likesCount: Int = 0,
    val isLikedByUser: Boolean = false
)
