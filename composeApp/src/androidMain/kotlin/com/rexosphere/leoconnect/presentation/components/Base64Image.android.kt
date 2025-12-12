package com.rexosphere.leoconnect.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun Base64Image(
    base64String: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    var imageBitmap by remember(base64String) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var isLoading by remember(base64String) { mutableStateOf(true) }
    var error by remember(base64String) { mutableStateOf<String?>(null) }

    LaunchedEffect(base64String) {
        isLoading = true
        error = null
        try {
            val bitmap = withContext(Dispatchers.IO) {
                // Decode base64 string to byte array
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                // Convert bytes to Bitmap
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }
            
            if (bitmap != null) {
                imageBitmap = bitmap.asImageBitmap()
            } else {
                error = "Failed to decode image"
            }
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
