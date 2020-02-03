package com.harman.vsimakov2.CAPP.smartDeviceLink

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.harman.vsimakov2.CAPP.R
import com.smartdevicelink.managers.SdlManager
import com.smartdevicelink.managers.SdlManagerListener
import com.smartdevicelink.managers.file.filetypes.SdlArtwork
import com.smartdevicelink.managers.lifecycle.LifecycleConfigurationUpdate
import com.smartdevicelink.protocol.enums.FunctionID
import com.smartdevicelink.proxy.RPCNotification
import com.smartdevicelink.proxy.rpc.OnHMIStatus
import com.smartdevicelink.proxy.rpc.enums.AppHMIType
import com.smartdevicelink.proxy.rpc.enums.FileType
import com.smartdevicelink.proxy.rpc.enums.Language
import com.smartdevicelink.proxy.rpc.listeners.OnRPCNotificationListener
import com.smartdevicelink.transport.MultiplexTransportConfig
import java.util.*


class SdlService : Service() {

    companion object {
        const val LOG_TAG = "SdlService"

        private const val CHANNEL_NAME = "Sdl Notifications channel"

        private const val CHANNEL_DESCRIPTION = "Sdl details Information"

        private const val NOTIFICATION_NAME = "Sdl Notification"

        private const val NOTIFICATION_DESCRIPTION = "HMI Service Information"

        private const val ICON_FILENAME = "SDL icon"

        private const val APP_ID = "1234"

        private const val APP_NAME = "Tratata"

        private  const val TCP_PORT = 12345

	    private const val DEV_MACHINE_IP_ADDRESS = "192.168.53.76"
    }

    private val channelId = "notificationChannel" + SdlService::class.java.canonicalName

    private val notificationId = 1

    private lateinit var notificationManager: NotificationManager

    private lateinit var notificationChannel: NotificationChannel

    private val localBinder = LocalBinder()

    private var sdlManager: SdlManager? = null

    private var onRPCNotificationListenerMap: EnumMap<FunctionID, OnRPCNotificationListener> =
        EnumMap(com.smartdevicelink.protocol.enums.FunctionID::class.java)

    private val listener: SdlManagerListener = object : SdlManagerListener {
        override fun onStart() {
            Log.d(LOG_TAG, "onStart SdlManagerListener")
            // After this callback is triggered the SdlManager can be used to interact with
            // the connected SDL session (updating the display, sending RPCs, etc)
        }

        override fun onDestroy() {
            Log.d(LOG_TAG, " onDestroy SdlManagerListener")
            this@SdlService.stopSelf()
        }

        override fun managerShouldUpdateLifecycle(language: Language?): LifecycleConfigurationUpdate? {
            Log.d(LOG_TAG, " managerShouldUpdateLifecycle SdlManagerListener")
            return null
        }

        override fun onError(info: String, e: Exception) {
            Log.d(LOG_TAG, "onError SdlManagerListener")
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationChannel = getNotificationChannel()
            notificationManager.createNotificationChannel(notificationChannel)
            val serviceNotification =  Notification.Builder(this, channelId)
                .setContentTitle(NOTIFICATION_NAME)
                .setContentText(NOTIFICATION_DESCRIPTION)
                .setChannelId(notificationChannel.id)
                .build()
            startForeground(notificationId, serviceNotification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(LOG_TAG, "Received start id $startId: $intent")
        if (sdlManager == null) {
            sdlManager = sdlManagerBuilder().build()
            sdlManager?.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
        sdlManager?.dispose()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (this::notificationManager.isInitialized && this::notificationChannel.isInitialized) {
                notificationManager.deleteNotificationChannel(notificationChannel.id)
            }
            stopForeground(true)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(LOG_TAG, "onBind")
        return localBinder
    }

    private fun sdlManagerBuilder(): SdlManager.Builder {
        val transport = MultiplexTransportConfig(
            this,
            APP_ID,
            MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF
        )
        val appType: Vector<AppHMIType> = Vector()
        appType.add(AppHMIType.DEFAULT)
        val appIcon =
            SdlArtwork(
                ICON_FILENAME, FileType.GRAPHIC_PNG,
                R.mipmap.ic_launcher, true)
        val builder =
            SdlManager.Builder(
                this,
                APP_ID,
                APP_NAME, listener
            )
        builder.setAppTypes(appType)
        builder.setTransportType(transport)
        builder.setAppIcon(appIcon)
        prepareRPCNotificationListeners()
        builder.setRPCNotificationListeners(onRPCNotificationListenerMap)
        return builder
    }

    private fun prepareRPCNotificationListeners() {
        onRPCNotificationListenerMap[FunctionID.ON_HMI_STATUS] =
            object : OnRPCNotificationListener() {
                override fun onNotified(notification: RPCNotification?) {
                    val onHMIStatus = notification as OnHMIStatus
                    Log.d(LOG_TAG, "onNotified$onHMIStatus")
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(): NotificationChannel {
        val channel = NotificationChannel(
            channelId,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description =
            CHANNEL_DESCRIPTION
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(false)
        return channel
    }

    inner class LocalBinder : Binder() {
        val service: SdlService
            get() {
                return this@SdlService
            }
    }
}
