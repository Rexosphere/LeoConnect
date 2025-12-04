package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowPathRoundedSquare: ImageVector
    get() {
        if (_ArrowPathRoundedSquare != null) return _ArrowPathRoundedSquare!!

        _ArrowPathRoundedSquare = ImageVector.Builder(
            name = "ArrowPathRoundedSquare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19.5f, 12f)
                curveTo(19.5f, 10.7681f, 19.4536f, 9.54699f, 19.3624f, 8.3384f)
                curveTo(19.2128f, 6.35425f, 17.6458f, 4.78724f, 15.6616f, 4.63757f)
                curveTo(14.453f, 4.54641f, 13.2319f, 4.5f, 12f, 4.5f)
                curveTo(10.7681f, 4.5f, 9.54699f, 4.54641f, 8.3384f, 4.63757f)
                curveTo(6.35425f, 4.78724f, 4.78724f, 6.35425f, 4.63757f, 8.3384f)
                curveTo(4.62097f, 8.55852f, 4.60585f, 8.77906f, 4.59222f, 9f)
                moveTo(19.5f, 12f)
                lineTo(22.5f, 9f)
                moveTo(19.5f, 12f)
                lineTo(16.5f, 9f)
                moveTo(4.5f, 12f)
                curveTo(4.5f, 13.2319f, 4.54641f, 14.453f, 4.63757f, 15.6616f)
                curveTo(4.78724f, 17.6458f, 6.35425f, 19.2128f, 8.3384f, 19.3624f)
                curveTo(9.54699f, 19.4536f, 10.7681f, 19.5f, 12f, 19.5f)
                curveTo(13.2319f, 19.5f, 14.453f, 19.4536f, 15.6616f, 19.3624f)
                curveTo(17.6458f, 19.2128f, 19.2128f, 17.6458f, 19.3624f, 15.6616f)
                curveTo(19.379f, 15.4415f, 19.3941f, 15.2209f, 19.4078f, 15f)
                moveTo(4.5f, 12f)
                lineTo(7.5f, 15f)
                moveTo(4.5f, 12f)
                lineTo(1.5f, 15f)
            }
        }.build()

        return _ArrowPathRoundedSquare!!
    }

private var _ArrowPathRoundedSquare: ImageVector? = null

