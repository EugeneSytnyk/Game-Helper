package com.eugene.gamehelper.system

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.view.Surface
import androidx.core.app.NotificationCompat
import com.eugene.gamehelper.MainActivity
import com.eugene.gamehelper.R
import com.eugene.gamehelper.model.ScreenModel
import com.eugene.gamehelper.utils.toPixelMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ScreenCaptureService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        private const val NOTIFICATION_CHANNEL_NAME = "Screen Capture Service"
    }

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForegroundService()
        mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent)
        setupVirtualDisplay()
        return START_STICKY
    }

    private fun startForegroundService() {
        startForeground(1, createNotification())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Screen Capture")
            .setContentText("Screen capture in progress")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
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
            serviceScope.launch(Dispatchers.Default) {
                val image: Image? = reader.acquireLatestImage()
                image?.let {
                    processImage(it)
                    it.close()
                }
            }
        }, null)
    }

    private suspend fun processImage(image: Image) {
        val pixels = image.toPixelMap()
        val screenModel = ScreenModel(pixels)
        ScreenChannel.channel.emit(screenModel)
    }
}
