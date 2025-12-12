package com.rexosphere.leoconnect.presentation.components

import androidx.compose.runtime.Composable

interface ImagePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberImagePicker(
    onImageSelected: (String?) -> Unit,
    onError: (String) -> Unit = {}
): ImagePickerLauncher
