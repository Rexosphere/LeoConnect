package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoURL: String? = null,
    val leoId: String? = null,
    val bio: String? = null,
    val isWebmaster: Boolean = false,
    val isVerified: Boolean = false,
    val assignedClubId: String? = null,
    val followingClubs: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
    val publicKey: String? = null, // RSA public key for E2E encryption
    val postsCount: Int? = 0,
    val followersCount: Int? = 0,
    val followingCount: Int? = 0,
    val isFollowing: Boolean? = false,
    val isMutualFollow: Boolean? = false
)


