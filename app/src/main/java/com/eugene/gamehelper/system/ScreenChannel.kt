package com.eugene.gamehelper.system

import com.eugene.gamehelper.model.ScreenModel
import kotlinx.coroutines.flow.MutableSharedFlow

object ScreenChannel {

    val channel: MutableSharedFlow<ScreenModel> = MutableSharedFlow()
}
