package com.rexosphere.leoconnect

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

class LeoConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize KMPNotifier for Android
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true,
            )
        )
    }
}
