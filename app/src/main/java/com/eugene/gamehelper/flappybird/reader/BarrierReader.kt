package com.eugene.gamehelper.flappybird.reader

import com.eugene.gamehelper.flappybird.model.BarrierModel
import com.eugene.gamehelper.model.ScreenModel
import com.eugene.gamehelper.utils.redChannel

class BarrierReader {

    companion object {
        private const val BARRIER_RED_THRESHOLD = 50
        private const val TOP_BARRIER_RED_THRESHOLD = 70

        private const val SPACE_HEIGHT = 65
    }

    var barriers: List<BarrierModel> = emptyList()

    fun processScreenModel(screenModel: ScreenModel) {
        var isAddingBarrier = false
        val localBarriers = mutableListOf<Pair<Int, Int>>()
        var barrierStart = -1
        for (x in 0 until screenModel.width) {
            val isBarrierPart = screenModel.getPixel(x, 0).redChannel() >= BARRIER_RED_THRESHOLD
            if (barrierStart == -1 && isBarrierPart) {
                barrierStart = x
            } else if (barrierStart != -1 && !isBarrierPart) {
                localBarriers += barrierStart to x
                barrierStart = -1
            }
        }
        if (barrierStart != -1) {
            if (barriers.size == localBarriers.size) {
                isAddingBarrier = true
            }
            localBarriers += barrierStart to screenModel.width - 1
        }
        val newList = if (isAddingBarrier) {
            val oldSublist = barriers.mapIndexed { index, model ->
                model.copy(leftX = localBarriers[index].first, rightX = localBarriers[index].second)
            }
            val lastItem = localBarriers.last()
            val bottomY = readBottomY(screenModel)
            oldSublist + BarrierModel(lastItem.first, lastItem.second, bottomY, bottomY + SPACE_HEIGHT)
        } else {
            val difference = barriers.size - localBarriers.size
            localBarriers.mapIndexed { index, data ->
                barriers[index + difference].copy(leftX = data.first, rightX = data.second)
            }
        }
        barriers = newList
    }

    private fun readBottomY(screenModel: ScreenModel): Int {
        var result = 0
        for(y in 0 until screenModel.height) {
            if (screenModel.getPixel(screenModel.width - 1, y).redChannel() <= TOP_BARRIER_RED_THRESHOLD) {
                result = y
                break
            }
        }
        return result
    }
}
