package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MagnifyingGlass: ImageVector
    get() {
        if (_MagnifyingGlass != null) return _MagnifyingGlass!!

        _MagnifyingGlass = ImageVector.Builder(
            name = "MagnifyingGlass",
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
                moveTo(21f, 21f)
                lineTo(15.8033f, 15.8033f)
                moveTo(15.8033f, 15.8033f)
                curveTo(17.1605f, 14.4461f, 18f, 12.5711f, 18f, 10.5f)
                curveTo(18f, 6.35786f, 14.6421f, 3f, 10.5f, 3f)
                curveTo(6.35786f, 3f, 3f, 6.35786f, 3f, 10.5f)
                curveTo(3f, 14.6421f, 6.35786f, 18f, 10.5f, 18f)
                curveTo(12.5711f, 18f, 14.4461f, 17.1605f, 15.8033f, 15.8033f)
                close()
            }
        }.build()

        return _MagnifyingGlass!!
    }

private var _MagnifyingGlass: ImageVector? = null

