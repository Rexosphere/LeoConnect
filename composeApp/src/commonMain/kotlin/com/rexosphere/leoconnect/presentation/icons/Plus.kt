package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Plus: ImageVector
    get() {
        if (_Plus != null) {
            return _Plus!!
        }
        _Plus = ImageVector.Builder(
            name = "Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 4f)
                curveTo(12.5523f, 4f, 13f, 4.4477f, 13f, 5f)
                verticalLineTo(11f)
                horizontalLineTo(19f)
                curveTo(19.5523f, 11f, 20f, 11.4477f, 20f, 12f)
                curveTo(20f, 12.5523f, 19.5523f, 13f, 19f, 13f)
                horizontalLineTo(13f)
                verticalLineTo(19f)
                curveTo(13f, 19.5523f, 12.5523f, 20f, 12f, 20f)
                curveTo(11.4477f, 20f, 11f, 19.5523f, 11f, 19f)
                verticalLineTo(13f)
                horizontalLineTo(5f)
                curveTo(4.4477f, 13f, 4f, 12.5523f, 4f, 12f)
                curveTo(4f, 11.4477f, 4.4477f, 11f, 5f, 11f)
                horizontalLineTo(11f)
                verticalLineTo(5f)
                curveTo(11f, 4.4477f, 11.4477f, 4f, 12f, 4f)
                close()
            }
        }.build()

        return _Plus!!
    }

private var _Plus: ImageVector? = null
