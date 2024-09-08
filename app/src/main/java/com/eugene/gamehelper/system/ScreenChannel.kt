package com.eugene.gamehelper.system

import com.eugene.gamehelper.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow

object ScreenChannel {

    val channel: MutableStateFlow<ScreenModel> = MutableStateFlow(ScreenModel(emptyArray()))
}
