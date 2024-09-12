package com.eugene.gamehelper.flappybird

import com.eugene.gamehelper.flappybird.reader.BirdPositionReader
import com.eugene.gamehelper.game.IGame
import com.eugene.gamehelper.game.IOutputInteractionHandler
import com.eugene.gamehelper.model.ScreenModel

class BirdGame(
    override val outputInteractionHandler: IOutputInteractionHandler
) : IGame {

    private val birdPositionReader = BirdPositionReader()

    override suspend fun start() {

    }

    override suspend fun processScreenModel(screenModel: ScreenModel) {
        birdPositionReader.processScreenModel(screenModel)
    }
}
