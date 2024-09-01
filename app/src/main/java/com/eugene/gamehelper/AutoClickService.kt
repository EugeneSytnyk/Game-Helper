package com.eugene.gamehelper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AutoClickService : AccessibilityService() {
    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onCreate() {
        super.onCreate()
        Log.d("MYOWNTAG", "onCreate")
        click()
    }

    private fun click() {
        Handler(Looper.getMainLooper()).postDelayed({
            autoClick(0, 100, 500, 500)
            Log.d("MYOWNTAG", "clicked")
            click()
        }, 5000)
    }

    private fun autoClick(startTimeMs: Int, durationMs: Int, x: Int, y: Int) {
        val isCalled =
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
        val builder = GestureDescription.Builder()
        for (stroke in strokes) {
            builder.addStroke(stroke!!)
        }
        return builder.build()
    }
}
