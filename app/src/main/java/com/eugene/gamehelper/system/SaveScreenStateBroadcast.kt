package com.eugene.gamehelper.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eugene.gamehelper.utils.saveImageFromPixelArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// adb shell am broadcast -a com.eugene.gamehelper.system.SAVE_SCREEN_STATE

class SaveScreenStateBroadcast : BroadcastReceiver() {

    companion object {
        private const val ACTION = "com.eugene.gamehelper.system.SAVE_SCREEN_STATE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == ACTION) {
            CoroutineScope(Dispatchers.IO).apply {
                launch {
                    val timeStamp = System.currentTimeMillis().toString()
                    context.saveImageFromPixelArray(ScreenChannel.channel.value.pixels, timeStamp)
                }
            }
        }
    }
}
