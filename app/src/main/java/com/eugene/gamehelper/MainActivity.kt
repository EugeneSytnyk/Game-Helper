package com.eugene.gamehelper

import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eugene.gamehelper.utils.convertImageTo2DArray
import com.eugene.gamehelper.utils.intTo4ByteHexString


class MainActivity : AppCompatActivity() {
    companion object {
        private const val SCREEN_CAPTURE_REQUEST_CODE = 100
    }

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        checkAccessibilityServicePermission()
//        mediaProjectionManager =
//            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
//        startScreenCapture()
    }

    private fun startScreenCapture() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, SCREEN_CAPTURE_REQUEST_CODE)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCREEN_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val serviceIntent = Intent(this, ScreenCaptureService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)

            Handler(Looper.getMainLooper()).postDelayed({
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
                setupVirtualDisplay()
            }, 3000)
        }
    }

    private fun setupVirtualDisplay() {
        val metrics = resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val screenDensity = metrics.densityDpi

        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        val surface: Surface = imageReader!!.surface

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            screenWidth, screenHeight, screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface, null, null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image: Image? = reader.acquireLatestImage()
            image?.let {
                processImage(it)
                it.close()
            }
        }, null)
    }

    private fun processImage(image: Image) {
        val pixels = convertImageTo2DArray(image)
        val yCenter = pixels.size / 2
        val xCenter = pixels[0].size / 2

        val pixel = pixels[yCenter][xCenter]

        val leftTop = pixels[pixels.size - 1][200]
        val leftBottom = pixels[pixels.size - 1][pixels[0].size - 1]
        val rightTop = pixels[0][0]
        val rightBottom = pixels[0][pixels[0].size - 1]

        Log.d("MYOWNTAG", intTo4ByteHexString(leftTop) + " " + intTo4ByteHexString(leftBottom) + " " + intTo4ByteHexString(rightTop) + " " + intTo4ByteHexString(rightBottom))

        // You could convert this data to a bitmap, apply image processing, stream it, etc.
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()

        stopService(Intent(this, ScreenCaptureService::class.java))
    }

    fun checkAccessibilityServicePermission() {
        var access = 0
        try {
            access = Settings.Secure.getInt(
                this.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            //put a Toast
        }
        if (access == 0) {
            val myIntent: Intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(myIntent)
        }
    }
}