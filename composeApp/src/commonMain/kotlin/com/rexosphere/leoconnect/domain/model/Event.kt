package com.rexosphere.leoconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventId: String,
    val clubId: String?,
    val clubName: String?,
    val authorId: String,
    val authorName: String,
    val name: String,
    val description: String,
    val eventDate: String,
    val imageUrl: String?,
    val rsvpCount: Int,
    val hasRSVPd: Boolean,
    val rsvpParticipants: List<RSVPParticipant>,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class RSVPParticipant(
    val uid: String,
    val displayName: String,
    val photoUrl: String?
)

@Serializable
data class RSVPResponse(
    val message: String,
    val rsvpCount: Int,
    val hasRSVPd: Boolean
)
