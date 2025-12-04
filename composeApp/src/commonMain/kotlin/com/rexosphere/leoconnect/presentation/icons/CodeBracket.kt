package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CodeBracket: ImageVector
    get() {
        if (_CodeBracket != null) return _CodeBracket!!

        _CodeBracket = ImageVector.Builder(
            name = "CodeBracket",
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
                moveTo(17.25f, 6.75f)
                lineTo(22.5f, 12f)
                lineTo(17.25f, 17.25f)
                moveTo(6.75f, 17.25f)
                lineTo(1.5f, 12f)
                lineTo(6.75f, 6.75f)
                moveTo(14.25f, 3.75f)
                lineTo(9.75f, 20.25f)
            }
        }.build()

        return _CodeBracket!!
    }

private var _CodeBracket: ImageVector? = null

