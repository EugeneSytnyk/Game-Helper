package com.eugene.gamehelper.game

import com.eugene.gamehelper.model.ScreenModel

interface IGame {

    val outputInteractionHandler: IOutputInteractionHandler

    suspend fun start()

    suspend fun processScreenModel(screenModel: ScreenModel)
}
