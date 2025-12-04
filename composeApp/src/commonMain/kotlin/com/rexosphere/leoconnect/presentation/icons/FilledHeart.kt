package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val FilledHeart: ImageVector
    get() {
        if (_FilledHeart != null) return _FilledHeart!!

        _FilledHeart = ImageVector.Builder(
            name = "FilledHeart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF0F172A))
            ) {
                moveTo(21f, 8.25f)
                curveTo(21f, 5.76472f, 18.9013f, 3.75f, 16.3125f, 3.75f)
                curveTo(14.3769f, 3.75f, 12.7153f, 4.87628f, 12f, 6.48342f)
                curveTo(11.2847f, 4.87628f, 9.62312f, 3.75f, 7.6875f, 3.75f)
                curveTo(5.09867f, 3.75f, 3f, 5.76472f, 3f, 8.25f)
                curveTo(3f, 15.4706f, 12f, 20.25f, 12f, 20.25f)
                curveTo(12f, 20.25f, 21f, 15.4706f, 21f, 8.25f)
                close()
            }
        }.build()

        return _FilledHeart!!
    }

private var _FilledHeart: ImageVector? = null