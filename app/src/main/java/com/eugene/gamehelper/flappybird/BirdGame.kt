package com.eugene.gamehelper.flappybird

import android.util.Log
import com.eugene.gamehelper.flappybird.reader.BarrierReader
import com.eugene.gamehelper.flappybird.reader.BirdPositionReader
import com.eugene.gamehelper.game.IGame
import com.eugene.gamehelper.game.IOutputInteractionHandler
import com.eugene.gamehelper.model.Event
import com.eugene.gamehelper.model.ScreenModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BirdGame(
    override val outputInteractionHandler: IOutputInteractionHandler
) : IGame {

    init {
        instace = this
    }

    companion object {
        var instace: BirdGame? = null
    }

    private val birdPositionReader = BirdPositionReader()
    private val barrierReader = BarrierReader()

    private var nextEvent: Event? = null
    private var lastEventTime = 0

    private var lastJob: Job? = null

    private var isGameStarted = false

    override suspend fun start() {
        outputInteractionHandler.handle(Event.ClickEvent(400, 400))
        isGameStarted = true
//        while (true) {
//            delay(100)
//            Log.d("MYOWNTAG", "perform $nextEvent")
//            nextEvent?.let { outputInteractionHandler.handle(it) }
//            nextEvent = null
//        }
    }

    override suspend fun processScreenModel(screenModel: ScreenModel) {
        if (isGameStarted.not()) return
        birdPositionReader.processScreenModel(screenModel)
        barrierReader.processScreenModel(screenModel)
        decide()
    }

    private suspend fun decide() {
        val nextBarrier = barrierReader.barriers.firstOrNull {
            it.rightX >= 35
        } ?: return
        Log.d("MYOWNTAG", "check bird: ${birdPositionReader.birdY}, position: ${nextBarrier.bottomY + 15}")
        if (birdPositionReader.birdY < nextBarrier.bottomY + 20 && birdPositionReader.isFlyingUp().not()) {
            Log.d("MYOWNTAG", "set $nextEvent")
            val timeNow = System.currentTimeMillis()
            if (timeNow - lastEventTime > 150) {
                lastJob?.cancel()
                outputInteractionHandler.handle(Event.ClickEvent(400, 400))
            } else {
                coroutineScope {
                    lastJob = launch {
                        delay(timeNow - lastEventTime)
                        outputInteractionHandler.handle(Event.ClickEvent(400, 400))
                        lastJob = null
                    }
                }
            }
        }
    }
}
