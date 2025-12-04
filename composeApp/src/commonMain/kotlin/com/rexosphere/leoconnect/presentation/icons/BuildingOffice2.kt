package com.rexosphere.leoconnect.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BuildingOffice2: ImageVector
    get() {
        if (_BuildingOffice2 != null) return _BuildingOffice2!!

        _BuildingOffice2 = ImageVector.Builder(
            name = "BuildingOffice2",
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
                moveTo(2.25f, 21f)
                horizontalLineTo(21.75f)
                moveTo(3.75f, 3f)
                verticalLineTo(21f)
                moveTo(14.25f, 3f)
                verticalLineTo(21f)
                moveTo(20.25f, 7.5f)
                verticalLineTo(21f)
                moveTo(6.75f, 6.75f)
                horizontalLineTo(7.5f)
                moveTo(6.75f, 9.75f)
                horizontalLineTo(7.5f)
                moveTo(6.75f, 12.75f)
                horizontalLineTo(7.5f)
                moveTo(10.5f, 6.75f)
                horizontalLineTo(11.25f)
                moveTo(10.5f, 9.75f)
                horizontalLineTo(11.25f)
                moveTo(10.5f, 12.75f)
                horizontalLineTo(11.25f)
                moveTo(6.75f, 21f)
                verticalLineTo(17.625f)
                curveTo(6.75f, 17.0037f, 7.25368f, 16.5f, 7.875f, 16.5f)
                horizontalLineTo(10.125f)
                curveTo(10.7463f, 16.5f, 11.25f, 17.0037f, 11.25f, 17.625f)
                verticalLineTo(21f)
                moveTo(3f, 3f)
                horizontalLineTo(15f)
                moveTo(14.25f, 7.5f)
                horizontalLineTo(21f)
                moveTo(17.25f, 11.25f)
                horizontalLineTo(17.2575f)
                verticalLineTo(11.2575f)
                horizontalLineTo(17.25f)
                verticalLineTo(11.25f)
                close()
                moveTo(17.25f, 14.25f)
                horizontalLineTo(17.2575f)
                verticalLineTo(14.2575f)
                horizontalLineTo(17.25f)
                verticalLineTo(14.25f)
                close()
                moveTo(17.25f, 17.25f)
                horizontalLineTo(17.2575f)
                verticalLineTo(17.2575f)
                horizontalLineTo(17.25f)
                verticalLineTo(17.25f)
                close()
            }
        }.build()

        return _BuildingOffice2!!
    }

private var _BuildingOffice2: ImageVector? = null

