package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Newspaper: ImageVector
    get() {
        if (_Newspaper != null) return _Newspaper!!

        _Newspaper = ImageVector.Builder(
            name = "Newspaper",
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
                moveTo(12f, 7.5f)
                horizontalLineTo(13.5f)
                moveTo(12f, 10.5f)
                horizontalLineTo(13.5f)
                moveTo(6f, 13.5f)
                horizontalLineTo(13.5f)
                moveTo(6f, 16.5f)
                horizontalLineTo(13.5f)
                moveTo(16.5f, 7.5f)
                horizontalLineTo(19.875f)
                curveTo(20.4963f, 7.5f, 21f, 8.00368f, 21f, 8.625f)
                verticalLineTo(18f)
                curveTo(21f, 19.2426f, 19.9926f, 20.25f, 18.75f, 20.25f)
                moveTo(16.5f, 7.5f)
                verticalLineTo(18f)
                curveTo(16.5f, 19.2426f, 17.5074f, 20.25f, 18.75f, 20.25f)
                moveTo(16.5f, 7.5f)
                verticalLineTo(4.875f)
                curveTo(16.5f, 4.25368f, 15.9963f, 3.75f, 15.375f, 3.75f)
                horizontalLineTo(4.125f)
                curveTo(3.50368f, 3.75f, 3f, 4.25368f, 3f, 4.875f)
                verticalLineTo(18f)
                curveTo(3f, 19.2426f, 4.00736f, 20.25f, 5.25f, 20.25f)
                horizontalLineTo(18.75f)
                moveTo(6f, 7.5f)
                horizontalLineTo(9f)
                verticalLineTo(10.5f)
                horizontalLineTo(6f)
                verticalLineTo(7.5f)
                close()
            }
        }.build()

        return _Newspaper!!
    }

private var _Newspaper: ImageVector? = null

