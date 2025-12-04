package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ExclamationTriangle: ImageVector
    get() {
        if (_ExclamationTriangle != null) return _ExclamationTriangle!!

        _ExclamationTriangle = ImageVector.Builder(
            name = "ExclamationTriangle",
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
                moveTo(11.9998f, 9.00006f)
                verticalLineTo(12.7501f)
                moveTo(2.69653f, 16.1257f)
                curveTo(1.83114f, 17.6257f, 2.91371f, 19.5001f, 4.64544f, 19.5001f)
                horizontalLineTo(19.3541f)
                curveTo(21.0858f, 19.5001f, 22.1684f, 17.6257f, 21.303f, 16.1257f)
                lineTo(13.9487f, 3.37819f)
                curveTo(13.0828f, 1.87736f, 10.9167f, 1.87736f, 10.0509f, 3.37819f)
                lineTo(2.69653f, 16.1257f)
                close()
                moveTo(11.9998f, 15.7501f)
                horizontalLineTo(12.0073f)
                verticalLineTo(15.7576f)
                horizontalLineTo(11.9998f)
                verticalLineTo(15.7501f)
                close()
            }
        }.build()

        return _ExclamationTriangle!!
    }

private var _ExclamationTriangle: ImageVector? = null

