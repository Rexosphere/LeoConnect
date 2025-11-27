package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class District(
    val districtId: String,
    val name: String,
    val region: String? = null,
    val clubsCount: Int = 0,
    val membersCount: Int = 0,
    val description: String? = null,
    val logoUrl: String? = null,
    val coverImageUrl: String? = null,
    val chairman: ChairmanInfo? = null
)

@Serializable
data class ChairmanInfo(
    val name: String,
    val photoUrl: String? = null,
    val email: String? = null
)
