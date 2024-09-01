package com.eugene.gamehelper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AutoClickService : AccessibilityService() {


    //apparently this method is called every time a event occurs
    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        Log.d("MYOWNTAG", "event occurred")
    }


    public override fun onServiceConnected() {
        super.onServiceConnected()
       // autoClick(2000, 100, 950, 581)
    }

    override fun onInterrupt() {
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
