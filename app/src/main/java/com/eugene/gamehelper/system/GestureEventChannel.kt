package com.eugene.gamehelper.system

import com.eugene.gamehelper.model.Event
import kotlinx.coroutines.flow.MutableSharedFlow

object GestureEventChannel {

    val channel: MutableSharedFlow<Event> = MutableSharedFlow()
}
