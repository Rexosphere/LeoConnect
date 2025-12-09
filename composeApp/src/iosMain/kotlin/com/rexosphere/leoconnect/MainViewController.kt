package com.rexosphere.leoconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.data.repository.NotificationRepository
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.di.platformModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import com.rexosphere.leoconnect.util.NotificationInitializer
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController {
    val kamelConfig = KamelConfig {
        // Use default configuration
        takeFrom(KamelConfig.Default)
    }

    KoinApplication(application = {
        modules(commonModule, platformModule)
    }) {
        // Initialize notifications
        val notificationRepository = koinInject<NotificationRepository>()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            val initializer = NotificationInitializer(notificationRepository, scope)
            initializer.initialize()
        }

        MaterialTheme {
            CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
                Navigator(LoginScreen())
            }
        }
    }
}