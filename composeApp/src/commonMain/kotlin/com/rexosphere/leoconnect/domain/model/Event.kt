package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventId: String,
    val title: String,
    val date: String, // ISO 8601 string preferred, or Long timestamp
    val location: String,
    val rsvpCount: Int = 0
)
