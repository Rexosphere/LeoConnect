package com.rexosphere.leoconnect

import com.rexosphere.leoconnect.util.ActivityProvider

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.permission.permissionUtil

class MainActivity : ComponentActivity() {
    private val permissionUtil by permissionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()
        installSplashScreen()
        
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ActivityProvider.currentActivity = this

        // Handle notification intents
        NotifierManager.onCreateOrOnNewIntent(intent)

        // Request notification permission (Android 13+)
        permissionUtil.askNotificationPermission()

        setContent {
            // Pass the Activity context, not applicationContext
            // This is required for Credentials API to show the account picker
            AndroidApp(this@MainActivity)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        NotifierManager.onCreateOrOnNewIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityProvider.currentActivity === this) {
            ActivityProvider.currentActivity = null
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // Note: Preview won't work without a real Context
    // Use the device/emulator to test
}