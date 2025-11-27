package com.rexosphere.leoconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.di.platformModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import org.koin.compose.KoinApplication

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LeoConnect",
    ) {
        val kamelConfig = KamelConfig {
            // Use default configuration
            takeFrom(KamelConfig.Default)
        }

        KoinApplication(application = {
            modules(commonModule, platformModule)
        }) {
            MaterialTheme {
                CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
                    Navigator(LoginScreen())
                }
            }
        }
    }
}