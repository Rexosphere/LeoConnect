package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MapPin: ImageVector
    get() {
        if (_MapPin != null) return _MapPin!!

        _MapPin = ImageVector.Builder(
            name = "MapPin",
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
                moveTo(15f, 10.5f)
                curveTo(15f, 12.1569f, 13.6569f, 13.5f, 12f, 13.5f)
                curveTo(10.3431f, 13.5f, 9f, 12.1569f, 9f, 10.5f)
                curveTo(9f, 8.84315f, 10.3431f, 7.5f, 12f, 7.5f)
                curveTo(13.6569f, 7.5f, 15f, 8.84315f, 15f, 10.5f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19.5f, 10.5f)
                curveTo(19.5f, 17.6421f, 12f, 21.75f, 12f, 21.75f)
                curveTo(12f, 21.75f, 4.5f, 17.6421f, 4.5f, 10.5f)
                curveTo(4.5f, 6.35786f, 7.85786f, 3f, 12f, 3f)
                curveTo(16.1421f, 3f, 19.5f, 6.35786f, 19.5f, 10.5f)
                close()
            }
        }.build()

        return _MapPin!!
    }

private var _MapPin: ImageVector? = null

