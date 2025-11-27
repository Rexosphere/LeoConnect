package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val clubId: String,
    val name: String,
    val district: String,
    val districtId: String = "",
    val description: String? = null,
    val logoUrl: String? = null,
    val coverImageUrl: String? = null,
    val membersCount: Int = 0,
    val followersCount: Int = 0,
    val isFollowing: Boolean = false,
    val address: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val socialLinks: SocialLinks? = null
)

@Serializable
data class SocialLinks(
    val facebook: String? = null,
    val instagram: String? = null,
    val twitter: String? = null
)
