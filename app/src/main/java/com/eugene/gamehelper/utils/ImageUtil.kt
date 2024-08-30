package com.eugene.gamehelper.utils

import android.graphics.PixelFormat
import android.media.Image

fun convertImageTo2DArray(image: Image): Array<IntArray> {
    // Ensure the image format is RGBA_8888
    if (image.format != PixelFormat.RGBA_8888) {
        throw IllegalArgumentException("Image format must be PixelFormat.RGBA_8888")
    }

    // Get image dimensions
    val width = image.width
    val height = image.height

    // Get the byte buffer from the image
    val planes = image.planes
    val buffer = planes[0].buffer

    // Ensure the buffer has enough data
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride

    // Allocate a 2D array to store the pixel data
    val pixelArray = Array(height) { IntArray(width) }

    // Iterate over the image data to fill the 2D array
    for (y in 0 until height) {
        for (x in 0 until width) {
            // Calculate the position of the pixel in the buffer
            val offset = (y * rowStride) + (x * pixelStride)

            // Read RGBA values
            val r = buffer.get(offset).toInt() and 0xFF
            val g = buffer.get(offset + 1).toInt() and 0xFF
            val b = buffer.get(offset + 2).toInt() and 0xFF
            val a = buffer.get(offset + 3).toInt() and 0xFF

            // Combine RGBA values into a single integer
            val color = (a shl 24) or (r shl 16) or (g shl 8) or b
            pixelArray[y][x] = color
        }
    }

    return pixelArray
}

fun intTo4ByteHexString(value: Int): String {
    return String.format(
        "%02X%02X%02X%02X",
        (value shr 24) and 0xFF,
        (value shr 16) and 0xFF,
        (value shr 8) and 0xFF,
        value and 0xFF
    )
}
