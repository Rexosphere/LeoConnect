package com.rexosphere.leoconnect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import com.rexosphere.leoconnect.ui.theme.AppTheme
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Common App composable for non-Android platforms
 * Android uses AndroidApp instead to provide Context
 */
@Composable
@Preview
fun App() {
    val kamelConfig = remember {
        KamelConfig {
            // Use default configuration
            takeFrom(KamelConfig.Default)
        }
    }

    AppTheme {
        CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
            Navigator(LoginScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}