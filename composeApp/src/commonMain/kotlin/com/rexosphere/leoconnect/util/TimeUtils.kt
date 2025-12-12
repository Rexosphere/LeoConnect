package com.rexosphere.leoconnect.util

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

/**
 * Formats a timestamp string to a relative time string (e.g., "2h ago", "3d ago")
 */
@OptIn(ExperimentalTime::class)
fun formatTimeAgo(timestamp: String): String {
    if (timestamp.isEmpty()) return ""
    
    return try {
        val postTime = Instant.parse(timestamp)
        val now = Clock.System.now()
        val duration = now - postTime
        
        when {
            duration < 1.minutes -> "Just now"
            duration < 1.hours -> "${duration.inWholeMinutes}m ago"
            duration < 1.days -> "${duration.inWholeHours}h ago"
            duration < 7.days -> "${duration.inWholeDays}d ago"
            duration < 30.days -> "${duration.inWholeDays / 7}w ago"
            duration < 365.days -> "${duration.inWholeDays / 30}mo ago"
            else -> "${duration.inWholeDays / 365}y ago"
        }
    } catch (e: Exception) {
        ""
    }
}
