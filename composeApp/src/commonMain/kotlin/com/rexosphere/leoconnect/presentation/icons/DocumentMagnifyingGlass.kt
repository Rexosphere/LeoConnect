package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DocumentMagnifyingGlass: ImageVector
    get() {
        if (_DocumentMagnifyingGlass != null) return _DocumentMagnifyingGlass!!

        _DocumentMagnifyingGlass = ImageVector.Builder(
            name = "DocumentMagnifyingGlass",
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
                moveTo(19.5f, 14.25f)
                verticalLineTo(11.625f)
                curveTo(19.5f, 9.76104f, 17.989f, 8.25f, 16.125f, 8.25f)
                horizontalLineTo(14.625f)
                curveTo(14.0037f, 8.25f, 13.5f, 7.74632f, 13.5f, 7.125f)
                verticalLineTo(5.625f)
                curveTo(13.5f, 3.76104f, 11.989f, 2.25f, 10.125f, 2.25f)
                horizontalLineTo(8.25f)
                moveTo(13.4812f, 15.7312f)
                lineTo(15f, 17.25f)
                moveTo(10.5f, 2.25f)
                horizontalLineTo(5.625f)
                curveTo(5.00368f, 2.25f, 4.5f, 2.75368f, 4.5f, 3.375f)
                verticalLineTo(19.875f)
                curveTo(4.5f, 20.4963f, 5.00368f, 21f, 5.625f, 21f)
                horizontalLineTo(18.375f)
                curveTo(18.9963f, 21f, 19.5f, 20.4963f, 19.5f, 19.875f)
                verticalLineTo(11.25f)
                curveTo(19.5f, 6.27944f, 15.4706f, 2.25f, 10.5f, 2.25f)
                close()
                moveTo(14.25f, 13.875f)
                curveTo(14.25f, 15.3247f, 13.0747f, 16.5f, 11.625f, 16.5f)
                curveTo(10.1753f, 16.5f, 9f, 15.3247f, 9f, 13.875f)
                curveTo(9f, 12.4253f, 10.1753f, 11.25f, 11.625f, 11.25f)
                curveTo(13.0747f, 11.25f, 14.25f, 12.4253f, 14.25f, 13.875f)
                close()
            }
        }.build()

        return _DocumentMagnifyingGlass!!
    }

private var _DocumentMagnifyingGlass: ImageVector? = null

