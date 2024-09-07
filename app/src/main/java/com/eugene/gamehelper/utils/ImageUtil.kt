package com.eugene.gamehelper.utils

import android.graphics.PixelFormat
import android.media.Image

fun Image.toPixelMap(): Array<IntArray> {
    if (this.format != PixelFormat.RGBA_8888) {
        error("Image format must be PixelFormat.RGBA_8888")
    }

    val width = this.width
    val height = this.height

    val planes = this.planes
    val buffer = planes[0].buffer

    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride

    val pixelArray = Array(height) { IntArray(width) }

    for (y in 0 until height) {
        for (x in 0 until width) {
            val offset = (y * rowStride) + (x * pixelStride)

            val r = buffer.get(offset).toInt() and 0xFF
            val g = buffer.get(offset + 1).toInt() and 0xFF
            val b = buffer.get(offset + 2).toInt() and 0xFF
            val a = buffer.get(offset + 3).toInt() and 0xFF

            val color = (a shl 24) or (r shl 16) or (g shl 8) or b
            pixelArray[y][x] = color
        }
    }

    return pixelArray
}

fun Int.toByteHexString(): String {
    return String.format(
        "%02X%02X%02X%02X",
        (this shr 24) and 0xFF,
        (this shr 16) and 0xFF,
        (this shr 8) and 0xFF,
        this and 0xFF
    )
}
