package com.eugene.gamehelper.model

sealed interface Event {

    class ClickEvent(
        val x: Int,
        val y: Int,
        val duration: Int = 50
    ) : Event
}
