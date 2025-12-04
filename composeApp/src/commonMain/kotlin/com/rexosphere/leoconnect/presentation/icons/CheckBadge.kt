package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CheckBadge: ImageVector
    get() {
        if (_CheckBadge != null) return _CheckBadge!!

        _CheckBadge = ImageVector.Builder(
            name = "CheckBadge",
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
                moveTo(9f, 12.75f)
                lineTo(11.25f, 15f)
                lineTo(15f, 9.75f)
                moveTo(21f, 12f)
                curveTo(21f, 13.2683f, 20.3704f, 14.3895f, 19.4067f, 15.0682f)
                curveTo(19.6081f, 16.2294f, 19.2604f, 17.4672f, 18.3637f, 18.3639f)
                curveTo(17.467f, 19.2606f, 16.2292f, 19.6083f, 15.068f, 19.4069f)
                curveTo(14.3893f, 20.3705f, 13.2682f, 21f, 12f, 21f)
                curveTo(10.7319f, 21f, 9.61072f, 20.3705f, 8.93204f, 19.407f)
                curveTo(7.77066f, 19.6086f, 6.53256f, 19.261f, 5.6357f, 18.3641f)
                curveTo(4.73886f, 17.4673f, 4.39125f, 16.2292f, 4.59286f, 15.0678f)
                curveTo(3.62941f, 14.3891f, 3f, 13.2681f, 3f, 12f)
                curveTo(3f, 10.7319f, 3.62946f, 9.61077f, 4.59298f, 8.93208f)
                curveTo(4.39147f, 7.77079f, 4.7391f, 6.53284f, 5.63587f, 5.63607f)
                curveTo(6.53265f, 4.73929f, 7.77063f, 4.39166f, 8.93194f, 4.59319f)
                curveTo(9.61061f, 3.62955f, 10.7318f, 3f, 12f, 3f)
                curveTo(13.2682f, 3f, 14.3893f, 3.6295f, 15.068f, 4.59307f)
                curveTo(16.2294f, 4.39145f, 17.4674f, 4.73906f, 18.3643f, 5.6359f)
                curveTo(19.2611f, 6.53274f, 19.6087f, 7.77081f, 19.4071f, 8.93218f)
                curveTo(20.3706f, 9.61087f, 21f, 10.7319f, 21f, 12f)
                close()
            }
        }.build()

        return _CheckBadge!!
    }

private var _CheckBadge: ImageVector? = null

