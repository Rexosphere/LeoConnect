package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val UserGroup: ImageVector
    get() {
        if (_UserGroup != null) return _UserGroup!!

        _UserGroup = ImageVector.Builder(
            name = "UserGroup",
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
                moveTo(17.9999f, 18.7191f)
                curveTo(18.2474f, 18.7396f, 18.4978f, 18.75f, 18.7506f, 18.75f)
                curveTo(19.7989f, 18.75f, 20.8054f, 18.5708f, 21.741f, 18.2413f)
                curveTo(21.7473f, 18.1617f, 21.7506f, 18.0812f, 21.7506f, 18f)
                curveTo(21.7506f, 16.3431f, 20.4074f, 15f, 18.7506f, 15f)
                curveTo(18.123f, 15f, 17.5403f, 15.1927f, 17.0587f, 15.5222f)
                moveTo(17.9999f, 18.7191f)
                curveTo(18f, 18.7294f, 18f, 18.7397f, 18f, 18.75f)
                curveTo(18f, 18.975f, 17.9876f, 19.1971f, 17.9635f, 19.4156f)
                curveTo(16.2067f, 20.4237f, 14.1707f, 21f, 12f, 21f)
                curveTo(9.82933f, 21f, 7.79327f, 20.4237f, 6.03651f, 19.4156f)
                curveTo(6.01238f, 19.1971f, 6f, 18.975f, 6f, 18.75f)
                curveTo(6f, 18.7397f, 6.00003f, 18.7295f, 6.00008f, 18.7192f)
                moveTo(17.9999f, 18.7191f)
                curveTo(17.994f, 17.5426f, 17.6494f, 16.4461f, 17.0587f, 15.5222f)
                moveTo(17.0587f, 15.5222f)
                curveTo(15.9928f, 13.8552f, 14.1255f, 12.75f, 12f, 12.75f)
                curveTo(9.87479f, 12.75f, 8.00765f, 13.8549f, 6.94169f, 15.5216f)
                moveTo(6.94169f, 15.5216f)
                curveTo(6.46023f, 15.1925f, 5.87796f, 15f, 5.25073f, 15f)
                curveTo(3.59388f, 15f, 2.25073f, 16.3431f, 2.25073f, 18f)
                curveTo(2.25073f, 18.0812f, 2.25396f, 18.1617f, 2.26029f, 18.2413f)
                curveTo(3.19593f, 18.5708f, 4.2024f, 18.75f, 5.25073f, 18.75f)
                curveTo(5.50307f, 18.75f, 5.75299f, 18.7396f, 6.00008f, 18.7192f)
                moveTo(6.94169f, 15.5216f)
                curveTo(6.35071f, 16.4457f, 6.00598f, 17.5424f, 6.00008f, 18.7192f)
                moveTo(15f, 6.75f)
                curveTo(15f, 8.40685f, 13.6569f, 9.75f, 12f, 9.75f)
                curveTo(10.3431f, 9.75f, 9f, 8.40685f, 9f, 6.75f)
                curveTo(9f, 5.09315f, 10.3431f, 3.75f, 12f, 3.75f)
                curveTo(13.6569f, 3.75f, 15f, 5.09315f, 15f, 6.75f)
                close()
                moveTo(21f, 9.75f)
                curveTo(21f, 10.9926f, 19.9926f, 12f, 18.75f, 12f)
                curveTo(17.5074f, 12f, 16.5f, 10.9926f, 16.5f, 9.75f)
                curveTo(16.5f, 8.50736f, 17.5074f, 7.5f, 18.75f, 7.5f)
                curveTo(19.9926f, 7.5f, 21f, 8.50736f, 21f, 9.75f)
                close()
                moveTo(7.5f, 9.75f)
                curveTo(7.5f, 10.9926f, 6.49264f, 12f, 5.25f, 12f)
                curveTo(4.00736f, 12f, 3f, 10.9926f, 3f, 9.75f)
                curveTo(3f, 8.50736f, 4.00736f, 7.5f, 5.25f, 7.5f)
                curveTo(6.49264f, 7.5f, 7.5f, 8.50736f, 7.5f, 9.75f)
                close()
            }
        }.build()

        return _UserGroup!!
    }

private var _UserGroup: ImageVector? = null

