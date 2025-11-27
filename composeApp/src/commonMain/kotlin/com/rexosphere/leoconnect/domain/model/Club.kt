package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val clubId: String,
    val name: String,
    val district: String,
    val description: String?
)
