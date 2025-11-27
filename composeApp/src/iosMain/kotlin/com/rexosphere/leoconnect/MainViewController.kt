package com.rexosphere.leoconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.di.commonModule
import com.rexosphere.leoconnect.di.platformModule
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import org.koin.compose.KoinApplication

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(commonModule, platformModule)
    }) {
        MaterialTheme {
            Navigator(LoginScreen())
        }
    }
}