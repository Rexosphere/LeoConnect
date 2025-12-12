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
import platform.Foundation.NSData
import platform.Foundation.NSDataBase64DecodingIgnoreUnknownCharacters
import platform.Foundation.create

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
            val bitmap = withContext(Dispatchers.Default) {
                // Decode base64 string to NSData
                val nsData = NSData.create(base64EncodedString = base64String, options = NSDataBase64DecodingIgnoreUnknownCharacters)
                    ?: throw IllegalArgumentException("Failed to decode base64 string")
                
                // Convert NSData to ByteArray
                val bytes = ByteArray(nsData.length.toInt())
                nsData.getBytes(bytes.refTo(0), nsData.length)
                
                // Convert to Skia Image and then to ImageBitmap
                SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
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
