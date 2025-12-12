package com.rexosphere.leoconnect.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.rexosphere.leoconnect.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberImagePicker(
    onImageSelected: (String?) -> Unit,
    onError: (String) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val imageBytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }

                    if (imageBytes == null) {
                        onError("Failed to read image")
                        return@launch
                    }

                    // Check original size
                    val sizeInMB = ImageUtils.getFileSizeInMB(imageBytes)
                    if (sizeInMB > 10) { // Reject if original is too large (>10MB)
                        onError("Image is too large. Please select an image smaller than 10MB")
                        return@launch
                    }

                    // Compress and encode
                    val base64Image = withContext(Dispatchers.Default) {
                        ImageUtils.compressAndEncodeImage(imageBytes)
                    }

                    if (base64Image != null) {
                        onImageSelected(base64Image)
                    } else {
                        onError("Failed to process image")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onError("Error: ${e.message ?: "Unknown error"}")
                }
            }
        }
    }

    return object : ImagePickerLauncher {
        override fun launch() {
            launcher.launch("image/*")
        }
    }
}
