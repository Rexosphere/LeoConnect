package com.rexosphere.leoconnect

import android.content.Context
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.di.androidPlatformModule
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import com.rexosphere.leoconnect.ui.theme.AppTheme
import org.koin.compose.KoinApplication

@Composable
fun AndroidApp(context: Context) {
    KoinApplication(application = {
        modules(commonModule, androidPlatformModule(context))
    }) {
        AppTheme {
            Navigator(LoginScreen())
        }
    }
}
