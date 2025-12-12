package com.rexosphere.leoconnect.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * @param length Diagonal length of the element
 */
fun Modifier.shimmer(length: Float): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer transition")
    val offset by transition.animateFloat(
        initialValue = -100f,
        targetValue = length + 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient offset"
    )
    val color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
    val startOffset = -length / 4 + offset
    val endOffset = length / 4 + offset
    val brush = Brush.linearGradient(
        colors = listOf(
            color.copy(alpha = 0.2f),
            color.copy(alpha = 0.6f),
            color.copy(alpha = 0.2f)
        ),
        start = Offset(x = startOffset, y = startOffset),
        end = Offset(x = endOffset, y = endOffset)
    )
    this.then(background(brush))
}
