package com.eugene.gamehelper.model

class ScreenModel(
    private val pixels: Array<IntArray>
) {
    var topOffset: Int = 0

    var bottomOffset: Int = 0

    var leftOffset: Int = 0

    var rightOffset: Int = 0

    val height
        get() = (pixels.size - bottomOffset - topOffset).coerceAtLeast(0)

    val width
        get() = pixels.firstOrNull()?.size?.minus(leftOffset)?.minus(rightOffset)?.coerceAtLeast(0) ?: 0

    fun getPixel(x: Int, y: Int): Int {
        return pixels[y + bottomOffset][x + leftOffset]
    }
}
