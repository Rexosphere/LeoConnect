package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EllipsisVertical: ImageVector
    get() {
        if (_EllipsisVertical != null) return _EllipsisVertical!!

        _EllipsisVertical = ImageVector.Builder(
            name = "EllipsisVertical",
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
                moveTo(12f, 6.75f)
                curveTo(11.5858f, 6.75f, 11.25f, 6.41421f, 11.25f, 6f)
                curveTo(11.25f, 5.58579f, 11.5858f, 5.25f, 12f, 5.25f)
                curveTo(12.4142f, 5.25f, 12.75f, 5.58579f, 12.75f, 6f)
                curveTo(12.75f, 6.41421f, 12.4142f, 6.75f, 12f, 6.75f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 12.75f)
                curveTo(11.5858f, 12.75f, 11.25f, 12.4142f, 11.25f, 12f)
                curveTo(11.25f, 11.5858f, 11.5858f, 11.25f, 12f, 11.25f)
                curveTo(12.4142f, 11.25f, 12.75f, 11.5858f, 12.75f, 12f)
                curveTo(12.75f, 12.4142f, 12.4142f, 12.75f, 12f, 12.75f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 18.75f)
                curveTo(11.5858f, 18.75f, 11.25f, 18.4142f, 11.25f, 18f)
                curveTo(11.25f, 17.5858f, 11.5858f, 17.25f, 12f, 17.25f)
                curveTo(12.4142f, 17.25f, 12.75f, 17.5858f, 12.75f, 18f)
                curveTo(12.75f, 18.4142f, 12.4142f, 18.75f, 12f, 18.75f)
                close()
            }
        }.build()

        return _EllipsisVertical!!
    }

private var _EllipsisVertical: ImageVector? = null

