package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PaperAirplane: ImageVector
    get() {
        if (_PaperAirplane != null) return _PaperAirplane!!

        _PaperAirplane = ImageVector.Builder(
            name = "PaperAirplane",
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
                moveTo(5.99972f, 12f)
                lineTo(3.2688f, 3.12451f)
                curveTo(9.88393f, 5.04617f, 16.0276f, 8.07601f, 21.4855f, 11.9997f)
                curveTo(16.0276f, 15.9235f, 9.884f, 18.9535f, 3.26889f, 20.8752f)
                lineTo(5.99972f, 12f)
                close()
                moveTo(5.99972f, 12f)
                lineTo(13.5f, 12f)
            }
        }.build()

        return _PaperAirplane!!
    }

private var _PaperAirplane: ImageVector? = null

