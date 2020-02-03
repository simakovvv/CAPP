package com.harman.vsimakov2.capp.smartDeviceLink

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.harman.vsimakov2.CAPP.smartDeviceLink.SdlService
import com.smartdevicelink.transport.SdlBroadcastReceiver
import com.smartdevicelink.transport.SdlRouterService
import com.smartdevicelink.transport.TransportConstants

class SdlReceiver : SdlBroadcastReceiver() {
    override fun defineLocalSdlRouterClass(): Class<out SdlRouterService?> {
        Log.d(SdlService.LOG_TAG, "defineLocalSdlRouterClass")
        return  com.harman.vsimakov2.CAPP.smartDeviceLink.SdlRouterService::class.java
    }

    override fun onSdlEnabled(
        context: Context,
        intent: Intent
    ) {
        Log.d(SdlService.LOG_TAG, "onSdlEnabled")
        intent.setClass(context, SdlService::class.java)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d(SdlService.LOG_TAG, "onReceive")
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                if (action.equals(
                        TransportConstants.START_ROUTER_SERVICE_ACTION,
                        ignoreCase = true)
                ) {
                    if (intent.getBooleanExtra(
                            RECONNECT_LANG_CHANGE,
                            false
                        )
                    ) {
                        onSdlEnabled(context, intent)
                    }
                }
            }
        }
    }

    companion object {
        const val RECONNECT_LANG_CHANGE = "RECONNECT_LANG_CHANGE"
    }
}