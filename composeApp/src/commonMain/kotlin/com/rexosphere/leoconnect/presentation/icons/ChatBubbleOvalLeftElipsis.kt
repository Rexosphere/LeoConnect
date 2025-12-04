package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChatBubbleOvalLeftEllipsis: ImageVector
    get() {
        if (_ChatBubbleOvalLeftEllipsis != null) return _ChatBubbleOvalLeftEllipsis!!

        _ChatBubbleOvalLeftEllipsis = ImageVector.Builder(
            name = "ChatBubbleOvalLeftEllipsis",
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
                moveTo(8.625f, 12f)
                curveTo(8.625f, 12.2071f, 8.45711f, 12.375f, 8.25f, 12.375f)
                curveTo(8.04289f, 12.375f, 7.875f, 12.2071f, 7.875f, 12f)
                curveTo(7.875f, 11.7929f, 8.04289f, 11.625f, 8.25f, 11.625f)
                curveTo(8.45711f, 11.625f, 8.625f, 11.7929f, 8.625f, 12f)
                close()
                moveTo(8.625f, 12f)
                horizontalLineTo(8.25f)
                moveTo(12.375f, 12f)
                curveTo(12.375f, 12.2071f, 12.2071f, 12.375f, 12f, 12.375f)
                curveTo(11.7929f, 12.375f, 11.625f, 12.2071f, 11.625f, 12f)
                curveTo(11.625f, 11.7929f, 11.7929f, 11.625f, 12f, 11.625f)
                curveTo(12.2071f, 11.625f, 12.375f, 11.7929f, 12.375f, 12f)
                close()
                moveTo(12.375f, 12f)
                horizontalLineTo(12f)
                moveTo(16.125f, 12f)
                curveTo(16.125f, 12.2071f, 15.9571f, 12.375f, 15.75f, 12.375f)
                curveTo(15.5429f, 12.375f, 15.375f, 12.2071f, 15.375f, 12f)
                curveTo(15.375f, 11.7929f, 15.5429f, 11.625f, 15.75f, 11.625f)
                curveTo(15.9571f, 11.625f, 16.125f, 11.7929f, 16.125f, 12f)
                close()
                moveTo(16.125f, 12f)
                horizontalLineTo(15.75f)
                moveTo(21f, 12f)
                curveTo(21f, 16.5563f, 16.9706f, 20.25f, 12f, 20.25f)
                curveTo(11.1125f, 20.25f, 10.2551f, 20.1323f, 9.44517f, 19.9129f)
                curveTo(8.47016f, 20.5979f, 7.28201f, 21f, 6f, 21f)
                curveTo(5.80078f, 21f, 5.60376f, 20.9903f, 5.40967f, 20.9713f)
                curveTo(5.25f, 20.9558f, 5.0918f, 20.9339f, 4.93579f, 20.906f)
                curveTo(5.41932f, 20.3353f, 5.76277f, 19.6427f, 5.91389f, 18.8808f)
                curveTo(6.00454f, 18.4238f, 5.7807f, 17.9799f, 5.44684f, 17.6549f)
                curveTo(3.9297f, 16.1782f, 3f, 14.1886f, 3f, 12f)
                curveTo(3f, 7.44365f, 7.02944f, 3.75f, 12f, 3.75f)
                curveTo(16.9706f, 3.75f, 21f, 7.44365f, 21f, 12f)
                close()
            }
        }.build()

        return _ChatBubbleOvalLeftEllipsis!!
    }

private var _ChatBubbleOvalLeftEllipsis: ImageVector? = null

