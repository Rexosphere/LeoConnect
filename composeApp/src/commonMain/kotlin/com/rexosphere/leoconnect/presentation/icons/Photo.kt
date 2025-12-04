package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Photo: ImageVector
    get() {
        if (_Photo != null) return _Photo!!

        _Photo = ImageVector.Builder(
            name = "Photo",
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
                moveTo(2.25f, 15.75f)
                lineTo(7.40901f, 10.591f)
                curveTo(8.28769f, 9.71231f, 9.71231f, 9.71231f, 10.591f, 10.591f)
                lineTo(15.75f, 15.75f)
                moveTo(14.25f, 14.25f)
                lineTo(15.659f, 12.841f)
                curveTo(16.5377f, 11.9623f, 17.9623f, 11.9623f, 18.841f, 12.841f)
                lineTo(21.75f, 15.75f)
                moveTo(3.75f, 19.5f)
                horizontalLineTo(20.25f)
                curveTo(21.0784f, 19.5f, 21.75f, 18.8284f, 21.75f, 18f)
                verticalLineTo(6f)
                curveTo(21.75f, 5.17157f, 21.0784f, 4.5f, 20.25f, 4.5f)
                horizontalLineTo(3.75f)
                curveTo(2.92157f, 4.5f, 2.25f, 5.17157f, 2.25f, 6f)
                verticalLineTo(18f)
                curveTo(2.25f, 18.8284f, 2.92157f, 19.5f, 3.75f, 19.5f)
                close()
                moveTo(14.25f, 8.25f)
                horizontalLineTo(14.2575f)
                verticalLineTo(8.2575f)
                horizontalLineTo(14.25f)
                verticalLineTo(8.25f)
                close()
                moveTo(14.625f, 8.25f)
                curveTo(14.625f, 8.45711f, 14.4571f, 8.625f, 14.25f, 8.625f)
                curveTo(14.0429f, 8.625f, 13.875f, 8.45711f, 13.875f, 8.25f)
                curveTo(13.875f, 8.04289f, 14.0429f, 7.875f, 14.25f, 7.875f)
                curveTo(14.4571f, 7.875f, 14.625f, 8.04289f, 14.625f, 8.25f)
                close()
            }
        }.build()

        return _Photo!!
    }

private var _Photo: ImageVector? = null

