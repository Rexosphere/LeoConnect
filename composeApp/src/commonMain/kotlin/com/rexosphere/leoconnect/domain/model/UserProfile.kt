package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoURL: String?,
    val leoId: String?,
    val isWebmaster: Boolean = false,
    val assignedClubId: String?
)
