package com.rexosphere.leoconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.di.platformModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import org.koin.compose.KoinApplication

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LeoConnect",
    ) {
        KoinApplication(application = {
            modules(commonModule, platformModule)
        }) {
            MaterialTheme {
                Navigator(LoginScreen())
            }
        }
    }
}