package com.rexosphere.leoconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            // Pass the Activity context, not applicationContext
            // This is required for Credentials API to show the account picker
            AndroidApp(this@MainActivity)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // Note: Preview won't work without a real Context
    // Use the device/emulator to test
}