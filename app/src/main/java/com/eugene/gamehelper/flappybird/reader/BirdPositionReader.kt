package com.eugene.gamehelper.flappybird.reader

import android.util.Log
import com.eugene.gamehelper.model.ScreenModel
import com.eugene.gamehelper.utils.blueChannel

class BirdPositionReader {

    companion object {
        private const val BIRD_COLOR_BLUE_THRESHOLD = 105
    }

    private var oldBirdY = 0
    var birdY = 0

    val birdX = 43

    fun processScreenModel(screenModel: ScreenModel) {
        var bottomBirdPosition = -1
        for (i in 0 until screenModel.height) {
            val blueChannel = screenModel.getPixel(birdX, i).blueChannel()
            if (bottomBirdPosition == -1 && blueChannel <= BIRD_COLOR_BLUE_THRESHOLD) {
                bottomBirdPosition = i
            } else if (bottomBirdPosition != -1 && blueChannel > BIRD_COLOR_BLUE_THRESHOLD ){
                oldBirdY = birdY
                birdY = (bottomBirdPosition + i) / 2
                break
            }
        }
        if (birdY != oldBirdY) Log.d("BirdPositionReader", "New Y: $birdY")
    }

    fun isFlyingUp(): Boolean = birdY - oldBirdY > 0
}
