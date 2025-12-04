package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChevronLeft: ImageVector
    get() {
        if (_ChevronLeft != null) return _ChevronLeft!!

        _ChevronLeft = ImageVector.Builder(
            name = "ChevronLeft",
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
                moveTo(15.75f, 19.5f)
                lineTo(8.25f, 12f)
                lineTo(15.75f, 4.5f)
            }
        }.build()

        return _ChevronLeft!!
    }

private var _ChevronLeft: ImageVector? = null

