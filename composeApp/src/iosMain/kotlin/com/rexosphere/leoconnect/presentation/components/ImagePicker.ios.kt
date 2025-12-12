package com.rexosphere.leoconnect.presentation.components

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(
    onImageSelected: (String?) -> Unit,
    onError: (String) -> Unit
): ImagePickerLauncher {
    return object : ImagePickerLauncher {
        override fun launch() {
            onError("Image picker not implemented for iOS yet")
        }
    }
}
