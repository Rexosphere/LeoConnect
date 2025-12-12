package com.rexosphere.leoconnect.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

object ImageUtils {
    private const val MAX_SIZE_BYTES = 2 * 1024 * 1024 // 2MB

    /**
     * Compress and encode image to Base64 with size limit
     * @param imageBytes Original image bytes
     * @return Base64 encoded string or null if compression fails
     */
    fun compressAndEncodeImage(imageBytes: ByteArray): String? {
        try {
            var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: return null

            // Calculate initial compression quality
            var quality = 90
            var compressedBytes: ByteArray

            do {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                compressedBytes = outputStream.toByteArray()

                // If still too large and quality can be reduced, try again
                if (compressedBytes.size > MAX_SIZE_BYTES && quality > 10) {
                    quality -= 10
                } else if (compressedBytes.size > MAX_SIZE_BYTES) {
                    // If quality is already low, resize the bitmap
                    val scaleFactor = sqrt(MAX_SIZE_BYTES.toDouble() / compressedBytes.size)
                    val newWidth = (bitmap.width * scaleFactor).toInt()
                    val newHeight = (bitmap.height * scaleFactor).toInt()
                    bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                    quality = 90 // Reset quality after resizing
                } else {
                    break
                }
            } while (compressedBytes.size > MAX_SIZE_BYTES)

            return Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Get file size in MB
     */
    fun getFileSizeInMB(bytes: ByteArray): Double {
        return bytes.size / (1024.0 * 1024.0)
    }
}
