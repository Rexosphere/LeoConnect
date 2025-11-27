package com.rexosphere.leoconnect

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.di.androidPlatformModule
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import com.rexosphere.leoconnect.ui.theme.AppTheme
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import org.koin.compose.KoinApplication

@Composable
fun AndroidApp(context: Context) {
    val kamelConfig = remember {
        KamelConfig {
            // Use default configuration with increased timeouts for image loading
            takeFrom(KamelConfig.Default)
        }
    }

    KoinApplication(application = {
        modules(commonModule, androidPlatformModule(context))
    }) {
        AppTheme {
            CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
                Navigator(LoginScreen())
            }
        }
    }
}
