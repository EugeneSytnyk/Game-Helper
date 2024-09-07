package com.eugene.gamehelper.system

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.eugene.gamehelper.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GestureService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            GestureEventChannel.channel.collect {
                processEvent(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun processEvent(event: Event) {
        when (event) {
            is Event.ClickEvent -> {
                click(0, event.duration, event.x, event.y)
            }
        }
    }

    private fun click(startTimeMs: Int, durationMs: Int, x: Int, y: Int) {
        dispatchGesture(gestureDescription(startTimeMs, durationMs, x, y), null, null)
    }

    private fun gestureDescription(startTimeMs: Int, durationMs: Int, x: Int, y: Int): GestureDescription {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        return createGestureDescription(
            StrokeDescription(
                path,
                startTimeMs.toLong(),
                durationMs.toLong()
            )
        )
    }

    private fun createGestureDescription(vararg strokes: StrokeDescription?): GestureDescription {
        return GestureDescription.Builder().apply {
            for (stroke in strokes) {
                addStroke(stroke!!)
            }
        }.build()
    }
}
