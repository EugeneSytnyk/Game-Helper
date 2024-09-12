package com.eugene.gamehelper.game

import com.eugene.gamehelper.model.Event

interface IOutputInteractionHandler {

    fun handle(event: Event)
}
