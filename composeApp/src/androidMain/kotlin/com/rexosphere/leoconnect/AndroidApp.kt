package com.rexosphere.leoconnect

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.di.androidPlatformModule
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import org.koin.compose.KoinApplication

@Composable
fun AndroidApp(context: Context) {
    KoinApplication(application = {
        modules(commonModule, androidPlatformModule(context))
    }) {
        MaterialTheme {
            Navigator(LoginScreen())
        }
    }
}
