package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CalendarDays: ImageVector
    get() {
        if (_CalendarDays != null) return _CalendarDays!!

        _CalendarDays = ImageVector.Builder(
            name = "CalendarDays",
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
                moveTo(6.75f, 3f)
                verticalLineTo(5.25f)
                moveTo(17.25f, 3f)
                verticalLineTo(5.25f)
                moveTo(3f, 18.75f)
                verticalLineTo(7.5f)
                curveTo(3f, 6.25736f, 4.00736f, 5.25f, 5.25f, 5.25f)
                horizontalLineTo(18.75f)
                curveTo(19.9926f, 5.25f, 21f, 6.25736f, 21f, 7.5f)
                verticalLineTo(18.75f)
                moveTo(3f, 18.75f)
                curveTo(3f, 19.9926f, 4.00736f, 21f, 5.25f, 21f)
                horizontalLineTo(18.75f)
                curveTo(19.9926f, 21f, 21f, 19.9926f, 21f, 18.75f)
                moveTo(3f, 18.75f)
                verticalLineTo(11.25f)
                curveTo(3f, 10.0074f, 4.00736f, 9f, 5.25f, 9f)
                horizontalLineTo(18.75f)
                curveTo(19.9926f, 9f, 21f, 10.0074f, 21f, 11.25f)
                verticalLineTo(18.75f)
                moveTo(12f, 12.75f)
                horizontalLineTo(12.0075f)
                verticalLineTo(12.7575f)
                horizontalLineTo(12f)
                verticalLineTo(12.75f)
                close()
                moveTo(12f, 15f)
                horizontalLineTo(12.0075f)
                verticalLineTo(15.0075f)
                horizontalLineTo(12f)
                verticalLineTo(15f)
                close()
                moveTo(12f, 17.25f)
                horizontalLineTo(12.0075f)
                verticalLineTo(17.2575f)
                horizontalLineTo(12f)
                verticalLineTo(17.25f)
                close()
                moveTo(9.75f, 15f)
                horizontalLineTo(9.7575f)
                verticalLineTo(15.0075f)
                horizontalLineTo(9.75f)
                verticalLineTo(15f)
                close()
                moveTo(9.75f, 17.25f)
                horizontalLineTo(9.7575f)
                verticalLineTo(17.2575f)
                horizontalLineTo(9.75f)
                verticalLineTo(17.25f)
                close()
                moveTo(7.5f, 15f)
                horizontalLineTo(7.5075f)
                verticalLineTo(15.0075f)
                horizontalLineTo(7.5f)
                verticalLineTo(15f)
                close()
                moveTo(7.5f, 17.25f)
                horizontalLineTo(7.5075f)
                verticalLineTo(17.2575f)
                horizontalLineTo(7.5f)
                verticalLineTo(17.25f)
                close()
                moveTo(14.25f, 12.75f)
                horizontalLineTo(14.2575f)
                verticalLineTo(12.7575f)
                horizontalLineTo(14.25f)
                verticalLineTo(12.75f)
                close()
                moveTo(14.25f, 15f)
                horizontalLineTo(14.2575f)
                verticalLineTo(15.0075f)
                horizontalLineTo(14.25f)
                verticalLineTo(15f)
                close()
                moveTo(14.25f, 17.25f)
                horizontalLineTo(14.2575f)
                verticalLineTo(17.2575f)
                horizontalLineTo(14.25f)
                verticalLineTo(17.25f)
                close()
                moveTo(16.5f, 12.75f)
                horizontalLineTo(16.5075f)
                verticalLineTo(12.7575f)
                horizontalLineTo(16.5f)
                verticalLineTo(12.75f)
                close()
                moveTo(16.5f, 15f)
                horizontalLineTo(16.5075f)
                verticalLineTo(15.0075f)
                horizontalLineTo(16.5f)
                verticalLineTo(15f)
                close()
            }
        }.build()

        return _CalendarDays!!
    }

private var _CalendarDays: ImageVector? = null

