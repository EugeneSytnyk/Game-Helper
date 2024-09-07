package com.eugene.gamehelper.model

class ScreenModel(
    private val pixels: Array<IntArray>
) {

    fun getPixel(x: Int, y: Int): Int {
        return pixels[y][x]
    }
}
