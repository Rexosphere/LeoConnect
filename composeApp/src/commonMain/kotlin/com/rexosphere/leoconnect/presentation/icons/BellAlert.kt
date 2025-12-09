package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BellAlert: ImageVector
    get() {
        if (_BellAlert != null) return _BellAlert!!
        
        _BellAlert = ImageVector.Builder(
            name = "BellAlert",
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
                moveTo(14.8569f, 17.0817f)
                curveTo(16.7514f, 16.857f, 18.5783f, 16.4116f, 20.3111f, 15.7719f)
                curveTo(18.8743f, 14.177f, 17.9998f, 12.0656f, 17.9998f, 9.75f)
                verticalLineTo(9.04919f)
                curveTo(17.9999f, 9.03281f, 18f, 9.01641f, 18f, 9f)
                curveTo(18f, 5.68629f, 15.3137f, 3f, 12f, 3f)
                curveTo(8.6863f, 3f, 6.00001f, 5.68629f, 6.00001f, 9f)
                lineTo(5.99982f, 9.75f)
                curveTo(5.99982f, 12.0656f, 5.12529f, 14.177f, 3.68849f, 15.7719f)
                curveTo(5.42142f, 16.4116f, 7.24845f, 16.857f, 9.14315f, 17.0818f)
                moveTo(14.8569f, 17.0817f)
                curveTo(13.92f, 17.1928f, 12.9666f, 17.25f, 11.9998f, 17.25f)
                curveTo(11.0332f, 17.25f, 10.0799f, 17.1929f, 9.14315f, 17.0818f)
                moveTo(14.8569f, 17.0817f)
                curveTo(14.9498f, 17.3711f, 15f, 17.6797f, 15f, 18f)
                curveTo(15f, 19.6569f, 13.6569f, 21f, 12f, 21f)
                curveTo(10.3432f, 21f, 9.00001f, 19.6569f, 9.00001f, 18f)
                curveTo(9.00001f, 17.6797f, 9.0502f, 17.3712f, 9.14315f, 17.0818f)
                moveTo(3.12445f, 7.5f)
                curveTo(3.41173f, 5.78764f, 4.18254f, 4.23924f, 5.29169f, 3f)
                moveTo(18.7083f, 3f)
                curveTo(19.8175f, 4.23924f, 20.5883f, 5.78764f, 20.8756f, 7.5f)
            }
        }.build()
        
        return _BellAlert!!
    }

private var _BellAlert: ImageVector? = null

