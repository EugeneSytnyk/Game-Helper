package com.eugene.gamehelper.system

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class ClickService : AccessibilityService() {
    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

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
