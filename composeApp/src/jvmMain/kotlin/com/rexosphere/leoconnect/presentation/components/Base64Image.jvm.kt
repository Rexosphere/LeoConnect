package com.rexosphere.leoconnect.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage
import java.util.Base64

@Composable
actual fun Base64Image(
    base64String: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    var imageBitmap by remember(base64String) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(base64String) { mutableStateOf(true) }
    var error by remember(base64String) { mutableStateOf<String?>(null) }

    LaunchedEffect(base64String) {
        isLoading = true
        error = null
        try {
            val bitmap = withContext(Dispatchers.IO) {
                // Decode base64 string to byte array
                val imageBytes = Base64.getDecoder().decode(base64String)
                // Convert to Skia Image and then to ImageBitmap
                SkiaImage.makeFromEncoded(imageBytes).toComposeImageBitmap()
            }
            
            imageBitmap = bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            error = "Error loading image: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text("Failed to load image")
            }
            imageBitmap != null -> {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        }
    }
}
